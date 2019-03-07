package com.example.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.BookStats;
import com.example.domain.Order;
import com.example.domain.OrderStatus;
import com.example.request.ExecutionRequest;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;
import com.example.service.ExecutionService;
import com.example.service.OrderService;
import com.example.trade.ApplicationLiterals;

import io.swagger.annotations.ApiOperation;

@Component
@RestController
public class OrderBookController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ExecutionService execService;

	private static final Logger logger = LoggerFactory.getLogger(OrderBookController.class);
	
	
	@ApiOperation(value = ApplicationLiterals.CREATE_BOOK)
	
	@PostMapping(value="/OrderBook", produces = MediaType.APPLICATION_JSON_VALUE)
	
	public SimpleResponse CreateBook(@RequestParam("instrId") Long instrId) {
		
		SimpleResponse resp = orderService.createBook(instrId);
		
		return resp;
		
	}
	
	@ApiOperation(value = ApplicationLiterals.OPEN_BOOK)
	
	@PutMapping(value="/OrderBook/{instrId}/open")
	
	public SimpleResponse openOrderBook(@PathVariable("instrId") Long instrId) {
		logger.debug("Request received");
		
		SimpleResponse resp = orderService.updateBookStatus(instrId, OrderStatus.OPEN);
		
		return resp;
		
	}
	
	
	@ApiOperation(value = ApplicationLiterals.CLOSE_BOOK)
	
	@PutMapping(value="/OrderBook/{instrId}/close")
	
	public SimpleResponse closeOrderBook(@PathVariable("instrId") Long instrId) {
		logger.debug("Request received");
		
		SimpleResponse resp = orderService.updateBookStatus(instrId, OrderStatus.CLOSED);
		
		return resp;
		
	}

	@ApiOperation(value = ApplicationLiterals.VIEW_STATS)
	
	@GetMapping(value="/OrderBook/{instrId}/stats",  produces = MediaType.APPLICATION_JSON_VALUE)
	public BookStats getOrderBookStats(@PathVariable("instrId") Long instrId) {
		
		BookStats stats = orderService.getStats(instrId);

		return stats;
		
	}
	
	
	@ApiOperation(value = ApplicationLiterals.ADD_EXEC)
	
	@PostMapping(value="/OrderBook/{instrId}/Execution",  produces = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse addExecution(@PathVariable("instrId") Long instrId, @RequestBody ExecutionRequest exec) {
		
		SimpleResponse resp = execService.addExecution(exec);
		return resp;
		
	}
	
	@ApiOperation(value = ApplicationLiterals.ADD_ORDER)
	
	@PostMapping(value="/OrderBook/{instrId}/Order",  produces = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse addOrder(@PathVariable("instrId") Long instrId, @RequestBody OrderRequest orderRequest) {
		
		logger.debug("Request for Order received");
		SimpleResponse resp = orderService.addOrder(orderRequest);

		return resp;
		
	}
	
	@ApiOperation(value = ApplicationLiterals.GET_ORDER)
	
	@GetMapping(value="/OrderBook/{instrId}/Order/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> getOrder(@PathVariable("orderId") Long orderId, @PathVariable("instrId") Long instrId) {
		
		Optional<Order> storedOrder = orderService.getOrder(orderId, instrId);
		
		if(storedOrder.isPresent()) {
			return new ResponseEntity<Order>(storedOrder.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
		}
	}
}
