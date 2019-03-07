package com.example.service;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.domain.Execution;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.ExecutionRequest;
import com.example.response.SimpleResponse;

@Component
public class ExecutionService {

	@Autowired
	private OrderService orderService;
	
	public ExecutionService() {}
	
	public ExecutionService(OrderService orderService) { //Junit
		this.orderService = orderService;
	}

	public SimpleResponse addExecution(ExecutionRequest exec) { //pass orderbook id
		
		Long createdId = 0L;
		OrderBook book = orderService.getBookFromOrderBooks(exec.getOrderBookId());

		validateExecRequest(book, exec);
		Execution newExec = new Execution(exec);
		createdId = newExec.getId();
			
		synchronized(this) {
			book.addExecution(newExec);
			updateOrderBook(book, newExec);
		}
		
			
		return new SimpleResponse(createdId, "Execution added to order book");
		
	}
	
	private void validateExecRequest(OrderBook book, ExecutionRequest exec) {
		
		
		if(!(OrderStatus.CLOSED == book.getStatus())) { //if status is null, open or executed, throw an exception
			
			throw new OTException("Order Book for given Order Id is not closed, executions cannot be added");
		
		} 
		
		if(! book.getExecList().isEmpty()) { //check also close status 
		
			Execution firstExec = book.getExecList().get(0);  //check for 1st price
			if(firstExec.getExecPrice().compareTo(exec.getExecPrice()) != 0 ) {
				
				throw new OTException("Exec price for new exec should be " + firstExec.getExecPrice());
			}
		
		} 

		if(exec.getExecPrice().compareTo(BigDecimal.ZERO) < 0 || exec.getQuantity() < 0) {
			
			throw new OTException("Execution price or quantity is invalid ");
		}
		
	}
	
	private void updateOrderBook(OrderBook book, Execution exec) {
		Predicate<Order> limitType = order -> (OrderType.LIMIT == order.getType());
		Predicate<Order> lesserPrice = order -> (order.getOrderPrice().compareTo(exec.getExecPrice()) < 0 );
		
		//check if this is the first execution to be added by checking execution list size
		boolean isFirstExec = book.getExecList().size() == 1;
		
		//invalidate orders if this is first execution
		if(isFirstExec) {
			book.getOrderList()
				.stream()
				.filter(limitType.and(lesserPrice))
				.forEach(order -> { order.setValid(false); order.setExecQty(0L);});
		}
		
		//get valid demand
		Long totalDemand = book.getOrderList()
			.stream()
			.filter(order -> order.isValid())
			.mapToLong(Order::getOrderQty)
			.sum();
		
		//calculate allocation factor (order quantity / total demand) and store it on each valid order
		book.getOrderList()
			.stream()
			.filter(order -> order.isValid())
			.forEach(order -> { order.setAllocationFactor((double)order.getOrderQty()/(double)totalDemand);} );
		
		//distribute valid demand
		book.getOrderList()
			.stream()
			.filter(order -> order.isValid())
			.forEach(order -> { order.setExecQty(order.getExecQty() + Math.round(order.getAllocationFactor()*exec.getQuantity()));} );
		
		//check if total exec quantity matches total demand
		
		Long totalExecQty = book.getExecList()
			.stream()
			.mapToLong(Execution::getQuantity)
			.sum();
		
		if(totalDemand == totalExecQty) {
			book.setStatus(OrderStatus.EXECUTED);
		}
	}
 }
