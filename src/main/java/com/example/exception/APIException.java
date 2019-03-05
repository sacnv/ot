package com.example.exception;

import org.springframework.http.HttpStatus;

public class APIException extends Exception {

	
	private static final long serialVersionUID = 1L;

	private HttpStatus status;
	
	private String shortMessage;

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return shortMessage;
	}

	public void setMessage(String message) {
		this.shortMessage = message;
	}

	public APIException(HttpStatus status, String message) {
		super();
		this.status = status;
		this.shortMessage = message;
	}
	
	
}
