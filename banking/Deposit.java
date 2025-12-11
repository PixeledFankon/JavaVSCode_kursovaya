package banking;

public class Deposit {

    private String bankName;
    private double amount;
    private double interestRate;
    private int termMonths;
    private boolean withCapitalization;

    public Deposit(String bankName, double amount, double interestRate,
                   int termMonths, boolean withCapitalization) {
        this.bankName = bankName;
        this.amount = amount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.withCapitalization = withCapitalization;
    }

    public String getBankName() {
        return bankName;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public boolean isWithCapitalization() {
        return withCapitalization;
    }

    @Override
    public String toString() {
        return String.format(
                "Банк: %s | Сумма: %.2f | Ставка: %.2f%% | Срок: %d мес | Капитализация: %s",
                bankName,
                amount,
                interestRate,
                termMonths,
                withCapitalization ? "да" : "нет"
        );
    }
}
