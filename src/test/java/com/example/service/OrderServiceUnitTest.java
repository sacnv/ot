package com.example.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.domain.Order;
import com.example.domain.OrderBook;

public class OrderServiceUnitTest {
	
	private static List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();

	OrderService mockService;
	
	@BeforeClass
	public void setup() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    Order mockOrder = Mockito.mock(Order.class);
	    when(mockOrder.getId()).thenReturn(1L);
	    when(mockOrder.getOrderQty()).thenReturn(1L);
	    when(mockOrder.getInstrId()).thenReturn(1L);
	    
	    OrderBook mockBook = Mockito.mock(OrderBook.class);
	    when(mockBook.getId()).thenReturn(1L);
	    when(mockBook.getInstrId()).thenReturn(1L);
	    List<Order> odList = new ArrayList<Order>();
	    odList.add(mockOrder);
	    
	    when(mockBook.getOrderList()).thenReturn(odList);
	    
	    books.add(mockBook);
	    
	    mockService = new OrderService(books);
	}
	
	@AfterClass
	public void tearDown() {
		mockService = null;
	}
	
	@Test
	public final void testGetOrderBook() {
		Optional<Order> order = mockService.getOrder(1L, 1L);
		assertEquals(order.get().getId(), 1L);
	}

	@Test
	public final void testGetOrderBookForMissingOrder() {
		Optional<Order> order = mockService.getOrder(2L, 1L);
		assertEquals(order, Optional.empty());
	}

}
