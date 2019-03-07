package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.domain.BookStats;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;

public class OrderServiceUnitTest {
	
	private static List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();

	private OrderService mockService;
	
	@Before
	public void setup() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    LocalDateTime now = LocalDateTime.now();
	    
	    //Add Limit Order with qty 10 with order price 10  
	    Order mockOrder1 = Mockito.mock(Order.class);
	    when(mockOrder1.getId()).thenReturn(1L);
	    when(mockOrder1.getOrderQty()).thenReturn(10L);
	    when(mockOrder1.getOrderPrice()).thenReturn(BigDecimal.TEN);
	    when(mockOrder1.getInstrId()).thenReturn(1L);
	    when(mockOrder1.getType()).thenReturn(OrderType.LIMIT);
	    when(mockOrder1.getEntryDate()).thenReturn(now.minusMinutes(3));

	    //Add Limit Order with qty 20 with order price 10  
	    Order mockOrder2 = Mockito.mock(Order.class);
	    when(mockOrder2.getId()).thenReturn(2L);
	    when(mockOrder2.getOrderQty()).thenReturn(10L);
	    when(mockOrder2.getOrderPrice()).thenReturn(BigDecimal.TEN);
	    when(mockOrder2.getInstrId()).thenReturn(1L);
	    when(mockOrder2.getType()).thenReturn(OrderType.LIMIT);
	    when(mockOrder2.getEntryDate()).thenReturn(now.minusMinutes(2));

	    //Add Market Order with qty 20  
	    Order mockOrder3 = Mockito.mock(Order.class);
	    when(mockOrder3.getId()).thenReturn(3L);
	    when(mockOrder3.getOrderQty()).thenReturn(10L);
	    when(mockOrder3.getOrderPrice()).thenReturn(null);
	    when(mockOrder3.getInstrId()).thenReturn(1L);
	    when(mockOrder3.getType()).thenReturn(OrderType.MARKET);
	    when(mockOrder3.getEntryDate()).thenReturn(now.minusMinutes(1));


	    
	    OrderBook mockBook = Mockito.mock(OrderBook.class);
	    when(mockBook.getId()).thenReturn(1L);
	    when(mockBook.getInstrId()).thenReturn(1L);
	    when(mockBook.getStatus())
	    	.thenReturn(OrderStatus.OPEN);
//	    	.thenReturn(OrderStatus.OPEN)
//	    	.thenReturn(OrderStatus.EXECUTED);

	    OrderBook executedMockBook = Mockito.mock(OrderBook.class);
	    when(executedMockBook.getId()).thenReturn(2L);
	    when(executedMockBook.getInstrId()).thenReturn(2L);
	    when(executedMockBook.getStatus()).thenReturn(OrderStatus.EXECUTED);
	    
	    
	    OrderBook closedMockBook = Mockito.mock(OrderBook.class);
	    when(closedMockBook.getId()).thenReturn(3L);
	    when(closedMockBook.getInstrId()).thenReturn(3L);
	    when(closedMockBook.getStatus()).thenReturn(OrderStatus.CLOSED);

	    
	    List<Order> odList =  new CopyOnWriteArrayList<Order>();
	    odList.add(mockOrder1);
	    odList.add(mockOrder2);
	    odList.add(mockOrder3);	    
	    
	    when(mockBook.getOrderList()).thenReturn(odList);
	    
	    Collection<OrderBook> bookList = new CopyOnWriteArrayList<OrderBook>();
	    bookList.add(mockBook);
	    bookList.add(executedMockBook);
	    bookList.add(closedMockBook);	
	    books.addAll(bookList);
	    
	    mockService = new OrderService(books);
	}
	
	@After
	public void tearDown() {
		books = new CopyOnWriteArrayList<OrderBook>();
		//mockService = null;
	}
	
	@AfterClass
	public static void tearDownAll() {
		books = null;
	}
	
	@Test
	public void testGetOrderBook() {
		Optional<Order> order = mockService.getOrder(1L, 1L);
		assertEquals(order.get().getId(), Long.valueOf(1L));
	}

	@Test
	public void testGetOrderBookForMissingOrder() {
		Optional<Order> order = mockService.getOrder(200L, 1L);
		assertEquals(order, Optional.empty());
	}

	@Test(expected = OTException.class)
	public void testAddOrderForMarketOrderWithPrice() {
		OrderRequest req = new OrderRequest(1L, 10L, OrderType.MARKET, BigDecimal.ONE);
		
		mockService.addOrder(req);
	}

	@Test(expected = OTException.class)
	public void testAddOrderForLimitOrderWithoutPrice() {
		OrderRequest req = new OrderRequest(1L, 10L, OrderType.LIMIT, null);
		
		mockService.addOrder(req);
	}
	
	@Test
	public void testAddValidOrder() {
		OrderRequest req = new OrderRequest(1L, 10L, OrderType.LIMIT, BigDecimal.TEN);
		
		SimpleResponse resp = mockService.addOrder(req);
		assertThat(resp.getMessage().contains("successfully"));
	}
	
	@Test
	public void testCreateBookWithExistingInstrId() {
		SimpleResponse resp = mockService.createBook(1L);
		assertThat(resp.getMessage().contains("already"));
	}
	
	@Test
	public void testCreateBookWithNonExistingInstrId() {
		SimpleResponse resp = mockService.createBook(150L);
		assertEquals(resp.getId().longValue(), 150L);
	}
	
	@Test(expected = OTException.class)
	public void testUpdateBookStatusWithNonExistinInstrId() {
		mockService.updateBookStatus(30L, OrderStatus.CLOSED);
	}
	
	@Test(expected = OTException.class)
	public void testUpdateBookStatusWithExecuted() {
		mockService.updateBookStatus(1L, OrderStatus.EXECUTED);
	}
	
	@Test(expected = OTException.class)
	public void testOpeningExecutedBook() {
		mockService.updateBookStatus(2L, OrderStatus.OPEN);
	}
	
	@Test(expected = OTException.class)
	public void testOpeningClosedBook() {
		mockService.updateBookStatus(3L, OrderStatus.OPEN);
	}
	
	@Test
	public void getBooks() {
		List<OrderBook> books = mockService.getBooks();
		assertNotNull(books.get(0));
	}
	
	@Test(expected = OTException.class)
	public void testGetStatsForNonExistingInstrId() {
		mockService.getStats(30L);
	}
	
	@Test
	public void testGetStatsForExistingInstrId() {
		BookStats stats = mockService.getStats(1L);
		assertEquals(stats.getOrderStats().getCount(), 3L);
	}
	
	@Test
	public void testValidOrdersSortedByDate() {
		BookStats stats = mockService.getStats(1L);
		assertEquals(stats.getEarliest().getId().longValue(), 1L);
		assertEquals(stats.getLatest().getId().longValue(), 3L);
	}
}
