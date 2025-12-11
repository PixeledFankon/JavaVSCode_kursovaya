package banking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepositAppGUI extends JFrame {

    private static final Logger logger = LogManager.getLogger(DepositAppGUI.class);

    private final DepositDatabase db;

    private JTextField bankField;
    private JTextField amountField;
    private JTextField rateField;
    private JTextField termField;
    private JCheckBox capCheck;

    private DefaultTableModel tableModel;

    public DepositAppGUI() {
        super("Deposit Comparison Tool");
        this.db = new DepositDatabase();
        initUI();
        loadDepositsFromCsv("JavaVSCode_kursovaya/deposits.csv");
        logger.info("DepositAppGUI window created");
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        bankField = new JTextField(15);
        amountField = new JTextField(10);
        rateField = new JTextField(10);
        termField = new JTextField(10);
        capCheck = new JCheckBox("Capitalization enabled");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Bank:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bankField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Amount (KZT):"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Interest rate (%):"), gbc);
        gbc.gridx = 1;
        formPanel.add(rateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Term (months):"), gbc);
        gbc.gridx = 1;
        formPanel.add(termField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(capCheck, gbc);
        row++;

        tableModel = new DefaultTableModel(
                new Object[]{"Bank", "Amount (KZT)", "Rate %", "Term (months)", "Capitalization", "Income (KZT)"}, 0
        );
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        JButton addButton = new JButton("Add Deposit");
        JButton sortButton = new JButton("Sort by Income");
        JButton clearButton = new JButton("Clear List");

        addButton.addActionListener(e -> addDeposit());
        sortButton.addActionListener(e -> sortDeposits());
        clearButton.addActionListener(e -> clearDeposits());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(clearButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formPanel, BorderLayout.NORTH);
        getContentPane().add(tableScroll, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDepositToTable(Deposit d) {
        double income = DepositCalculator.calculateIncome(d);
        tableModel.addRow(new Object[]{
                d.getBankName(),
                String.format("%.2f", d.getAmount()),
                String.format("%.2f", d.getInterestRate()),
                d.getTermMonths(),
                d.isWithCapitalization() ? "Yes" : "No",
                String.format("%.2f", income)
        });
    }

    private void loadDepositsFromCsv(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 5) {
                    logger.warn("Incorrect CSV line: " + line);
                    continue;
                }

                String bank = parts[0].trim();
                double amount = Double.parseDouble(parts[1].trim());
                double rate = Double.parseDouble(parts[2].trim());
                int term = Integer.parseInt(parts[3].trim());
                boolean cap = Boolean.parseBoolean(parts[4].trim());

                Deposit d = new Deposit(bank, amount, rate, term, cap);
                db.addDeposit(d);
                addDepositToTable(d);
            }

            logger.info("Initial deposits loaded from CSV file: " + fileName);
        } catch (IOException | NumberFormatException ex) {
            logger.error("Error while loading deposits from CSV: " + fileName, ex);
        }
    }

    private void addDeposit() {
        try {
            String bank = bankField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            double rate = Double.parseDouble(rateField.getText().trim());
            int term = Integer.parseInt(termField.getText().trim());
            boolean cap = capCheck.isSelected();

            if (bank.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter the bank name.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Deposit d = new Deposit(bank, amount, rate, term, cap);
            db.addDeposit(d);

            logger.info("Deposit added: bank=" + bank
                    + ", amount=" + amount
                    + ", rate=" + rate
                    + ", term=" + term
                    + ", capitalization=" + cap);

            addDepositToTable(d);
            clearInputFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Check that amount, rate and term are valid numeric values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.error("Number format error while adding deposit", ex);
        }
    }

    private void sortDeposits() {
        if (db.getAll().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "The list of deposits is empty.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            logger.info("Attempted to sort deposits, but the list is empty");
            return;
        }

        List<Deposit> list = new ArrayList<>(db.getAll());
        Collections.sort(list, new DepositComparator());

        tableModel.setRowCount(0);

        for (Deposit d : list) {
            addDepositToTable(d);
        }

        logger.info("Deposits sorted by income, count=" + list.size());
    }

    private void clearDeposits() {
        db.getAll().clear();
        tableModel.setRowCount(0);
        logger.info("Deposit list cleared by user");
    }

    private void clearInputFields() {
        bankField.setText("");
        amountField.setText("");
        rateField.setText("");
        termField.setText("");
        capCheck.setSelected(false);
    }
}
