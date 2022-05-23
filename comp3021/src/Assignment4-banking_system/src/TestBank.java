import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

class Account {
	private String accountID;
	private Integer balance;
	
	public Account(String accountID, Integer balance) {
		this.accountID = accountID;
		this.balance = balance;
	}
	
	public String getAccountID() {
		return accountID;
	}
	
	public Integer getBalance() {
		return balance;
	}
}

class Transaction {
    String accID1;
    String accID2;
    String operation;
    Integer amount;
    
    public Transaction(String accID1, String accID2, String operation, Integer amount) {
    	this.accID1 = accID1;
    	this.accID2 = accID2;
    	this.operation = operation;
    	this.amount = amount;
    }
    
    public Integer getValChange1() {
    	if (operation.equals("deposit")) {
    		return amount;
    	} else if (operation.equals("withdraw")) {
    		return 0 - amount;
    	} else {
    		return 0 - amount;
    	}
    }
    
    public Integer getValChange2() {
    	assert (operation.equals("transfer"));
    	return amount;
    }
    
    public String toString() {
    	if (operation.equals("deposit")) {
    		return "DEPOSIT " + accID1 + " " + amount; 
    	} else if (operation.equals("withdraw")) {
    		return "WITHDRAW " + accID1 + " " + amount; 
    	} else {
    		return accID1 + " TRANSFER " + amount + " to " + accID2; 
    	}
    }
}




public class TestBank {
	static HashMap<String, Integer> minedMap = new HashMap<String, Integer>();
	
