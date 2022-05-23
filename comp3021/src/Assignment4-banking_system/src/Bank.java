import java.util.ArrayList;

interface Miner {
	public Integer mine(String accName);
}


public interface Bank {
	public boolean addAccount(String accountID, Integer initBalance);
	
	public boolean deposit(String accountID, Integer amount);
	
	public boolean withdraw(String accountID, Integer amount, long timeoutMillis);
	
	public boolean transfer(String srcAccount, 
			                String dstAccount, 
			                Integer amount, 
			                long timeoutMillis);
	
	public Integer getBalance(String accountID);
	
	public void doLottery(ArrayList<String> accounts, Miner miner);
}
