package com.example.domain;

public class Execution {

	private Long id;
	
	private Long quantity;

	private Long execPrice;
	
	private Long orderBookId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(Long execPrice) {
		this.execPrice = execPrice;
	}

	public Long getOrderBookId() {
		return orderBookId;
	}

	public void setOrderBookId(Long orderBookId) {
		this.orderBookId = orderBookId;
	}
	
	
}
