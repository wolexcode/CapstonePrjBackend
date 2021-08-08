package com.meritamerica.main.security;

public class SecretKeyService {
	//We are creating a Singleton Pattern Secret Key instance for better security
	private static SecretKeyService instance = new SecretKeyService();
	
	private SecretKeyService() {
	}
	
	public static String getSecretKey() {
		return key();
	}
	
	public static String key() {
		return "TeamArrakis";
	}

	public static SecretKeyService getInstance() {
		return instance;
	}

	public static void setInstance(SecretKeyService instance) {
		SecretKeyService.instance = instance;
	}
	
}
