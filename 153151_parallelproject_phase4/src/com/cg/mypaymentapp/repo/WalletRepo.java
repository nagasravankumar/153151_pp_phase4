package com.cg.mypaymentapp.repo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.mypaymentapp.beans.Customer;

public interface WalletRepo extends JpaRepository<Customer, String>{

	
	
	
}
