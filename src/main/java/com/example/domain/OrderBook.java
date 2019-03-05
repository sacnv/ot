package com.example.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderBook {
	
	//For simplicity this id will be equal to instruction id
	private long id;
	
	private List<Order> orderList = new CopyOnWriteArrayList<Order>();
	
	private List<Execution> execList = new CopyOnWriteArrayList<Execution>();
	
	private OrderStatus status;
	
	private long instrId;
	
	public OrderBook(long bookId) {
		super();
		this.id = bookId;
		this.instrId = bookId;
	}
	
	public OrderBook(OrderStatus status, long instrId) {
		super();
		this.status = status;
		this.id = instrId;
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

	public long getInstrId() {
		return instrId;
	}

	public void setInstrId(long instrId) {
		this.instrId = instrId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void addExecution(Execution exec) {
		execList.add(exec);
	}

}
