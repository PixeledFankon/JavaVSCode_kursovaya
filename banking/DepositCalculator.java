package banking;

public class DepositCalculator {

    public static double calculateIncome(Deposit deposit) {
        double amount = deposit.getAmount();
        double rate = deposit.getInterestRate() / 100.0;
        int months = deposit.getTermMonths();

        if (deposit.isWithCapitalization()) {
            double monthlyRate = rate / 12.0;
            return amount * Math.pow(1 + monthlyRate, months) - amount;
        } else {
            return amount * rate * months / 12.0;
        }
    }
}
