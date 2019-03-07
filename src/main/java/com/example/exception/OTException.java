package com.example.exception;

public class OTException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;

	private String shortMessage;

	public String getMessage() {
		return shortMessage;
	}

	public void setMessage(String message) {
		this.shortMessage = message;
	}

	public OTException(String message) {
		super();
		this.shortMessage = message;
	}
	
	
}
