
package com.example.service;

import static com.example.domain.OrderBookStatus.CLOSED;
import static com.example.domain.OrderBookStatus.EXECUTED;
import static com.example.domain.OrderBookStatus.OPEN;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summarizingLong;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.domain.BookStats;
import com.example.domain.Execution;
import com.example.domain.Order;
import com.example.domain.OrderBook;
import com.example.domain.OrderBookStatus;
import com.example.domain.OrderStatus;
import com.example.domain.OrderType;
import com.example.exception.OTException;
import com.example.request.ExecutionRequest;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;
import com.example.util.OTBigFraction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Component
public class OrderBookService {

    private static final Logger logger = LoggerFactory.getLogger(OrderBookService.class);

    private List<OrderBook> books = new CopyOnWriteArrayList<>();

    OrderBookService() {
        super();
    }

    OrderBookService(List<OrderBook> books) { // for Junit
        super();
        this.books = books;
    }

    public SimpleResponse closeOrderBook(Long orderBookId) {
        return updateOrderBookStatus(orderBookId, OrderBookStatus.CLOSED);
    }

    public SimpleResponse updateOrderBookStatus(Long orderBookId, OrderBookStatus newStatus) {

        OrderBook book = getBookFromOrderBooks(orderBookId);
        logger.info("Order books size = {}", books.size());

        OrderBookStatus currentStatus = book.getStatus();
        validateBookStatuses(newStatus, currentStatus);

        synchronized (this) {
            book.setStatus(newStatus);
        }

        return new SimpleResponse(orderBookId,
                "Order book status updated successfully for order book id = " + orderBookId);
    }

    public SimpleResponse createBook(Long instrId) {

        SimpleResponse resp;
        Optional<OrderBook> book =
                books.stream().filter(ob -> ob.getInstrId().equals(instrId)).findFirst();

        if (book.isPresent()) {
            resp = new SimpleResponse(instrId,
                    "Order Book already exists for instr id =" + instrId);
        }
        else {

            OrderBook newBook = new OrderBook(instrId);
            synchronized (this) {
                books.add(newBook);
            }
            resp = new SimpleResponse(instrId, "Order Book created for instr id =" + instrId);
        }

        return resp;
    }

    public SimpleResponse addOrder(Long orderBookId, OrderRequest orderRequest) {

        OrderBook book = getBookFromOrderBooks(orderBookId);

        if (!OrderBookStatus.OPEN.equals(book.getStatus())) {
            throw new OTException("Order book for given order id is not open");
        }

        validateOrder(orderRequest);

        Order order = new Order(orderRequest.getOrderQty(), orderRequest.getInstrId(),
                orderRequest.getType(), orderRequest.getOrderPrice());

        synchronized (this) {
            book.getOrderList().add(order);
        }

        return new SimpleResponse(order.getId(), "Order successfully added");
    }

