package com.example.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.util.IDGenerator;

public class OrderBook {
	
	private Long id;
	
	private List<Order> orderList = new CopyOnWriteArrayList<Order>();
	
	private List<Execution> execList = new CopyOnWriteArrayList<Execution>();
	
	private OrderStatus status;
	
	private Long instrId;
	
	public OrderBook(Long bookId) {
		super();
		this.id = IDGenerator.generateId(OrderBook.class);
		this.instrId = bookId;
	}
	
	public OrderBook(OrderStatus status, Long instrId) {
		super();
		this.status = status;
		this.id = IDGenerator.generateId(OrderBook.class);
		this.instrId = instrId;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	public List<Execution> getExecList() {
		return execList;
	}

	public void setExecList(List<Execution> execList) {
		this.execList = execList;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Long getInstrId() {
		return instrId;
	}

	public void setInstrId(Long instrId) {
		this.instrId = instrId;
	}

	public Long getId() {
		return id;
	}

	public void addExecution(Execution exec) {
		execList.add(exec);
	}

}
