package lab6;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class TestLab6 {
	public static void main(String args[]) {
		test1();
		test2();
		test3();
		test4();
	}

	public static void output(String s) {
		System.out.println(s);
	}

	public static void test1() {
		output("Task1");
		Account a1 = new Account(1, 500);
		Account.add100.accept(a1);

		if(a1.balance == 600) {
			output("Success");
		} else {
			output("Fail");
		}
	}

	public static void test2() {
		output("Task1");
		Account a1 = new Account(1, 500);
		Account a2 = new Account(2, -100);
		Account a3 = new Account(3, 12000);

		boolean b1 = Account.checkBound.test(a1);
		boolean b2 = Account.checkBound.test(a2);
		boolean b3 = Account.checkBound.test(a3);
		if(b1 && !b2 && !b3) {
			output("Success");
		} else {
			output("Fail");
		}
	}

	public static void test3() {
		output("Task3");
		Account a1 = new Account(1, 500);
		Consumer<Account> add1000 = Account.maker.make(1000);
		Consumer<Account> sub1000 = Account.maker.make(-1000);
		add1000.accept(a1);
		int b1 = a1.balance;
		sub1000.accept(a1);
		int b2 = a1.balance;

		if(b1 == 1500 && b2 ==500) {
			output("Success");
		} else {
			output("Fail");
		}
	}

	public static void test4() {
		output("Task4");
		int[] arrInt = {100, 200, 300, 400, 500, 600, 500, 400, 300, 700, 200};
		ArrayList<Account> accounts = new ArrayList<>();

		int id = 0;
		for(Integer balance : arrInt) accounts.add(new Account(id++, balance));

		int maxID = Account.getMaxAccountID(accounts);
		if(maxID == 9) {
			output("Success");
		} else {
			output("Fail");
		}


	}

}
