package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.domain.Execution;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.ExecutionRequest;
import com.example.response.SimpleResponse;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class ExecutionServiceUnitTest {

	private OrderService mockService;
	
	private ExecutionService execService;
	
	@Before
	public void setup() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    LocalDateTime now = LocalDateTime.now();
	    
	    //Add Limit Order with qty 10 with order price 10  
	    Order mockOrder1 = Mockito.mock(Order.class);
	    when(mockOrder1.getId()).thenReturn(1L);
	    when(mockOrder1.getOrderQty()).thenReturn(10L);
	    when(mockOrder1.getOrderPrice()).thenReturn(BigDecimal.ONE);
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

	    Execution mockExec = new Execution(101L, 10L, BigDecimal.TEN, 1L);
	    List<Execution> execList = new CopyOnWriteArrayList<Execution>();
	    execList.add(mockExec);
	    
	    OrderBook mockBook = Mockito.mock(OrderBook.class);
	    when(mockBook.getId()).thenReturn(1L);
	    when(mockBook.getInstrId()).thenReturn(1L);
	    when(mockBook.getStatus()).thenReturn(OrderStatus.CLOSED);

	    OrderBook executedMockBook = Mockito.mock(OrderBook.class);
	    when(executedMockBook.getId()).thenReturn(2L);
	    when(executedMockBook.getInstrId()).thenReturn(2L);
	    when(executedMockBook.getStatus()).thenReturn(OrderStatus.EXECUTED);
	    
	    
	    OrderBook closedMockBook = Mockito.mock(OrderBook.class);
	    when(closedMockBook.getId()).thenReturn(3L);
	    when(closedMockBook.getInstrId()).thenReturn(3L);
	    when(closedMockBook.getStatus()).thenReturn(OrderStatus.CLOSED);

	    when(closedMockBook.getExecList()).thenReturn(execList);
	   
	    List<Order> odList =  new CopyOnWriteArrayList<Order>();
	    odList.add(mockOrder1);
	    odList.add(mockOrder2);
	    odList.add(mockOrder3);	    
	    
	    
	    when(mockBook.getOrderList()).thenReturn(odList);
	    
	    List<OrderBook> bookList = new CopyOnWriteArrayList<OrderBook>();
	    bookList.add(mockBook);
	    bookList.add(executedMockBook);
	    bookList.add(closedMockBook);	
//	    books.addAll(bookList);
	    mockService = Mockito.mock(OrderService.class);

	    when(mockService.getBooks()).thenReturn(bookList);
	    when(mockService.getBookFromOrderBooks(1L)).thenReturn(mockBook);
	    when(mockService.getBookFromOrderBooks(20L)).thenReturn(new OrderBook(null, 300L));
	    when(mockService.getBookFromOrderBooks(3L)).thenReturn(closedMockBook);
	    
	    execService = new ExecutionService(mockService);
	}
	
	@After
	public void tearDown() {
		mockService = null;
	}
	
	@Test(expected = OTException.class)
	public void testAddExecWithInvalidOrderId() {
		ExecutionRequest exec = new ExecutionRequest(20L, BigDecimal.TEN, 20L);
		execService.addExecution(exec);
	}
	
	@Test
	public void testAddExecForClosedBook() {
		ExecutionRequest exec = new ExecutionRequest(3L, BigDecimal.TEN, 20L);
		SimpleResponse resp = execService.addExecution(exec);
		assertThat(resp.getMessage().contains("successfully"));
	}
	
	@Test(expected = OTException.class)
	public void testAddExecForUnequalPrice() {
		ExecutionRequest exec = new ExecutionRequest(3L, BigDecimal.ONE, 20L);
		execService.addExecution(exec);
	}
	
	@Test
	public void testAddExecAndOrderInvalidation() {
		ExecutionRequest exec = new ExecutionRequest(1L, BigDecimal.ONE, 20L);
		execService.addExecution(exec);
		assertEquals(mockService.getBooks().get(0).getOrderList().get(0).isValid(), false);
	}
}
