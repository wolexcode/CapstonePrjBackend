package com.meritamerica.main.dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import com.meritamerica.main.models.AccountHolder;
import com.meritamerica.main.repositories.AccountHolderRepo;
import com.meritamerica.main.repositories.MyUserRepo;
import com.meritamerica.main.security.Users;

@Service
public class DBinit implements CommandLineRunner {
	@Autowired
	MyUserRepo userRepo;
	
	@Autowired
	AccountHolderRepo accHolderRepo; 
	@Override
	public void run(String... args) throws Exception {
		// Create users
		Users huy = new Users("huycam", "123", "USER_PRIVILEGE");
		new Users("david", "123", "USER_PRIVILEGE");
		new Users("admin", "123", "ADMIN_PRIVILEGE");
		
		// add AccountHolder to user
		AccountHolder huyAccountHolder = new AccountHolder("Huy","", "Cam", "123456789");
//		huyAccountHolder.setUser(huy);
//		huy.setAccountHolder(huyAccountHolder);
		
		huy = userRepo.save(huy);
		huyAccountHolder.setId(huy.getId());
		huyAccountHolder.setEmail("camhuy@email.com");
		huyAccountHolder.setUser(huy);
		accHolderRepo.save(huyAccountHolder);
//		List<Users> users = Arrays.asList(huy, admin, david);
		

		// Save to db
//		huy = users.get(0);
//		huyAccountHolder.setId(huy.getId());
//		huyAccountHolder.setUser(huy);
//		huy.setAccountHolder(huyAccountHolder);
//		
//		this.userRepo.save(huy);
		
	}

}
