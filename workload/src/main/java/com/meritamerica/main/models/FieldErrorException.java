package com.meritamerica.main.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldErrorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FieldErrorException (String msg) {
		super(msg);
	}
}
