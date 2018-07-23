
package com.cg.mypaymentapp.service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListenerFactory;

import com.cg.mypaymentapp.beans.Customer;
import com.cg.mypaymentapp.beans.TransactionHistory;
import com.cg.mypaymentapp.beans.Wallet;
import com.cg.mypaymentapp.exception.InsufficientBalanceException;
import com.cg.mypaymentapp.exception.InvalidInputException;
import com.cg.mypaymentapp.repo.WalletRepo;
import com.cg.mypaymentapp.repo.WalletRepoTransactions;

@Component(value="walletService")
public class WalletServiceImpl implements WalletService
{

	@Autowired
    private WalletRepo repo;

	@Autowired
	private WalletRepoTransactions transactionRepo;
	
	
	

	public Customer createAccount(Customer customer) 
	{
		
		return repo.save(customer);		
	}

	public Customer showBalance(String mobileNo) throws InvalidInputException
	{
		
		
		
		return repo.findOne(mobileNo);
	}

	public Customer fundTransfer(String sourceMobileNo, String targetMobileNo, BigDecimal amount) throws InsufficientBalanceException,InvalidInputException
	{
		
		Customer source=null;
		Customer target=null;
		
			     
			     
			  /*   if(amount.compareTo(new BigDecimal(0)) == 0 )
			    	 throw new InvalidInputException("Enter valid Amount to transfer");*/
		         source=repo.findOne(sourceMobileNo);
		         target=repo.findOne(targetMobileNo);
		         BigDecimal balance=source.getWallet().getBalance().subtract(amount);
		         source.setWallet(new Wallet(balance));
		         repo.save(source);
		         BigDecimal balance1=target.getWallet().getBalance().add(amount);
		         target.setWallet(new Wallet(balance1));
		         repo.save(target);
	             
	             
	            
	             
	             
		if(amount.compareTo(source.getWallet().getBalance()) > 0 )
			throw new InsufficientBalanceException("Insufficient Balance in the account "+sourceMobileNo);
		
		
		/*source=withdrawAmount(sourceMobileNo, amount);
		target=depositAmount(targetMobileNo, amount);*/
		
		
		return source;
	}

	public Customer depositAmount(String mobileNo, BigDecimal amount) throws InvalidInputException
	{
		
		Customer customer=null;
			
		customer=repo.findOne(mobileNo);
		
		
		
		BigDecimal balance=customer.getWallet().getBalance().add(amount);
		customer.setWallet(new Wallet(balance));
		
		repo.save(customer);
		transactionRepo.save(new TransactionHistory(mobileNo,"Rupees "+amount+" is deposited on"+new Date()));
		
		
		
		return customer;
	}

	public Customer withdrawAmount(String mobileNo, BigDecimal amount) throws InsufficientBalanceException,InvalidInputException
	{
		Customer customer=null;
		customer=repo.findOne(mobileNo);		
			
		if(amount.compareTo(customer.getWallet().getBalance()) > 0 )
			throw new InsufficientBalanceException("Insufficient Balance");
		
		BigDecimal balance=customer.getWallet().getBalance().subtract(amount);
		customer.setWallet(new Wallet(balance));
		repo.save(customer);
		transactionRepo.save(new TransactionHistory(mobileNo,"Rupees "+amount+" is withdrawn on"+new Date()));
		
				
	
		return customer;
	
}

	@Override
	public List<TransactionHistory> printTransactions(String mobileNo)
	{
		List<TransactionHistory> transHistory=null;
		
			transHistory=transactionRepo.findByMobileno(mobileNo);
			System.out.println(transHistory);
			if(transHistory == null)
				throw new InvalidInputException("No Transactions Happened ");
			
			
		return transHistory;
	}

	@Override
	public boolean checkAccount(Customer customer) 
	{
		Customer customer1=repo.findOne(customer.getMobileNo());
		if(customer1==null)
			return false;
		return true;
	}



}