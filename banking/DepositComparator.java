package banking;

import java.util.Comparator;

public class DepositComparator implements Comparator<Deposit> {

    @Override
    public int compare(Deposit d1, Deposit d2) {
        double inc1 = DepositCalculator.calculateIncome(d1);
        double inc2 = DepositCalculator.calculateIncome(d2);
        return Double.compare(inc2, inc1);
    }
}
