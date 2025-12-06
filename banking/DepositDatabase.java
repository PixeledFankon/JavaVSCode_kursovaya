package banking;

import java.util.ArrayList;
import java.util.List;

public class DepositDatabase {
    
    private List<Deposit> deposits = new ArrayList<Deposit>();

    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
    }

    public List<Deposit> getAll() {
        return deposits;
    }
}
