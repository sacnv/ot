
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.domain.BookStats;
import com.example.domain.Execution;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderBookStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.ExecutionRequest;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;

public class OrderBookServiceUnitTest {

    private List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();

    private OrderBookService mockService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        LocalDateTime now = LocalDateTime.now();

        // Add Limit Order with qty 10 with order price 10
        Order mockOrder1 = Mockito.mock(Order.class);
        when(mockOrder1.getId()).thenReturn(1L);
        when(mockOrder1.getOrderQty()).thenReturn(10L);
        when(mockOrder1.getOrderPrice()).thenReturn(BigDecimal.TEN);
        when(mockOrder1.getInstrId()).thenReturn(1L);
        when(mockOrder1.getType()).thenReturn(OrderType.LIMIT);
        when(mockOrder1.getEntryDate()).thenReturn(now.minusMinutes(3));

        // Add Limit Order with qty 20 with order price 10
        Order mockOrder2 = Mockito.mock(Order.class);
        when(mockOrder2.getId()).thenReturn(2L);
        when(mockOrder2.getOrderQty()).thenReturn(10L);
        when(mockOrder2.getOrderPrice()).thenReturn(BigDecimal.TEN);
        when(mockOrder2.getInstrId()).thenReturn(1L);
        when(mockOrder2.getType()).thenReturn(OrderType.LIMIT);
        when(mockOrder2.getEntryDate()).thenReturn(now.minusMinutes(2));

        // Add Market Order with qty 20
        Order mockOrder3 = Mockito.mock(Order.class);
        when(mockOrder3.getId()).thenReturn(3L);
        when(mockOrder3.getOrderQty()).thenReturn(10L);
        when(mockOrder3.getOrderPrice()).thenReturn(null);
        when(mockOrder3.getInstrId()).thenReturn(1L);
        when(mockOrder3.getType()).thenReturn(OrderType.MARKET);
        when(mockOrder3.getEntryDate()).thenReturn(now.minusMinutes(1));

        Execution mockExec = new Execution(101L, 10L, BigDecimal.TEN, 3L);
        List<Execution> execList = new CopyOnWriteArrayList<Execution>();
        execList.add(mockExec);

        OrderBook mockBook = Mockito.mock(OrderBook.class);
        when(mockBook.getId()).thenReturn(1L);
        when(mockBook.getInstrId()).thenReturn(1L);
        when(mockBook.getStatus()).thenReturn(OrderBookStatus.OPEN);
        // .thenReturn(OrderBookStatus.OPEN)
        // .thenReturn(OrderBookStatus.EXECUTED);

        OrderBook executedMockBook = Mockito.mock(OrderBook.class);
        when(executedMockBook.getId()).thenReturn(2L);
        when(executedMockBook.getInstrId()).thenReturn(2L);
        when(executedMockBook.getStatus()).thenReturn(OrderBookStatus.EXECUTED);

        OrderBook closedMockBook = Mockito.mock(OrderBook.class);
        when(closedMockBook.getId()).thenReturn(3L);
        when(closedMockBook.getInstrId()).thenReturn(3L);
        when(closedMockBook.getStatus()).thenReturn(OrderBookStatus.CLOSED);

        List<Order> odList = new CopyOnWriteArrayList<Order>();
        odList.add(mockOrder1);
        odList.add(mockOrder2);
        odList.add(mockOrder3);

        when(mockBook.getOrderList()).thenReturn(odList);
        when(closedMockBook.getOrderList()).thenReturn(odList);
        when(closedMockBook.getExecList()).thenReturn(execList);

        Collection<OrderBook> bookList = new CopyOnWriteArrayList<OrderBook>();
        bookList.add(mockBook);
        bookList.add(executedMockBook);
        bookList.add(closedMockBook);
        books.addAll(bookList);

