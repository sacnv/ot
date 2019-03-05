package com.example.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summarizingLong;

import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.domain.BookStats;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.APIException;
import com.example.exception.ValidationException;

@Component
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	private static List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();
	
	OrderService() {
		super();
	}
	
	OrderService(List<OrderBook> books) {
		super();
		OrderService.books = books;
	}

	public OrderBook updateBookStatus (OrderBook orderBook) throws APIException {

		Long instructionId = orderBook.getInstrId();

		OrderBook book = books.stream()
				.filter(ob -> ob.getInstrId() == instructionId)
				.findAny()
				.orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Order Book not found for book id =" + instructionId));
		
		logger.info("Order books size = " + books.size());
		book.setStatus(orderBook.getStatus());
		return book;
	}
	
	
	public OrderBook createBook(OrderBook orderBook) throws APIException {
		
		OrderBook book = books.stream()
				.filter(ob -> ob.getInstrId() == orderBook.getId())
				.findAny()
				.orElseGet(() -> new OrderBook(orderBook.getId()));
		
		book.setStatus(OrderStatus.OPEN);
		
		books.add(book);
		
		logger.info("Order books size = " + books.size());
		return book;
	}

	public OrderBook getOrderBook(Long bookId) throws APIException {

		OrderBook book = books.stream()
				.filter(ob -> ob.getInstrId() == bookId)
				.findAny()
				.orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Order Book not found for book id =" + bookId));
		
		return book;
	}

	public Order createOrder(Order order) throws APIException {
		
		logger.info("Order books size = " + books.size());
		
		OrderBook book = books.stream()
				.filter(ob -> ob.getInstrId() == order.getInstrId())
				.findAny()
				.orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "No Order book found for provided instrument id"));
		
		if(OrderStatus.CLOSED.equals(book.getStatus())) {
			
			throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Order book for given order is closed");
		
		} else if(OrderStatus.OPEN.equals(book.getStatus()))  {
			
			try {
				
				validateOrder(order);
			
				book.getOrderList().add(order);
			
			}catch (ValidationException ve) {
			
				throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, ve.getMessage());
			}
		}
		
		return order;
	}
	
	public Optional<Order> getOrder(Long orderId, Long instrId) {
		
		List<Order> orders = books.stream()
				.map(book -> book.getOrderList())
				.flatMap(List::stream)
				.filter(o1 -> o1.getId() == orderId && o1.getInstrId() == instrId)
				.collect(Collectors.toList());
		
		if(orders.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(orders.get(0));
	}
	
	public boolean validateOrder(Order order) throws ValidationException {
		
		if(OrderType.LIMIT.equals(order.getType()) && null == order.getOrderPrice()) {
			
			throw new ValidationException("Limit Orders cannot have empty price");
		
		} else if (OrderType.MARKET.equals(order.getType()) && null != order.getOrderPrice()) {
			
			throw new ValidationException("Market Orders should have empty price");
			
		} else if (null == order.getOrderQty() || null == order.getInstrId()) {
			
			throw new ValidationException("Quantity or Instrument id for the order is missing");
		}
		
		return true;
		
	}
	
	public List<OrderBook> getBooks() {
		
		return books;
	}
	
	
	//Long method, will need to be split in smaller methods
	public BookStats getStats(Long orderBookId) throws APIException {
		
		BookStats bookStats = new BookStats();
		
		OrderBook book = books
				.stream()
				.filter(b -> b.getId() == orderBookId)
				.findFirst()
				.orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "OrderBook not found for id " + orderBookId));
		
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
		Map<Long,Long> limitTable =
		orderList
			.stream()
			.filter(od -> od.getType().equals(OrderType.LIMIT) && od.isValid())
			.collect(Collectors.toMap(Order::getOrderPrice, Order::getExecQty));
		
		bookStats.setLimitTable(limitTable);
		
		//Get accumulated exec quantity
		LongSummaryStatistics execStats = 
				orderList
				.stream()
				.collect(summarizingLong(Order::getExecQty));
		
		bookStats.setExecStats(execStats);
		
		//Get Exec Price
		
		if(!book.getExecList().isEmpty()) {
			
			Long execPrice = book.getExecList().get(0).getExecPrice();
			bookStats.setExecPrice(execPrice);
		}
		
		
		
		return bookStats;
	}
}