    public Optional<Order> getOrder(Long orderBookId, Long orderId) {

        OrderBook book = getBookFromOrderBooks(orderBookId);
        List<Order> orders = book.getOrderList().stream().filter(o1 -> o1.getId().equals(orderId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orders.get(0));
    }

    private void validateOrder(OrderRequest orderRequest) {

        if (OrderType.LIMIT.equals(orderRequest.getType())
                && null == orderRequest.getOrderPrice()) {
            throw new OTException("Limit Orders cannot have empty price");
        }
        else if (OrderType.MARKET.equals(orderRequest.getType())
                && null != orderRequest.getOrderPrice()) {
            throw new OTException("Market Orders should have empty price");
        }
    }

    public List<OrderBook> getBooks() {

        return books;
    }

    // Long method, will need to be split in smaller methods
    public BookStats getStats(Long orderBookId) {

        logger.info(getJson());
        BookStats bookStats = new BookStats();
        OrderBook book = getBookFromOrderBooks(orderBookId);

        List<Order> orderList = book.getOrderList();

        // Get count of orders, minimum order and max order quantity
        LongSummaryStatistics orderStats =
                orderList.stream().collect(summarizingLong(Order::getOrderQty));

        bookStats.setOrderStats(orderStats);

        // Get earliest and latest order
        Comparator<Order> comparator = Comparator.comparing(Order::getEntryDate);

        Optional<Order> earliest = orderList.stream().min(comparator);
        Optional<Order> latest = orderList.stream().max(comparator);

        bookStats.setEarliest(earliest.orElse(null));
        bookStats.setLatest(latest.orElse(null));

        // Stats based on valid/invalid orders
        Map<Boolean, LongSummaryStatistics> orderStatsByValidity =
                orderList.stream().collect(partitioningBy(Order::isValid,
                        collectingAndThen(summarizingLong(Order::getOrderQty), x -> x)));

        bookStats.setOrderStatsByValidity(orderStatsByValidity);

        // Get table for limit prices and demand per limit price
        Map<BigDecimal, Long> limitTable = orderList.stream()
                .filter(od -> od.getType().equals(OrderType.LIMIT) && od.isValid())
                .collect(toMap(Order::getOrderPrice, Order::getExecQty, (x1, x2) -> (x1 + x2)));

        bookStats.setLimitTable(limitTable);

        // Get accumulated exec quantity
        LongSummaryStatistics execStats =
                book.getExecList().stream().collect(summarizingLong(Execution::getQuantity));

        bookStats.setExecStats(execStats);

        // Get Exec Price

        if (!book.getExecList().isEmpty()) {

            BigDecimal execPrice = book.getExecList().get(0).getExecPrice();
            bookStats.setExecPrice(execPrice);
        }

        return bookStats;
    }

    private void validateBookStatuses(OrderBookStatus newStatus, OrderBookStatus currentStatus) {

        if (!(CLOSED.equals(newStatus)) && !(OPEN.equals(newStatus))) {
            throw new OTException("provided status is invalid should be either open or closed");
        }
        else if (EXECUTED.equals(currentStatus)) {
            throw new OTException("Order Book is already executed, its status cannot be changed");
        }
        else if (CLOSED.equals(currentStatus) && OPEN.equals(newStatus)) {
            throw new OTException("Order Book is already closed, its cannot be opened");
        }
        else if (null == currentStatus && CLOSED.equals(newStatus)) {
            throw new OTException("Order Book is not open");
        }
    }

    public OrderBook getBookFromOrderBooks(Long orderBookId) {

        return getBooks().stream().filter(ob -> ob.getId().equals(orderBookId)).findAny()
                .orElseThrow(
                        () -> new OTException("Order Book not found for book id =" + orderBookId));

    }

    public SimpleResponse addExecution(ExecutionRequest exec) { 

        OrderBook book = getBookFromOrderBooks(exec.getOrderBookId());
        validateExecRequest(book, exec);

        Execution newExec =
                new Execution(exec.getQuantity(), exec.getExecPrice(), exec.getOrderBookId());

        synchronized (this) {
            book.addExecution(newExec);
            updateOrderBook(book, newExec);
        }

        return new SimpleResponse(newExec.getId(), "Execution added successfully to order book");

    }

    private void validateExecRequest(OrderBook book, ExecutionRequest exec) {

        if (OrderBookStatus.CLOSED != book.getStatus()) { // if status is null, open or executed,
                                                          // throw an exception

            throw new OTException(
                    "Order Book for given Order Id is not closed, executions cannot be added");
        }

        if (!book.getExecList().isEmpty()) { // check also close status

            Execution firstExec = book.getExecList().get(0); // check for 1st price
            if (firstExec.getExecPrice().compareTo(exec.getExecPrice()) != 0) {

                throw new OTException(
                        "Exec price for new exec should be " + firstExec.getExecPrice());
            }

        }

        if (exec.getExecPrice().compareTo(BigDecimal.ZERO) < 0 || exec.getQuantity() < 0) {
            throw new OTException("Execution price or quantity is invalid ");
        }

    }

    private void updateOrderBook(OrderBook book, Execution exec) {
        Predicate<Order> limitType = order -> (OrderType.LIMIT == order.getType());
        Predicate<Order> lesserPrice =
                order -> (order.getOrderPrice().compareTo(exec.getExecPrice()) < 0);

        // check if this is the first execution to be added by checking execution list size
        boolean isFirstExec = book.getExecList().size() == 1;

        // invalidate orders if this is first execution
        if (isFirstExec) {
            book.getOrderList().stream().filter(limitType.and(lesserPrice)).forEach(order -> {
                order.setStatus(OrderStatus.INVALID);
                order.setExecQty(0L);
            });
        }

        // get valid demand
        Long totalDemand = book.getOrderList().stream().filter(Order::isValid)
                .mapToLong(Order::getOrderQty).sum();

        // calculate allocation factor (order quantity / total demand) and store it on each valid
        // order
        book.getOrderList().stream().filter(Order::isValid).forEach(order -> order
                .setAllocationFactor(new OTBigFraction(order.getOrderQty(), totalDemand)));

        // distribute valid demand
        book.getOrderList().stream().filter(Order::isValid)
                .forEach(order -> order.setExecQty(order.getExecQty()
                        + (order.getAllocationFactor().multiply(exec.getQuantity())).longValue()));

        // check if total exec quantity matches total demand

        Long totalExecQty = book.getExecList().stream().mapToLong(Execution::getQuantity).sum();

        if (totalDemand.equals(totalExecQty)) {

            //Recalculate the execution quantity to accurately allocate execution quantity
            book.getOrderList().stream().filter(Order::isValid).forEach(order -> order
                    .setExecQty((order.getAllocationFactor().multiply(totalExecQty)).longValue()));

            book.setStatus(OrderBookStatus.EXECUTED);
        }
    }

    public String getJson() {

        String json = null;
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            json = ow.writeValueAsString(books);
        }
        catch (IOException e) {
            logger.error("There was an error converting order book array to json {}",
                    e.getMessage());
        }

        return json;
    }

}
