package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.BookStats;
import com.example.domain.OrderBook;
import com.example.exception.APIException;
import com.example.service.OrderService;
import com.example.trade.GracefulShutdown;

@Component
@RestController
public class OrderBookController {
	
	@Autowired
	OrderService orderService;

	private static final Logger logger = LoggerFactory.getLogger(GracefulShutdown.class);
	
	@PatchMapping(value="/OrderBook")
	public ResponseEntity<OrderBook> updateStatus(@RequestBody OrderBook orderBook) throws APIException {
		logger.debug("Request received");
		
		OrderBook book = orderService.updateBookStatus(orderBook);
		
		return new ResponseEntity<OrderBook>(book, HttpStatus.OK);
		
	}
	
	
	@PostMapping(value="/OrderBook",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderBook> CreateBook(@RequestBody OrderBook orderBook) throws APIException {
		logger.debug("Request received");
		
		OrderBook book = orderService.createBook(orderBook);
		
		return new ResponseEntity<OrderBook>(book, HttpStatus.OK);
		
	}
	
	@GetMapping(value="/OrderBook/{bookId}",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderBook> getOrderBook(@PathVariable("bookId") Long bookId) throws APIException {
		
		OrderBook book = orderService.getOrderBook(bookId);
		
		return new ResponseEntity<OrderBook>(book, HttpStatus.OK);
		
	}
	
	@GetMapping(value="/OrderBook",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookStats> getOrderBookStats(@RequestParam("viewStats") Long orderId) throws APIException {
		
		BookStats stats = orderService.getStats(orderId);

		return new ResponseEntity<BookStats>(stats, HttpStatus.OK);
		
	}
}
