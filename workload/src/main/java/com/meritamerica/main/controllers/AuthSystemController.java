package com.meritamerica.main.controllers;



import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.meritamerica.main.models.AccountHolder;
import com.meritamerica.main.repositories.MyUserRepo;
import com.meritamerica.main.security.AuthenticationRequest;
import com.meritamerica.main.security.AuthenticationResponse;
import com.meritamerica.main.security.JwtUtil;
import com.meritamerica.main.security.MyUserDetailsService;
import com.meritamerica.main.security.Users;
// cors, cross orginal
@CrossOrigin(origins ="*")
@RestController
public class AuthSystemController {
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MyUserRepo myUserRepo;
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello World";
	}
	
	@PostMapping("/authenticate/createUser")
	@ResponseStatus(HttpStatus.CREATED)
	public Users createUser(@RequestBody @Valid Users user) {
		user =  myUserRepo.save(user);
		
		return user;
		
	}
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		/*
		 * 
		 */
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		/*
		 * Use user Details Service to get userDetails
		 */
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);
		
		Users user = myUserRepo.findByUserName(authenticationRequest.getUsername());
		AccountHolder acc = user.getAccountHolder();

		return ResponseEntity.ok(new AuthenticationResponse(jwt, acc));
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
