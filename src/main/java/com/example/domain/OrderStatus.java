package com.example.domain;

public enum OrderStatus {
	
	OPEN("open"), CLOSED("closed"), EXECUTED("executed");
	
	private String status;
	
	private OrderStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
