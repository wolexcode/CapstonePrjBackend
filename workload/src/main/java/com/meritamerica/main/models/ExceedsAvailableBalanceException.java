package com.meritamerica.main.models;

public class ExceedsAvailableBalanceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceedsAvailableBalanceException() {
		super("Exceed Available Balance Exception");
	}
}
