package com.meritamerica.main.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.meritamerica.main.security.Users;

@Entity
@Table(name="accountholder")
//@JsonIgnoreProperties(value = { "accountHolderContact" })
public class AccountHolder implements Comparable<Object>{ 	
		@Id
//		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name="id")
		private long id;
	    // Class member variables 
		@NotNull(message="First name can not be Null")
		@NotBlank(message="First name must not be empty")
	 	private String firstName;
		private String middleName;
	    @NotNull(message="Last name can not be Null")
	    @NotBlank(message="Last name must not be empty")
	    private String lastName;
	    @NotNull
	    @Size(min=9, message="SNN can not be less than 9 characters")
	    private String ssn;
	    
	    @NotNull(message="email can not be Null")
	    private String email;
	    
	    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accHolder")
	    private List<CheckingAccount> checkingAccounts;
	    
	    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accHolder")
	    private List<SavingsAccount> savingsAccounts;
	    
	    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accHolder")
	    private List<CDAccount> CDAccounts;
	    
	    @OneToOne(cascade = CascadeType.ALL)
		@JoinColumn(name="username")
		@JsonBackReference
	    private Users user;
	    

	    public AccountHolder (){	
	    	// instantiate array of Checkings
	        checkingAccounts = new ArrayList<>();
	        savingsAccounts = new ArrayList<>();
	        CDAccounts = new ArrayList<>();
	    }
	    
	    public AccountHolder(String firstName, String middleName, String lastName, String ssn){
	    	this();
	    	
	        this.firstName = firstName;
	        this.middleName = middleName;
	        this.lastName = lastName;
	        this.ssn = ssn;
	    }
	    
	    public CheckingAccount addCheckingAccount(double openingBalance) throws ExceedsCombinedBalanceLimitException {
	    	CheckingAccount acc = new CheckingAccount(openingBalance);
	    	
	    	return this.addCheckingAccount(acc);
	    }
	            
      /*If combined balance limit is exceeded, throw ExceedsCombinedBalanceLimitException
       * also add a deposit transaction with the opening balance */
	    public CheckingAccount addCheckingAccount(CheckingAccount checkingAccount) throws ExceedsCombinedBalanceLimitException {
	    	// check the opening account condition
	    	if (canOpen(checkingAccount.getBalance())) {
		    	double amount = checkingAccount.getBalance();	
		    	DepositTransaction tran = new DepositTransaction(checkingAccount, amount, new Date() );
		    	checkingAccount.addTransaction(tran);
		    	
		    	this.checkingAccounts.add(checkingAccount);
		    	return checkingAccount;
	    	} else {
	    		throw new ExceedsCombinedBalanceLimitException();
	    	}
	    }
	    
	    public List<CheckingAccount> getCheckingAccounts( ) {
	    	return this.checkingAccounts;
	    }
	    
	    public double getCheckingBalance() {
	    	double total = 0;
	    	for (int i=0; i < this.checkingAccounts.size() ; i++ ) {
	    		total += this.checkingAccounts.get(i).getBalance();
	    	}
	    	
	    	return total;
	    }
	    
	    /*If combined balance limit is exceeded, throw ExceedsCombinedBalanceLimitException
	     * also add a deposit transaction with the opening balance */	    
	    public SavingsAccount addSavingsAccount(double openingBalance) throws ExceedsCombinedBalanceLimitException {
	    	SavingsAccount sav = new SavingsAccount(openingBalance);
	    	return this.addSavingsAccount(sav);
	    }
	    
	    /*If combined balance limit is exceeded, throw ExceedsCombinedBalanceLimitException
	     * also add a deposit transaction with the opening balance */
	    public SavingsAccount addSavingsAccount(SavingsAccount savingsAccount) throws ExceedsCombinedBalanceLimitException{
	    	// check if total amount is greater than 250, 000
	    	if (canOpen(savingsAccount.getBalance())) {		    	
		    	DepositTransaction tran = new DepositTransaction(savingsAccount, savingsAccount.getBalance(), new Date());
		    	savingsAccount.addTransaction(tran);
		    	
		    	this.savingsAccounts.add(savingsAccount);
		    	return savingsAccount;
	    	} else {
	    		throw new ExceedsCombinedBalanceLimitException();
	    	}
	    }
	    