	static String generateRandomName(int len) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		return sb.toString();
	}

	//Random number generated has repeated number!!!!!!
	static HashSet<Integer> set = new HashSet<Integer>();
	static Integer generateRandomInteger(int min, int max) {
		Random rnd = new Random();
		return rnd.nextInt(max - min) + min;
	}
	
	
	static ArrayList<Account> getAccountList(int count) {
		ArrayList<Account> resList = new ArrayList<Account>();
		
		for (int i = 0; i < count; ++i) {
			String accountID = "bankAccountID_" + i;		
			resList.add(new Account(accountID, 1000));
		}
		return resList;
	}
	
	static ArrayList<Transaction> getTransactionList(int count, ArrayList<Account> accList) {
		ArrayList<Transaction> resList = new ArrayList<Transaction>();
		int numAccount = accList.size();
		
		Random rnd = new Random();
		
		for (int i = 0; i < count; ++i) {
			int op = rnd.nextInt(3);
			if (op == 0) { // "deposit"
				Account acc = accList.get(rnd.nextInt(numAccount));
				Integer amount = generateRandomInteger(100, 1000);
				resList.add(new Transaction(acc.getAccountID(), "", "deposit", amount));
			} else if (op == 1) { // "withdraw"
				Account acc = accList.get(rnd.nextInt(numAccount));
				Integer amount = generateRandomInteger(600, 2000);
				resList.add(new Transaction(acc.getAccountID(), "", "withdraw", amount));
			} else {
				assert(op == 2);
				Account acc1 = accList.get(rnd.nextInt(numAccount));
				Account acc2 = accList.get(rnd.nextInt(numAccount));
				
				Integer amount = generateRandomInteger(300, 1500);				
				resList.add(new Transaction(acc1.getAccountID(), acc2.getAccountID(), "transfer", amount));								
			}
		}
		
		return resList;
	}
	
	public static void main(String [] args) {
		test();
	}
	
	static double calculateScore(Bank bank, ArrayList<Account> accList, Set<Transaction> successTransactions, double totalScore) {
	    Map<String, Integer> accountToBalance = new HashMap<String, Integer>();
		for (Account acc : accList) {
			accountToBalance.put(acc.getAccountID(), acc.getBalance());
		}
		
		for (Transaction t : successTransactions) {
			String op = t.operation;
//			System.out.println(t);
			
			if (op.equals("transfer")) {
				assert(!t.accID1.equals(""));
				assert(!t.accID2.equals(""));
				
				accountToBalance.put(t.accID1, accountToBalance.get(t.accID1) + t.getValChange1());
				accountToBalance.put(t.accID2, accountToBalance.get(t.accID2) + t.getValChange2());
			} else { // withdraw or deposit
				accountToBalance.put(t.accID1, accountToBalance.get(t.accID1) + t.getValChange1());
			}
		}
		
		boolean success = true;
		int unmatch = 0;
		for (Map.Entry<String, Integer> e : accountToBalance.entrySet()) {
			int balance1 = e.getValue();
			int balance2 = bank.getBalance(e.getKey());
			if (balance1 != balance2) {
				success = false;
				System.out.println("ERROR: "+ e.getKey() + " balance1: " + balance1 + ", balance2: " + balance2);
				unmatch++;
			}
		}
		
		if (success) {
			return totalScore;
		} else {
			int numEntries = accountToBalance.entrySet().size();
			return totalScore * ((double) (numEntries - unmatch) / (double) numEntries);
		}
	}
	
	static double singleThreadTest(ArrayList<Account> accList, ArrayList<Transaction> transactions, double totalScore) {
		ConcreteBank bank = new ConcreteBank();
		for (Account acc : accList) {
			boolean addRes = bank.addAccount(acc.getAccountID(), acc.getBalance());
			if (!addRes) {
				System.out.println("Adding account fails!");
				return 0.0;
			}
			
			if (!bank.getBalance(acc.getAccountID()).equals(acc.getBalance())) {
				System.out.println("Initing account balance fails!");
				return 0.0;
			}
		}
		
		Set<Transaction> successTransactions = new HashSet<Transaction>();
		
		for (Transaction transaction : transactions) {
			String op = transaction.operation;
			boolean success;
			if (op.equals("deposit")) {
				success = bank.deposit(transaction.accID1, transaction.amount);				
			} else if (op.equals("withdraw")) {
				success = bank.withdraw(transaction.accID1, transaction.amount, 20);				
			} else {				
				assert (op.equals("transfer"));
				success = bank.transfer(transaction.accID1, transaction.accID2, transaction.amount, 20);
			}
			
			if (success) {
				successTransactions.add(transaction);
			}			
		}
		
//		System.out.println("Number of success transactions (single thread): " + successTransactions.size());
		return calculateScore(bank, accList, successTransactions, totalScore);		
	}
	
	static double multiThreadTest(ArrayList<Account> accList, ArrayList<Transaction> transactions, double totalScore) {
		ConcreteBank bank = new ConcreteBank();
		for (Account acc : accList) {
			boolean addRes = bank.addAccount(acc.getAccountID(), acc.getBalance());
			if (!addRes) {
				System.out.println("Adding account fails!");
				return 0.0;
			}
			
			if (!bank.getBalance(acc.getAccountID()).equals(acc.getBalance())) {
				System.out.println("Initing account balance fails!");
				return 0.0;
			}
		}
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		Set<Transaction> successTransactions = new HashSet<Transaction>();
		int numTransactions = transactions.size();			
		for (int i = 0; i < numTransactions; ++i) {
			Transaction transaction = transactions.get(i);			
			String op = transaction.operation;
			
			Thread t;
			if (op.equals("deposit")) {
				t = new Thread(()->{
					try {
						Thread.sleep(generateRandomInteger(10,50));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					boolean success = bank.deposit(transaction.accID1, transaction.amount);

					synchronized (successTransactions) {
						if (success) {
							successTransactions.add(transaction);
						}
					}
					
				});
			} else if (op.equals("withdraw")) {
				t = new Thread(()->{	
					try {
						Thread.sleep(generateRandomInteger(10,50));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					boolean success = bank.withdraw(transaction.accID1, transaction.amount, 20);
					synchronized (successTransactions) {
						if (success) {
							successTransactions.add(transaction);
						}
					}
					
				});
			} else {
				assert (op.equals("transfer"));
				t = new Thread(()->{			
					try {
						Thread.sleep(generateRandomInteger(10,50));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					boolean success = bank.transfer(transaction.accID1, transaction.accID2, transaction.amount, 20);
					synchronized (successTransactions) {
						if (success) {
							successTransactions.add(transaction);
						}
					}
					
				});
				
			}
			
			threads.add(t);
		}
		
		for (Thread t : threads) {
			t.start();
		}
		
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
//		System.out.println("Number of success transactions (multi thread): " + successTransactions.size());
		return calculateScore(bank, accList, successTransactions, totalScore);
	}
	
	static double miningTest(Bank bank, ArrayList<String> accList, double totalScore) {		
		Map<String, Integer> balanceMap = new HashMap<String, Integer>();		
		for (String acc : accList) {
			balanceMap.put(acc, bank.getBalance(acc));
		}
								
		long startTime = System.currentTimeMillis();
		bank.doLottery(accList, (acc) -> {return mine(acc);});
		long endTime = System.currentTimeMillis();
		
		long lotteryTime = endTime - startTime;
		
		System.out.print("Verifying lottery correctness ...");
		int matchCount = 0;
		for (String acc : accList) {
			int beforeBalance = balanceMap.get(acc);
			int curBalance = bank.getBalance(acc);
			int bonus = minedMap.get(acc);
			
			
			if (curBalance - beforeBalance == bonus) {
				++matchCount;
			}						
		}
		
		double matchScore = totalScore / 2;
		if (matchCount != accList.size()) {
			matchScore *= ((double) matchCount) / ((double) accList.size());
		}
				
		System.out.println("done");
		
		System.out.print("Verifying lottery performance (this can take some time) ...");
		startTime = System.currentTimeMillis();
		for (String acc : accList) {
			mine(acc);			
		}
		endTime = System.currentTimeMillis();
		System.out.println("done");
		
		long sequentialTime = endTime - startTime;
		
		double performanceScore = 0.0;
		// 1.5x speed up
		if ((double) sequentialTime / (double) lotteryTime >= 1.5) {
			performanceScore = totalScore / 2;
		} else if (sequentialTime - lotteryTime >= 50) {
			performanceScore = totalScore / 4;
		}
		
		
		return matchScore + performanceScore;		
	}
	
	static int mine(String accName) {
		int bonus = 0;
		try {
			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			
			boolean mined = false;
			long startTime = System.currentTimeMillis();
			Integer idx = 0;
			while (true) {
				String tmpStr = accName + idx.toString();
				
				byte[] encodedhash = digest.digest(
						tmpStr.getBytes(StandardCharsets.UTF_8));
				
				int sz = encodedhash.length;
				byte by1 = encodedhash[sz-1];
				byte by2 = encodedhash[sz-2];
				byte by3 = encodedhash[sz-3];
				if (by1 == 0x7a && by2 == 0x7b && by3 > 0x70) {
					mined = true;
					break;
				}
				
				long curTime = System.currentTimeMillis();
				
				if (curTime - startTime >= 2000) {
					break;
				}
				
				++idx;							
			}
			
			long timetaken = System.currentTimeMillis() - startTime;
			
			if (!mined) {
				bonus = 0;
			} else {
				bonus = 2005 - (int) timetaken;
			}
			
		
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}
		
		synchronized(minedMap) {
			minedMap.put(accName, bonus);
		}
					
		return bonus;
	}
	
	
	static void test() {
		int numAccount = 10;
		ArrayList<Account> accList = getAccountList(numAccount);
		ArrayList<Transaction> transactions = getTransactionList(10*numAccount, accList);
		
		System.out.println("Begin single thread test ...");
		double score1 = singleThreadTest(accList, transactions, 30.0);
		System.out.println("(*) Single thread test finished. You obtain score " + score1 + " out of 30.");
		
		System.out.println("Begin multithread test ...");
		double score2 = multiThreadTest(accList, transactions, 40.0);
		System.out.println("(*) Multithread test finished. You obtain score " + score2 + " out of 40.");
		
		
		ConcreteBank bank = new ConcreteBank();
		for (Account acc : accList) {
			bank.addAccount(acc.getAccountID(), acc.getBalance());
		}
		
		ArrayList<String> accNameList = new ArrayList<String>();
		for (int i = 0; i < numAccount; ++i) {
			accNameList.add(accList.get(i).getAccountID());
		}
		
		System.out.println("Begin lottery test ...");
		double score3 = miningTest(bank, accNameList, 30.0);
		System.out.println("(*) Lottery test finished. You obtain score " + score3 + " out of 30.");
		
		System.out.println("(*) Your final score is: " + (score1 + score2 + score3));
		
	}

}
