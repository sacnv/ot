package com.example.domain;

import java.math.BigDecimal;

import com.example.request.ExecutionRequest;
import com.example.util.IDGenerator;

public class Execution {

	private Long id = 0L;
	
	private Long quantity;

	private BigDecimal execPrice;
	
	private Long orderBookId;

	public Execution(Long id, Long quantity, BigDecimal execPrice, Long orderBookId) { //Junit
		super();
		this.id = id;
		this.quantity = quantity;
		this.execPrice = execPrice;
		this.orderBookId = orderBookId;
	}
	
	public Execution(ExecutionRequest execRequest) {
		this.id = IDGenerator.generateId(Execution.class);
		this.quantity = execRequest.getQuantity();
		this.orderBookId = execRequest.getOrderBookId();
		this.execPrice = execRequest.getExecPrice();
	}
	
	public Long getId() {
		return id;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(BigDecimal execPrice) {
		this.execPrice = execPrice;
	}

	public Long getOrderBookId() {
		return orderBookId;
	}

	public void setOrderBookId(Long orderBookId) {
		this.orderBookId = orderBookId;
	}
	
}
