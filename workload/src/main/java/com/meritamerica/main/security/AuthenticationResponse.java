package com.meritamerica.main.security;

import com.meritamerica.main.models.AccountHolder;

public class AuthenticationResponse {
	private final String jwt;
	private final AccountHolder acc;

	public AuthenticationResponse(String jwt, AccountHolder acc) {
		this.jwt = jwt;
		this.acc = acc;
	}

	public String getJwt() {
		return jwt;
	}

	public AccountHolder getAcc() {
		return acc;
	}
	
	
}
