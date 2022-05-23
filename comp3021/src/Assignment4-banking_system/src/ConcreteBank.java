import java.util.ArrayList;
import java.util.HashMap;

public class ConcreteBank implements Bank {
    // TODO
    public HashMap<String, Integer> accounts = new HashMap<>();

    @Override
    public boolean addAccount(String accountID, Integer initBalance) {
        synchronized (accountID) {
            if (accounts.containsKey(accountID)) {
                return false;
            }
            else {
                accounts.put(accountID, initBalance);
                return true;
            }
        }
    }

    @Override
    public boolean deposit(String accountID, Integer amount) {
        synchronized (accountID) {
            if (!accounts.containsKey(accountID)) {
                return false;
            }
            else {
                accounts.replace(accountID, accounts.get(accountID) + amount);
                accountID.notifyAll();
                return true;
            }
        }
    }

    @Override
    public boolean withdraw(String accountID, Integer amount, long timeoutMillis) {
        synchronized (accountID) {
            if (!accounts.containsKey(accountID)) {
                return false;
            } else if (accounts.get(accountID) >= amount) {
                accounts.replace(accountID, accounts.get(accountID) - amount);
                return true;
            } else {
                try {
                    accountID.wait(timeoutMillis);
                    if (accounts.get(accountID) >= amount) {
                        accounts.replace(accountID, accounts.get(accountID) - amount);
                        return true;
                    } else {
                        return false;
                    }
                } catch (InterruptedException e) {
                    System.out.println(e);
                    return false;
                }
            }
        }
    }

    @Override
    public boolean transfer(String srcAccount, String dstAccount, Integer amount, long timeoutMillis) {
        if(withdraw(srcAccount, amount, timeoutMillis)) {
            if (deposit(dstAccount, amount)) {
                return true;
            }
            else {
                deposit(srcAccount, amount);
                return false;
            }
        }
        return false;
    }

    @Override
    public Integer getBalance(String accountID) {
        synchronized (accountID) {
            if(!accounts.containsKey(accountID)) {
                return 0;
            }
            else {
                return accounts.get(accountID);
            }
        }
    }

    @Override
    public void doLottery(ArrayList<String> accounts, Miner miner) {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (String accountID : accounts) {
            Thread t = new Thread(()-> {
                int bonus = miner.mine(accountID);
                deposit(accountID, bonus);
            });
            threads.add(t);
        }
        for (Thread t : threads) {
            t.start();
        }
        try{
            for (Thread t : threads) {
                t.join();
            }
        }
        catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