	    public double getSavingsBalance() {
	    	double total = 0;
	    	for (int i=0; i < this.savingsAccounts.size(); i++ ) {
	    		total += this.savingsAccounts.get(i).getBalance();
	    	}
	    	
	    	return total;
	    }
	    
	    // Should also add a deposit transaction with the opening balance
	    public CDAccount addCDAccount(CDOffering offering, double openingBalance) throws ExceedsFraudSuspicionLimitException{   	
	    	CDAccount acc = new CDAccount(offering, openingBalance);
	    	
	    	return this.addCDAccount(acc);
	    }
	    
	    //Should also add a deposit transaction with the opening balance
	    public CDAccount addCDAccount(CDAccount cdAccount) throws ExceedsFraudSuspicionLimitException {
	    	
	    	
	    	// check fraud
	    	DepositTransaction tran = new DepositTransaction(cdAccount, cdAccount.getBalance(), new Date());
	    	
	    	cdAccount.addTransaction(tran);
	    	
	    	
	    	this.CDAccounts.add(cdAccount);
	    	
	    	return cdAccount;
	    }
	    
	    public double getCDBalance() {
	    	double total = 0;
	    	for (int i=0; i < this.CDAccounts.size(); i++ ) {
	    		total += this.CDAccounts.get(i).getBalance();
	    	}
	    	
	    	return total;
	    }
	    
	    public double getCombinedBalance() {
	    	return this.getCDBalance() + this.getCheckingBalance() + this.getSavingsBalance();
	    }
	    
	    // This method validates that the total amount of combined balance and deposit is less than $250,000.00
	    private boolean canOpen(double deposit) throws ExceedsCombinedBalanceLimitException {
	    	if (this.getCombinedBalance() < 250000.00) {
	    		return true;
	    	} else {
	    		System.out.println("Total is over 250,000. Can not open a new account");
	    		throw new ExceedsCombinedBalanceLimitException();
	    	}
	    }

		@Override
		public int compareTo(Object o) {
			AccountHolder acc = (AccountHolder) o;
			if (this.getCombinedBalance() < acc.getCombinedBalance())
				return -1;
			else if (this.getCombinedBalance() > acc.getCombinedBalance())
				return 1;
			else
				return 0;
		}
		
		// find the account has that ID in this account holder and return that account, if can not find, return null
		public BankAccount findAccount(long ID) {
			for (int i = 0; i < this.checkingAccounts.size(); i++) {
				if (this.checkingAccounts.get(i).getAccountNumber() == ID) {
					return this.checkingAccounts.get(i);
				}
			}
			
			for (int j = 0; j < this.savingsAccounts.size(); j++) {
				if (this.savingsAccounts.get(j).getAccountNumber() == ID) {
					return this.savingsAccounts.get(j);
				}
			}
			
			for (int j = 0; j < this.CDAccounts.size(); j++) {
				if (this.CDAccounts.get(j).getAccountNumber() == ID) {
					return this.CDAccounts.get(j);
				}
			}
			
			return null;
		}				
				 
	    public String getFirstName() {
	        return firstName;
	    }
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }
	    public String getMiddleName() {
	        return middleName;
	    }
	    public void setMiddleName(String middleName) {
	        this.middleName = middleName;
	    }
	    public String getLastName() {
	        return lastName;
	    }

		public void setLastname(String lastName) {
	        this.lastName = lastName;
	    }
	    public String getSSN() {
	        return ssn;
	    }
	    public void setSSN(String ssn) {
	        this.ssn = ssn;
	    }
	    
	    public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}
		
	    public List<SavingsAccount> getSavingsAccounts() {
	    	return this.savingsAccounts;
	    }
	    
	    public Users getUser() {
			return user;
		}

		public void setUser(Users user) {
			this.user = user;
		}

		public void setSavingsAccounts(List<SavingsAccount> savingsAccounts) {
			this.savingsAccounts = savingsAccounts;
		}
	    
	    public List<CDAccount> getCDAccounts() {
	    	return this.CDAccounts;
	    }

		public void setCheckingAccounts(List<CheckingAccount> checkingAccounts) {
			this.checkingAccounts = checkingAccounts;
		}

		public void setCDAccounts(List<CDAccount> cDAccounts) {
			CDAccounts = cDAccounts;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		
}