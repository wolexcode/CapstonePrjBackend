package com.meritamerica.main.models;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MeritBank {
	private static AccountHolder[] accountHolders = new AccountHolder[10];
	private static CDOffering[] CDOfferings = new CDOffering[0];

	private static int numbOfAccountHolder = 0;
	public static FraudQueue fraudQueue = new FraudQueue();

	public static AccountHolder addAccountHolder(AccountHolder accountHolder) {
		MeritBank.numbOfAccountHolder++;

		if (MeritBank.numbOfAccountHolder >= MeritBank.accountHolders.length) {
			AccountHolder[] accounts = Arrays.copyOf(MeritBank.accountHolders, MeritBank.accountHolders.length * 2);
			MeritBank.accountHolders = accounts;
		}

		MeritBank.accountHolders[MeritBank.numbOfAccountHolder - 1] = accountHolder;	

		return accountHolder;
	}

	public static void addCDOffering(CDOffering offering) {
		CDOffering[] offerings = Arrays.copyOf(MeritBank.CDOfferings, MeritBank.CDOfferings.length + 1);
		offerings[offerings.length - 1] = offering;
		MeritBank.CDOfferings = offerings;
	}

	public static AccountHolder getAccountHolder(long id) {
		for (AccountHolder account : MeritBank.accountHolders) {
			if (account == null) {
				return null;
			}
			if (account.getId() == id) {
				return account;
			}
		}

		return null;


	}
	public static BankAccount findAccount(long ID) {
		if (accountHolders != null) {
			for (int i = 0; i < accountHolders.length; i++) {
				if (accountHolders[i] == null) {
					break;
				}
				BankAccount acc = accountHolders[i].findAccount(ID);
				if (acc != null) {
					return acc;
				}
			}
		}

		return null;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	public static String decimalFormat(double numb) {
		DecimalFormat df = new DecimalFormat("#.####");
		return df.format(numb);
	}

	public static String formatNumber(double d) {
		if(d == (int) d)
			return String.format("%d",(int)d);
		else
			return String.format("%s",d);
	} 

	// sort account from small to large
	public static AccountHolder[] sortAccountHolders() {
		AccountHolder[] accountHolder = MeritBank.accountHolders;

		int n = accountHolder.length; 
		for (int i = 0; i < n-1; i++) {
			for (int j = 0; j < n-i-1; j++) 
				if (accountHolder[j].compareTo(accountHolder[j+1]) > 0) 
				{ 
					// swap accountHolder[j+1] and accountHolder[i] 
					AccountHolder temp = accountHolder[j]; 
					accountHolder[j] = accountHolder[j+1]; 
					accountHolder[j+1] = temp; 
				} 
		}

		return accountHolder;
	}

	public static AccountHolder[] getAccountHolders() {
		AccountHolder[] accounts = Arrays.copyOf(MeritBank.accountHolders, MeritBank.numbOfAccountHolder);
		return accounts;
	}

	public static CDOffering[] getCDOfferings() {
		return CDOfferings;
	}

	public static CDOffering getBestCDOffering(double depositAmount) {
		double highestYield = 0;
		double tempYield = 0;
		int bestIndex = 0; 		// position of the best offerings in the CDOffering array

		// check if the CDOfferings is not null
		if (MeritBank.CDOfferings != null) {
			for (int i=0; i < MeritBank.CDOfferings.length; i++) {
				tempYield = MeritBank.futureValue(depositAmount, CDOfferings[i].getInterestRate(), CDOfferings[i].getTerm());
				if (tempYield > highestYield) {
					highestYield = tempYield;
					bestIndex = i;
				}
			}

			return CDOfferings[bestIndex];
		} else {
			return null;
		}
	}

	public static CDOffering getSecondBestCDOffering(double depositAmount) {

		double highestYield = 0;
		int secondBestI = 0; // second best offer index
		int bestI = 0;
		@SuppressWarnings("unused")
		double secondBestYield = 0;
		double tempYield = 0;

		if (MeritBank.CDOfferings != null) {
			for (int i=0; i < MeritBank.CDOfferings.length; i++) {
				tempYield = MeritBank.futureValue(depositAmount, CDOfferings[i].getInterestRate(), CDOfferings[i].getTerm());
				if (tempYield > highestYield) {

					// let the second best offer take over the old best offer
					secondBestI = bestI;
					secondBestYield = highestYield;

					// the best offer get the new position and value
					highestYield = tempYield;
					bestI = i;

				}
			}

			return CDOfferings[secondBestI];
		} else {
			return null;
		}
	}

	public static void clearCDOfferings() {
		MeritBank.CDOfferings = null;
	}

	public static void setCDOfferings(CDOffering[] offerings) {
		CDOfferings = offerings; 
	}



	public static double totalBalances() {
		double total = 0.0;

		// total all balances (checking and saving) in every account		
		for (int i=0; i < MeritBank.numbOfAccountHolder; i++) {
			total += MeritBank.accountHolders[i].getCheckingBalance() + MeritBank.accountHolders[i].getCheckingBalance();
		}

		return total;
	}

	public static double recursionFutureValue(double amount, int years, double interestRate) {
		if (years == 0) {
			return amount;
		} else {
			return amount * (1 + interestRate) * recursionFutureValue(1, years - 1, interestRate);
		}

	}

	public static double futureValue(double presentValue, double interestRate, int term) {
		double futureVal = presentValue * Math.pow(1 + interestRate, term);

		return futureVal;
	}

	// add transaction to an account
	// -- needed to be fixed, use instanceof, calling bankaccount.withdraw, deposit..etc
	public static boolean processTransaction(Transaction transaction) throws NegativeAmountException, ExceedsFraudSuspicionLimitException, 
	ExceedsAvailableBalanceException {
		double amount = transaction.getAmount();
		BankAccount source = transaction.getSourceAccount();
		BankAccount target = transaction.getTargetAccount();

		// if amount > 1000, add to fraud queue
		if (Math.abs(transaction.getAmount()) > 1000) {
			MeritBank.fraudQueue.addTransaction(transaction);
			throw new ExceedsFraudSuspicionLimitException();
		}

		// if amount < 0
		if (transaction.getAmount() < 0) {
			throw new NegativeAmountException();
		}

		// deposit transaction
		if (transaction instanceof DepositTransaction) {

			// deposit money into account
			target.deposit(amount);

			// add transaction record
			target.addTransaction(transaction);
		} else if (transaction instanceof WithdrawTransaction) {
			// if withdraw amount larger than balance
			if (transaction.getAmount() + transaction.getTargetAccount().getBalance() < 0 ) {
				throw new ExceedsAvailableBalanceException();
			}

			// withdraw money
			target.withdraw(amount);

			// add transaction record
			transaction.getTargetAccount().addTransaction(transaction);
		} else if (transaction instanceof TransferTransaction) {
			// if transfer money more than source account balance
			if (source.getBalance() - amount  < 0) {
				throw new ExceedsAvailableBalanceException();
			}

			// withdraw money from source account
			source.withdraw(amount);

			// deposit money to target account
			target.deposit(amount);

			// add transaction record to both accounts
			transaction.getSourceAccount().addTransaction(transaction);
			transaction.getTargetAccount().addTransaction(transaction);
		}

		return true;
	}	
}
