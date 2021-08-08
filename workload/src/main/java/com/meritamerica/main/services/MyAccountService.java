package com.meritamerica.main.services;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.meritamerica.main.models.AccountHolder;
import com.meritamerica.main.models.BankAccount;
import com.meritamerica.main.models.CDAccount;
import com.meritamerica.main.models.CheckingAccount;
import com.meritamerica.main.models.ExceedsCombinedBalanceLimitException;
import com.meritamerica.main.models.NegativeAmountException;
import com.meritamerica.main.models.SavingsAccount;
import com.meritamerica.main.repositories.AccountHolderRepo;
import com.meritamerica.main.repositories.CDAccountRepo;
import com.meritamerica.main.repositories.CDOfferRepo;
import com.meritamerica.main.repositories.CheckingAccountRepo;
import com.meritamerica.main.repositories.MyUserRepo;
import com.meritamerica.main.repositories.SavingAccountRepo;
import com.meritamerica.main.security.Users;

@Service
public class MyAccountService {
	@Autowired
	MyUserRepo userRepo;
	
	@Autowired
	CheckingAccountRepo checkingRepo;
	
	@Autowired
	SavingAccountRepo savingRepo;
	
	@Autowired
	CDAccountRepo cdaccRepo;
	
	@Autowired
	CDOfferRepo cdofferingRepo;
	
	@Autowired
	AccountHolderRepo accHolderRepo; 
	
	public Users getUser(String username) {
		return userRepo.findByUserName(username);
	}
	
	public AccountHolder createMyAccountHolder(AccountHolder newAccountHolder, Principal principal) {
		Users user = userRepo.findByUserName(principal.getName());
		newAccountHolder.setUser(user);
		
		newAccountHolder.setId(user.getId());
		
		newAccountHolder =  accHolderRepo.save(newAccountHolder);
		return newAccountHolder;
	}
	
	public AccountHolder getMyAccountHolder(Principal principal) {
		Users user = userRepo.findByUserName(principal.getName());
		AccountHolder acc = user.getAccountHolder();
		return acc;
	}
	
	public CheckingAccount addChecking(CheckingAccount checking,Principal principal) throws ExceedsCombinedBalanceLimitException {
		AccountHolder acc = getMyAccountHolder(principal);
		checking.setAccHolder(acc);
		checking = checkingRepo.save(checking);
		return checking;
	}
	
	public List<CheckingAccount> getCheckings(Principal principal) {
		AccountHolder acc = getMyAccountHolder(principal);
		
		return acc.getCheckingAccounts();
	}
	
	public SavingsAccount addSaving(SavingsAccount saving,Principal principal) throws ExceedsCombinedBalanceLimitException {
		AccountHolder acc = getMyAccountHolder(principal);
		saving.setAccHolder(acc);
		saving = savingRepo.save(saving);
		return saving;
	}
	
	public List<SavingsAccount> getSavings(Principal principal) {
		AccountHolder acc = getMyAccountHolder(principal);
		
		return acc.getSavingsAccounts();
	}
	
	public CDAccount addCDAccount(CDAccount cda,Principal principal) throws ExceedsCombinedBalanceLimitException {
		AccountHolder acc = getMyAccountHolder(principal);
		cda.setAccHolder(acc);
		cda = cdaccRepo.save(cda);
		return cda;
	}
	
	public List<CDAccount> getCDAccount(Principal principal) throws ExceedsCombinedBalanceLimitException {
		AccountHolder acc = getMyAccountHolder(principal);

		return acc.getCDAccounts();
	}
	
	public BankAccount depositBankAccount(Principal principal, long id, double amount) throws NegativeAmountException {
		
		if (amount < 0) {
			throw new NegativeAmountException();
		}
		
		AccountHolder acc = getMyAccountHolder(principal);
		BankAccount bankAccount = this.findAccount(acc, id);
		
		// not allow to deposit to CDAccount
		if (bankAccount instanceof CDAccount) {
			return null;
		}
		
		double newBalance = bankAccount.getBalance() + amount;
		bankAccount.setBalance(newBalance);
		
		return this.saveBankAccount(bankAccount);
	}
	
	public BankAccount withdrawBankAccount(Principal principal, long id, double amount) throws NegativeAmountException {
		if (amount < 0) {
			throw new NegativeAmountException();
		}
		
		AccountHolder acc = getMyAccountHolder(principal);
		BankAccount bankAccount = this.findAccount(acc, id);
		
		// not allow to deposit to CDAccount
		if (bankAccount instanceof CDAccount) {
			return null;
		}
		
		double newBalance = bankAccount.getBalance() - amount;
		bankAccount.setBalance(newBalance);
		return this.saveBankAccount(bankAccount);
	}
	
	public BankAccount transferBankAccount(Principal principal, long originID, long destID, double amount) throws NegativeAmountException {
		if (amount < 0) {
			throw new NegativeAmountException();
		}
		AccountHolder acc = getMyAccountHolder(principal);
		BankAccount originBankAccount = this.findAccount(acc, originID);
		BankAccount destinationBankAccount = this.findAccount(acc, destID);
		if (originBankAccount instanceof CDAccount || destinationBankAccount instanceof CDAccount) {
			return null;
		}
		double newOriginBalance = originBankAccount.getBalance() - amount;
		double newDestBalance = destinationBankAccount.getBalance() + amount;
		originBankAccount.setBalance(newOriginBalance);
		destinationBankAccount.setBalance(newDestBalance);
		this.saveBankAccount(originBankAccount);
		this.saveBankAccount(destinationBankAccount);
		return originBankAccount;
	}
	
	public BankAccount saveBankAccount(BankAccount account) {
		if (account instanceof CheckingAccount) {
			return checkingRepo.save((CheckingAccount) account);
		} else if (account instanceof SavingsAccount) {
			return savingRepo.save((SavingsAccount) account);
		} else if (account instanceof CDAccount) {
			return cdaccRepo.save((CDAccount) account);
		} else {
			return null;
		}
	}
	
	/*
	 * Find an account with id inside a provided account holder
	 */
	public BankAccount findAccount(AccountHolder acc, long ID) {
		List<CheckingAccount> checkings = acc.getCheckingAccounts();
		
		for (int i = 0; i < checkings.size(); i++) {
			if (checkings.get(i).getAccountNumber() == ID) {
				return checkings.get(i);
			}
		}
		
		List<SavingsAccount> savings = acc.getSavingsAccounts();
		for (int j = 0; j < savings.size(); j++) {
			if (savings.get(j).getAccountNumber() == ID) {
				return savings.get(j);
			}
		}
		
		List<CDAccount> cdaccount = acc.getCDAccounts();
		for (int j = 0; j < cdaccount.size(); j++) {
			if (cdaccount.get(j).getAccountNumber() == ID) {
				return cdaccount.get(j);
			}
		}
		
		return null;
	}
	
}