        mockService = new OrderBookService(books);
    }

    @After
    public void tearDown() {
        books = new CopyOnWriteArrayList<OrderBook>();
        // mockService = null;
    }

    @Test
    public void testGetOrderBook() {
        Optional<Order> order = mockService.getOrder(1L, 1L);
        assertEquals(order.get().getId(), Long.valueOf(1L));
    }

    @Test
    public void testGetOrderBookForMissingOrder() {
        Optional<Order> order = mockService.getOrder(1L, 200L);
        assertEquals(order, Optional.empty());
    }

    @Test(
            expected = OTException.class)
    public void testAddOrderForMarketOrderWithPrice() {
        OrderRequest req = new OrderRequest(1L, 10L, OrderType.MARKET, BigDecimal.ONE);

        mockService.addOrder(1L,req);
    }

    @Test(
            expected = OTException.class)
    public void testAddOrderForLimitOrderWithoutPrice() {
        OrderRequest req = new OrderRequest(1L, 10L, OrderType.LIMIT, null);

        mockService.addOrder(1L,req);
    }

    @Test
    public void testAddValidOrder() {
        OrderRequest req = new OrderRequest(1L, 10L, OrderType.LIMIT, BigDecimal.TEN);

        SimpleResponse resp = mockService.addOrder(1L,req);
        boolean result = resp.getMessage().contains("successfully");
        assertThat(result).isTrue();
    }

    @Test
    public void testCreateBookWithExistingInstrId() {
        SimpleResponse resp = mockService.createBook(1L);
        boolean result = resp.getMessage().contains("already");
        assertThat(result).isTrue();
    }

    @Test
    public void testCreateBookWithNonExistingInstrId() {
        SimpleResponse resp = mockService.createBook(150L);
        assertEquals(150L, resp.getId().longValue());
    }

    @Test(
            expected = OTException.class)
    public void testUpdateBookStatusWithNonExistinInstrId() {
        mockService.updateOrderBookStatus(30L, OrderBookStatus.CLOSED);
    }

    @Test(
            expected = OTException.class)
    public void testUpdateBookStatusWithExecuted() {
        mockService.updateOrderBookStatus(1L, OrderBookStatus.EXECUTED);
    }

    @Test
    public void testUpdateBookStatusWithClosed() {
        SimpleResponse resp = mockService.updateOrderBookStatus(1L, OrderBookStatus.CLOSED);
        boolean result = resp.getMessage().contains("successfully");
        assertThat(result).isTrue();
    }

    
    @Test(
            expected = OTException.class)
    public void testOpeningExecutedBook() {
        mockService.updateOrderBookStatus(2L, OrderBookStatus.OPEN);
    }

    @Test(
            expected = OTException.class)
    public void testOpeningClosedBook() {
        mockService.updateOrderBookStatus(3L, OrderBookStatus.OPEN);
    }

    @Test
    public void getBooks() {
        List<OrderBook> books = mockService.getBooks();
        assertNotNull(books.get(0));
    }

    @Test(
            expected = OTException.class)
    public void testGetStatsForNonExistingInstrId() {
        mockService.getStats(30L);
    }

    @Test
    public void testGetStatsForExistingInstrId() {
        BookStats stats = mockService.getStats(1L);
        assertEquals(3L, stats.getOrderStats().getCount());
    }

    @Test
    public void testValidOrdersSortedByDate() {
        BookStats stats = mockService.getStats(1L);
        assertEquals(1L, stats.getEarliest().getId().longValue());
        assertEquals(3L, stats.getLatest().getId().longValue());
    }

    @Test(
            expected = OTException.class)
    public void testAddExecWithInvalidOrderId() {
        ExecutionRequest exec = new ExecutionRequest(20L, BigDecimal.TEN, 20L);
        mockService.addExecution(exec);
    }

    @Test
    public void testAddExecForClosedBook() {
        ExecutionRequest exec = new ExecutionRequest(3L, BigDecimal.TEN, 20L);
        SimpleResponse resp = mockService.addExecution(exec);
        boolean result = resp.getMessage().contains("successfully");
        assertThat(result).isTrue();
    }

    @Test(
            expected = OTException.class)
    public void testAddExecForUnequalPrice() {
        ExecutionRequest exec = new ExecutionRequest(3L, BigDecimal.ONE, 20L);
        mockService.addExecution(exec);
    }

    @Test
    public void testAddExecAndOrderInvalidation() {
        ExecutionRequest exec = new ExecutionRequest(3L, BigDecimal.TEN, 20L);
        mockService.addExecution(exec);
        assertEquals(false, mockService.getBooks().get(0).getOrderList().get(0).isValid());
    }
    
}
