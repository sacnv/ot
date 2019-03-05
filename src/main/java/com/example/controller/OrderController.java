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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Order;
import com.example.exception.APIException;
import com.example.service.OrderService;

@Component
@RestController
public class OrderController {
	
	@Autowired
	OrderService orderService;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@PostMapping(value="/Order",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> addOrder(@RequestBody Order order) throws APIException {
		
		logger.debug("Request for Order received");
		Order createdOrder = orderService.createOrder(order);

		return new ResponseEntity<Order>(createdOrder, HttpStatus.OK);
		
	}
	
	@GetMapping(value="/Order/{orderId}/instr/{instrId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> getOrder(@PathVariable("orderId") Long orderId, @PathVariable("instrId") Long instrId) throws APIException {
		
		Optional<Order> storedOrder = orderService.getOrder(orderId, instrId);
		
		if(storedOrder.isPresent()) {
			return new ResponseEntity<Order>(storedOrder.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
		}
	}
}
