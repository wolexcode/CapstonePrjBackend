package com.meritamerica.main.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.meritamerica.main.models.AccountHolder;
import com.meritamerica.main.models.BankAccount;
import com.meritamerica.main.models.CDAccount;
import com.meritamerica.main.models.CheckingAccount;
import com.meritamerica.main.models.ExceedsCombinedBalanceLimitException;
import com.meritamerica.main.models.NegativeAmountException;
import com.meritamerica.main.models.SavingsAccount;
import com.meritamerica.main.services.MyAccountService;


@CrossOrigin(origins ="*", allowedHeaders ="*")
@RequestMapping(value="/Me")
@RestController
public class MyAccountController {
	@Autowired
	MyAccountService accService;
	
	@GetMapping
	public AccountHolder getMyAccountHolder(Principal principal) {
		return accService.getMyAccountHolder(principal);
	}
	
	@CrossOrigin(origins ="*")
	@PostMapping(value = "/AccountHolder")
	@ResponseStatus(HttpStatus.CREATED)
	public AccountHolder createMyAccountHolder(@RequestBody @Valid AccountHolder newAccountHolder, Principal principal) {	
		return  this.accService.createMyAccountHolder(newAccountHolder, principal);
	}
	
	@PostMapping("/CheckingAccounts")
	public CheckingAccount addChecking(@RequestBody @Valid CheckingAccount checking,Principal principal) throws ExceedsCombinedBalanceLimitException {
		return accService.addChecking(checking, principal);
	}
	
	@GetMapping("/CheckingAccounts")
	public List<CheckingAccount> getCheckings(Principal principal) {
		return accService.getCheckings(principal);
	}
	
	@PostMapping("/SavingsAccounts")
	public SavingsAccount addSaving(@RequestBody @Valid SavingsAccount saving,Principal principal) throws ExceedsCombinedBalanceLimitException {
		return accService.addSaving(saving, principal);
	}
	
	@GetMapping("/SavingsAccounts")
	public List<SavingsAccount> getSavings(Principal principal) {
		return accService.getSavings(principal);
	}
	
	@PostMapping("/CDAccounts")
	public CDAccount addCDAccount(@RequestBody @Valid CDAccount cda,Principal principal) throws ExceedsCombinedBalanceLimitException {
		return accService.addCDAccount(cda, principal);
	}
	
	@GetMapping("/CDAccounts")
	public List<CDAccount> getCDAccount(Principal principal) throws ExceedsCombinedBalanceLimitException {
		return accService.getCDAccount(principal);
	}
	
	@PutMapping("/BankAccount/deposit/{id}")
	public BankAccount depositBankAccount(Principal principal, @PathVariable("id") long id, @RequestBody Map<String, Double> json) throws NegativeAmountException {
		return accService.depositBankAccount(principal, id, json.get("amount"));
	}
	
	@PutMapping("/BankAccount/withdraw/{id}")
	public BankAccount withdrawBankAccount(Principal principal, @PathVariable("id") long id, @RequestBody Map<String, Double> json) throws NegativeAmountException {
		return accService.withdrawBankAccount(principal, id, json.get("amount"));
	}
	
	@PutMapping("/BankAccount/transfer/{id}")
	public void transferBankAccount(Principal principal, @PathVariable("id") long originID, @RequestBody Map<String, Double> json) throws NegativeAmountException {
		Double temp = json.get("destID");
		Long destID = temp.longValue();
		accService.transferBankAccount(principal, originID, destID, json.get("amount"));
		
	}
	
}
