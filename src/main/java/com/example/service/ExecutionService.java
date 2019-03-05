package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.domain.Execution;
import com.example.domain.OrderBook;
import com.example.domain.OrderStatus;
import com.example.exception.APIException;

@Component
public class ExecutionService {

	@Autowired
	OrderService orderService;

	public Execution createExecution(Execution exec) throws APIException {
		
		List<OrderBook> books = orderService.getBooks();
		
		OrderBook book = books.stream()
				.filter(b-> b.getId() == exec.getOrderBookId())
				.findFirst()
				.orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Order Book for given Order Id does not exist")) ;	
		
		if(OrderStatus.OPEN.equals(book.getStatus())) {
			
			throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Order Book for given Order Id is open, executions can't be added");
		
		} else if(! book.getExecList().isEmpty()) {
		
			Execution firstExec = book.getExecList().get(0);
			if(firstExec.getExecPrice() != exec.getExecPrice() ) {
				
				throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Exec price for new exec should be " + firstExec.getExecPrice());
			}
		
		} else { //everything seems to be ok, add the exec to the exec list
			book.addExecution(exec);
			//TODO: Check if exec quantity matches demand, execute all execs
		}
		return exec;
		
	}
}
