package com.example.domain;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * This class can be even made shorter in future java version by declaring it as record class
 */

//TODO: Make this class immutable
public class Order {

	private long id;
	
	@NotNull
	private Long orderQty;
	
	//Can be changed to Instant, if nanosecond level precision is required
	@JsonIgnore
	private LocalDateTime entryDate;
	
	@NotNull
	private Long instrId;
	
	@NotNull
	private OrderType type;
	
	private boolean valid;
	
	@NotNull
	private Long orderPrice;
	
	private Long execQty;
	
	private Long execPrice;

	
	public Long getOrderQty() {
		return orderQty;
	}

//	public void setOrderQty(Long quantity) {
//		this.orderQty = quantity;
//	}

	public LocalDateTime getEntryDate() {
		return entryDate;
	}

	public Order(long id, @NotNull Long orderQty, @NotNull Long instrId, @NotNull OrderType type,
			@NotNull Long orderPrice) {
		super();
		this.id = id;
		this.orderQty = orderQty;
		this.instrId = instrId;
		this.type = type;
		this.orderPrice = orderPrice;
		this.entryDate = LocalDateTime.now();
		this.execQty = 0L;
		this.execPrice = 0L;
	}
//	public void setEntryDate(LocalDateTime entryDate) {
//		this.entryDate = entryDate;
//	}

	public Long getInstrId() {
		return instrId;
	}

//	public void setInstrId(Long instrId) {
//		this.instrId = instrId;
//	}

	public OrderType getType() {
		return type;
	}

//	public void setType(OrderType type) {
//		this.type = type;
//	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Long getOrderPrice() {
		return orderPrice;
	}

//	public void setOrderPrice(Long price) {
//		this.orderPrice = price;
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getExecQty() {
		return execQty;
	}

	public void setExecQty(Long execQty) {
		this.execQty = execQty;
	}

	public Long getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(Long execPrice) {
		this.execPrice = execPrice;
	}

	
	
}
