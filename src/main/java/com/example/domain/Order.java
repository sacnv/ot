package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.example.request.OrderRequest;
import com.example.util.IDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * This class can be even made shorter in future java version by declaring it as record class
 */

//TODO: Make this class immutable
public class Order {

	private Long id = 0L;
	
	@NotNull
	private Long orderQty;
	
	//Can be changed to Instant, if nanosecond level precision is required
	@JsonIgnore
	private LocalDateTime entryDate;
	
	@NotNull
	private Long instrId;
	
	@NotNull
	private OrderType type;
	
	private boolean valid = true;
	
	@NotNull
	private BigDecimal orderPrice;
	
	private Long execQty;
	
	public double getAllocationFactor() {
		return allocationFactor;
	}

	public void setAllocationFactor(double allocationFactor) {
		this.allocationFactor = allocationFactor;
	}

	private BigDecimal execPrice;
	
	private double allocationFactor;

	
	public Long getOrderQty() {
		return orderQty;
	}

	public LocalDateTime getEntryDate() {
		return entryDate;
	}

	public Order(@NotNull Long orderQty, @NotNull Long instrId, @NotNull OrderType type,
			@NotNull BigDecimal orderPrice) {
		super();
		this.id = IDGenerator.generateId(Order.class);
		this.orderQty = orderQty;
		this.instrId = instrId;
		this.type = type;
		this.orderPrice = orderPrice;
		this.entryDate = LocalDateTime.now();
		this.execQty = 0L;
		this.execPrice = BigDecimal.ZERO;
	}
	
	public Order(OrderRequest orderRequest) {
		super();
		this.id = IDGenerator.generateId(Order.class);
		this.orderQty = orderRequest.getOrderQty();
		this.instrId = orderRequest.getInstrId();
		this.type = orderRequest.getType();
		this.orderPrice = orderRequest.getOrderPrice();
		this.entryDate = LocalDateTime.now();
		this.execQty = 0L;
		this.execPrice = BigDecimal.ZERO;
	}

	public Long getInstrId() {
		return instrId;
	}

	public OrderType getType() {
		return type;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public BigDecimal getOrderPrice() {
		return orderPrice;
	}

	public Long getId() {
		return id;
	}

	public Long getExecQty() {
		return execQty;
	}

	public void setExecQty(Long execQty) {
		this.execQty = execQty;
	}

	public BigDecimal getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(BigDecimal execPrice) {
		this.execPrice = execPrice;
	}

	
	
}
