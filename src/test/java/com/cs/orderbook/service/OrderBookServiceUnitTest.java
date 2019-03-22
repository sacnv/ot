
package com.cs.orderbook.service;

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

import com.cs.orderbook.domain.BookStats;
import com.cs.orderbook.domain.Execution;
import com.cs.orderbook.domain.Order;
import com.cs.orderbook.domain.OrderBook;
import com.cs.orderbook.domain.OrderBookStatus;
import com.cs.orderbook.domain.OrderType;
import com.cs.orderbook.exception.OTException;
import com.cs.orderbook.request.ExecutionRequest;
import com.cs.orderbook.request.OrderRequest;
import com.cs.orderbook.response.SimpleResponse;

public class OrderBookServiceUnitTest {

    private List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();

    private OrderBookService mockService;

    OrderBook closedMockBook;

    OrderBook mockBook;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        LocalDateTime now = LocalDateTime.now();

        // Add Limit Order with qty 10 with order price 10
        Order mockOrder1 = Mockito.mock(Order.class);
        when(mockOrder1.getId()).thenReturn(1L);
        when(mockOrder1.getOrderQty()).thenReturn(10L);
        when(mockOrder1.getOrderPrice()).thenReturn(new BigDecimal("5"));
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

        Execution mockExec = new Execution(101L, 10L, BigDecimal.TEN);
        List<Execution> execList = new CopyOnWriteArrayList<Execution>();
        execList.add(mockExec);

        mockBook = Mockito.mock(OrderBook.class);
        when(mockBook.getId()).thenReturn(1L);
        when(mockBook.getInstrId()).thenReturn(1L);
        when(mockBook.getStatus()).thenReturn(OrderBookStatus.OPEN);

        OrderBook executedMockBook = Mockito.mock(OrderBook.class);
        when(executedMockBook.getId()).thenReturn(2L);
        when(executedMockBook.getInstrId()).thenReturn(2L);
        when(executedMockBook.getStatus()).thenReturn(OrderBookStatus.EXECUTED);

        closedMockBook = Mockito.mock(OrderBook.class);
        when(closedMockBook.getId()).thenReturn(3L);
        when(closedMockBook.getInstrId()).thenReturn(3L);
        when(closedMockBook.getStatus()).thenReturn(OrderBookStatus.CLOSED);

        List<Order> odList = new CopyOnWriteArrayList<Order>();
        odList.add(mockOrder1);
        odList.add(mockOrder2);
        odList.add(mockOrder3);

        when(mockBook.getOrderList()).thenReturn(odList);
        when(closedMockBook.getOrderList()).thenReturn(odList);
        when(mockBook.getTotalValidDemand()).thenReturn(25L);
        when(closedMockBook.getExecList()).thenReturn(execList);
        when(closedMockBook.getAccumulatedExecQty()).thenReturn(20L);
        when(closedMockBook.getValidOrdersRevereseSortedByAllocationFactor())
            .thenReturn(odList);

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
    public void testGetOrder() {
        Optional<Order> order = mockService.getOrder(1L, 1L);
        assertEquals(order.get().getId(), Long.valueOf(1L));
    }

    @Test
    public void testGetOrderBookForNonExistentOrderId() {
        Optional<Order> order = mockService.getOrder(1L, 200L);
        assertEquals(order, Optional.empty());
    }

    @Test(
            expected = OTException.class)
    public void testGetOrderBookForNonExistentOrderBookId() {
        mockService.getOrder(15L, 1L);
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
        assertEquals(1L, resp.getId().longValue());
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

    @Test(
            expected = OTException.class)
    public void testAddOrderForNonMatchingInstrId() {
        OrderRequest req = new OrderRequest(15L, 10L, OrderType.LIMIT, BigDecimal.TEN);
        mockService.addOrder(1L,req);
    }

    @Test(
            expected = OTException.class)
    public void testAddOrderForNonOpenBook() {
        OrderRequest req = new OrderRequest(1L, 10L, OrderType.LIMIT, BigDecimal.TEN);
        mockService.addOrder(3L,req);
    }

    @Test(
            expected = OTException.class)
    public void testUpdateBookStatusWithNonExistinInstrId() {
        mockService.closeOrderBook(30L);
    }

    @Test(
            expected = OTException.class)
    public void testcloseBookWithNonOpenStatus() {
        mockService.closeOrderBook(2L);
    }

    @Test
    public void testUpdateBookStatusWithClosed() {
        SimpleResponse resp = mockService.closeOrderBook(1L);
        boolean result = resp.getMessage().contains("successfully");
        assertThat(result).isTrue();
    }

    @Test
    public void getBooks() {
        List<OrderBook> books = mockService.getBooks();
        assertNotNull(books.get(0));
    }

    @Test(
            expected = OTException.class)
    public void testGetStatsForNonExistingOrderBookId() {
        mockService.getStats(30L);
    }

    @Test
    public void testGetStatsForExistingOrderBookId() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, 10L);
        mockService.addExecution(3L, exec);
 
        BookStats stats = mockService.getStats(3L);
        assertEquals(3, stats.getTotalOrders().intValue());
        assertEquals(10L, stats.getLimitTable().get(BigDecimal.TEN)
                .longValue());
        assertEquals(10L, stats.getInvalidLimitTable().get(new BigDecimal("5"))
                .longValue());
    }

    @Test
    public void testGetStatsAndCorrectExecPriceForExistingOrderBookId() {
        BookStats stats = mockService.getStats(3L);
        assertEquals(BigDecimal.TEN, stats.getExecPrice());
    }

    @Test
    public void testValidOrdersSortedByDate() {
        BookStats stats = mockService.getStats(1L);
        assertEquals(1L, stats.getEarliestOrder().getId().longValue());
        assertEquals(3L, stats.getLastOrder().getId().longValue());
    }

    @Test(
            expected = OTException.class)
    public void testAddExecWithInvalidOrderBookId() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, 20L);
        mockService.addExecution(20L, exec);
    }

    @Test
    public void testAddExecForClosedBook() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, 10L);
        SimpleResponse resp = mockService.addExecution(3L, exec);
        boolean result = resp.getMessage().contains("successfully");
        assertThat(result).isTrue();
    }

    @Test
    public void testAddExecForClosedBookWithExcessQuantity() {
        ExecutionRequest exec1 = new ExecutionRequest(BigDecimal.TEN, 1000L);
        when(closedMockBook.getTotalExecQty()).thenReturn(1010L);
        when(closedMockBook.getTotalDemand()).thenReturn(20L);
        SimpleResponse resp1 = mockService.addExecution(3L, exec1);
        boolean result1 = resp1.getMessage().contains("successfully");
        assertThat(result1).isTrue();

    }

    @Test(
            expected = OTException.class)
    public void testAddExecForNotClosedBook() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, 10L);
        mockService.addExecution(1L, exec);
    }

    @Test(
            expected = OTException.class)
    public void testAddExecWithInvalidQuantity() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, -10L);
        mockService.addExecution(3L, exec);
    }

    @Test(
            expected = OTException.class)
    public void testAddExecForUnequalPrice() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.ONE, 20L);
        mockService.addExecution(3L, exec);
    }

    @Test
    public void testAddExecAndOrderInvalidation() {
        ExecutionRequest exec = new ExecutionRequest(BigDecimal.TEN, 20L);
        mockService.addExecution(3L, exec);
        assertEquals(false, mockService.getBooks().get(0).getOrderList().get(0).isValid());
    }

}
