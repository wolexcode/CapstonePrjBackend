package com.meritamerica.main.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountHolderAlreadyExist extends RuntimeException {
	public AccountHolderAlreadyExist(String msg) {
		super(msg);
	}
}
