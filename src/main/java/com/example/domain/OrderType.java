package com.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {

	LIMIT("limit"), MARKET("market");
	
	private String type;
	
	private OrderType(String type) {
		this.setType(type);
	}

	@JsonValue
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
