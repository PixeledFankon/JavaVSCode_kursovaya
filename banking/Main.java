package banking;

import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Program is starting");

        SwingUtilities.invokeLater(() -> {
            DepositAppGUI app = new DepositAppGUI();
            app.setVisible(true);
            logger.info("Interface was opened :)");
        });
    }
}
