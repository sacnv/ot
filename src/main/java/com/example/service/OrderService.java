package com.example.service;

import static com.example.domain.OrderStatus.CLOSED;
import static com.example.domain.OrderStatus.EXECUTED;
import static com.example.domain.OrderStatus.OPEN;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summarizingLong;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.domain.BookStats;
import com.example.domain.Execution;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderBooks;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;

@Component
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	private static List<OrderBook> books = OrderBooks.getBooks();
	
	OrderService() {
		super();
	}
	
	OrderService(List<OrderBook> books) { //for Junit
		super();
		OrderService.books = books;
	}

	public SimpleResponse updateBookStatus (Long instructionId, OrderStatus newStatus) {

		OrderBook book = getBookFromOrderBooks(instructionId);
		
		logger.info("Order books size = " + books.size());
		
		OrderStatus currentStatus = book.getStatus();
		
		validateBookStatuses(newStatus, currentStatus); 
		
		synchronized (this) {
			book.setStatus(newStatus);
		}
		
		SimpleResponse response = new SimpleResponse(instructionId, "Order book status updated for instr id = " + instructionId);
		return response;
	}
	
	
	public SimpleResponse createBook(Long instrId) {
		
		SimpleResponse resp;
		Optional<OrderBook> book = books.stream()
				.filter(ob -> ob.getInstrId() == instrId)
				.findFirst();
		
		if(book.isPresent()) {
			resp = new SimpleResponse(instrId, "Order Book already exists for instr id =" + instrId);
		} else {
		    
			OrderBook newBook = new OrderBook(instrId);
			synchronized (this) {
				books.add(newBook);
			}
			resp = new SimpleResponse(instrId, "Order Book created for instr id =" + instrId);
		}
		
		return resp;
	}

	public SimpleResponse addOrder(OrderRequest orderRequest) { 
		
		Long createdId = 0L;
		
		OrderBook book = getBookFromOrderBooks(orderRequest.getInstrId());
		
		if(! OrderStatus.OPEN.equals(book.getStatus())) {
			
			throw new OTException( "Order book for given order id is not open");
		
		} else if(OrderStatus.OPEN.equals(book.getStatus()))  {
			
				validateOrder(orderRequest); 
			
				Order order = new Order(orderRequest);
				createdId = order.getId();
				synchronized (this) {
					book.getOrderList().add(order);
				}
		}
		
		return new SimpleResponse(createdId, "Order successfully added");
	}
	
	public Optional<Order> getOrder(Long orderId, Long instrId) {
		
		OrderBook book = getBookFromOrderBooks(instrId);
		List<Order> orders = book.getOrderList()
				.stream()
				.filter(o1 -> o1.getId() == orderId)
				.collect(Collectors.toList());
		
		if(orders.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(orders.get(0));
	}
	
	private void validateOrder(OrderRequest orderRequest) {
		
		if(OrderType.LIMIT.equals(orderRequest.getType()) && null == orderRequest.getOrderPrice()) {
			
			throw new OTException("Limit Orders cannot have empty price");
		
		} else if (OrderType.MARKET.equals(orderRequest.getType()) && null != orderRequest.getOrderPrice()) {
			
			throw new OTException("Market Orders should have empty price");
			
		} 
		
	}
	
	public List<OrderBook> getBooks() {
		
		return books;
	}
	
	
	//Long method, will need to be split in smaller methods
	public BookStats getStats(Long instrId) {
		
		BookStats bookStats = new BookStats();
		OrderBook book = getBookFromOrderBooks(instrId);
		
		List<Order> orderList = book.getOrderList();
		
		//Get count of orders, minimum order and max order quantity
		LongSummaryStatistics orderStats = 
			orderList
			.stream()
			.collect(summarizingLong(Order::getOrderQty));
		
		bookStats.setOrderStats(orderStats);
		 
		//Get earliest and latest order 
		Comparator<Order> comparator = Comparator.comparing(Order::getEntryDate);
		 
		Order earliest = orderList.stream().min(comparator).get();
		Order latest   = orderList.stream().max(comparator).get();
		 
		bookStats.setEarliest(earliest);
		bookStats.setLatest(latest);
		
		//Stats based on valid/invalid orders 
		Map<Boolean, LongSummaryStatistics> orderStatsByValidity = 
			orderList
				.stream()
				.collect(partitioningBy(Order::isValid, collectingAndThen(summarizingLong(Order::getOrderQty), x -> x)));
		
		bookStats.setOrderStatsByValidity(orderStatsByValidity);
		
		//Get table for limit prices and demand per limit price
		Map<BigDecimal,Long> limitTable =
		orderList
			.stream()
			.filter(od -> od.getType().equals(OrderType.LIMIT) && od.isValid())
			.collect(Collectors.toMap(Order::getOrderPrice, Order::getExecQty));
		
		bookStats.setLimitTable(limitTable);
		
		//Get accumulated exec quantity
		LongSummaryStatistics execStats = 
				 book.getExecList()
				.stream()
				.collect(summarizingLong(Execution::getQuantity));
		
		bookStats.setExecStats(execStats);
		
		//Get Exec Price
		
		if(!book.getExecList().isEmpty()) {
			
			BigDecimal execPrice = book.getExecList().get(0).getExecPrice();
			bookStats.setExecPrice(execPrice);
		}
		
		
		
		return bookStats;
	}
	
	private void validateBookStatuses(OrderStatus newStatus, OrderStatus currentStatus) {
		
		if(!(CLOSED == (newStatus)) && ! (OPEN.equals(newStatus))) {
			
			throw new OTException("provided status is invalid should be either open or closed");
		
		} else if(EXECUTED.equals(currentStatus)) {
		
			throw new OTException("Order Book is already executed, its status cannot be changed");
		
		} else if(CLOSED.equals(currentStatus) & OPEN.equals(newStatus)) {
		
			throw new OTException("Order Book is already closed, its cannot be opened");
		
		} else if( null == currentStatus & CLOSED.equals(newStatus)) {
		
			throw new OTException("Order Book is not open");
		}
	}
	
	public OrderBook getBookFromOrderBooks(Long instructionId) {
		
		OrderBook book = getBooks().stream()
				.filter(ob -> ob.getInstrId() == instructionId)
				.findAny()
				.orElseThrow(() -> new OTException("Order Book not found for book id =" + instructionId));
		
		return book;
		
	}
}
