
package com.cs.orderbook.service;

import static com.cs.orderbook.domain.OrderBookStatus.OPEN;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summarizingLong;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cs.orderbook.domain.BookStats;
import com.cs.orderbook.domain.Execution;
import com.cs.orderbook.domain.Order;
import com.cs.orderbook.domain.OrderBook;
import com.cs.orderbook.domain.OrderBookStatus;
import com.cs.orderbook.domain.OrderStatus;
import com.cs.orderbook.domain.OrderType;
import com.cs.orderbook.exception.OTException;
import com.cs.orderbook.request.ExecutionRequest;
import com.cs.orderbook.request.OrderRequest;
import com.cs.orderbook.response.SimpleResponse;
import com.cs.orderbook.util.OTBigFraction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Component
public class OrderBookService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderBookService.class);

    private List<OrderBook> books = new CopyOnWriteArrayList<>();

    OrderBookService() {
    }

    OrderBookService(List<OrderBook> bookList) { // for Junit
        super();
        this.books = bookList;
    }

    public synchronized SimpleResponse closeOrderBook(Long orderBookId) {
        LOGGER.info("updating status {} for book with order book id {}",
                OrderBookStatus.CLOSED, orderBookId);
        OrderBook book = getBookFromOrderBooks(orderBookId);
        OrderBookStatus currentStatus = book.getStatus();

        LOGGER.info("validating new status {} against current status {}",
                OrderBookStatus.CLOSED, currentStatus);
        if (!(OPEN.equals(currentStatus))) {
            throw new OTException(
                 "current order book status should be open");
        }
        book.setStatus(OrderBookStatus.CLOSED);

        return new SimpleResponse(orderBookId,
                "Order book status updated successfully for order book id = "
                        + orderBookId);
    }

    public synchronized SimpleResponse createBook(Long instrId) {
        LOGGER.info("creating book for instr id {}", instrId);
        SimpleResponse resp;
        Optional<OrderBook> book = books.stream()
                .filter(ob -> ob.getInstrId().equals(instrId)).findFirst();

        if (book.isPresent()) {
            resp = new SimpleResponse(instrId,
                    "Order Book already exists for instr id =" + instrId);
        } else {
            OrderBook newBook = new OrderBook(instrId);
            books.add(newBook);
            resp = new SimpleResponse(newBook.getId(),
                    "Order Book created for instr id = " + instrId
                    + ", order book Id = " + newBook.getId());
        }

        return resp;
    }

    public synchronized SimpleResponse addOrder(Long orderBookId,
            OrderRequest orderRequest) {
        LOGGER.info("adding order for book id {}", orderBookId);
        OrderBook book = getBookFromOrderBooks(orderBookId);

        if (!OrderBookStatus.OPEN.equals(book.getStatus())) {
            throw new OTException("Order book for given order id is not open");
        }

        validateOrder(orderRequest);

        Order order =
                new Order(orderRequest.getOrderQty(), orderRequest.getInstrId(),
                        orderRequest.getType(), orderRequest.getOrderPrice());

        book.getOrderList().add(order);
        return new SimpleResponse(order.getId(), "Order successfully added");
    }

    public Optional<Order> getOrder(Long orderBookId, Long orderId) {
        LOGGER.info("getting order with id {} from book id {}",
                orderId, orderBookId);
        OrderBook book = getBookFromOrderBooks(orderBookId);
        List<Order> orders = book.getOrderList().stream()
                .filter(o1 -> o1.getId().equals(orderId))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orders.get(0));
    }

    private void validateOrder(OrderRequest orderRequest) {

        if (OrderType.LIMIT.getType().equalsIgnoreCase(
                (orderRequest.getType().getType()))
                && null == orderRequest.getOrderPrice()) {
            throw new OTException("Limit Orders cannot have empty price");
        } else if (OrderType.MARKET.getType().equals(
                orderRequest.getType().getType())
                && null != orderRequest.getOrderPrice()) {
            throw new OTException("Market Orders should have empty price");
        }
    }

    public List<OrderBook> getBooks() {

        return books;
    }

    public BookStats getStats(Long orderBookId) {

        LOGGER.info(getJson());
        BookStats bookStats = new BookStats();
        OrderBook book = getBookFromOrderBooks(orderBookId);

        List<Order> orderList = book.getOrderList();

        // Get total no. of orders
        bookStats.setTotalOrders(Long.valueOf(orderList.size()));

        setOrdersByComparison(bookStats, orderList);
        setStatsByOrderValidity(bookStats, orderList);

        bookStats.setValidDemand(book.getTotalValidDemand());
        bookStats.setInvalidDemand(book.getTotalInvalidDemand());

        bookStats.setTotalDemand(book.getTotalDemand());
        bookStats.setAccumulatedExecQuantity(book.getAccumulatedExecQty());

        setLimitTables(bookStats, orderList);

        // Get Exec Price
        if (!book.getExecList().isEmpty()) {

            BigDecimal execPrice = book.getExecList().get(0).getExecPrice();
            bookStats.setExecPrice(execPrice);
        }

        return bookStats;
    }

    public OrderBook getBookFromOrderBooks(Long orderBookId) {
        LOGGER.info("get Book for book id {}", orderBookId);
        return getBooks().stream().filter(ob -> ob.getId().equals(orderBookId))
                .findAny().orElseThrow(() -> new OTException(
                        "Order Book not found for book id =" + orderBookId));

    }

    public synchronized SimpleResponse addExecution(ExecutionRequest exec) {
        LOGGER.info("adding execution {}", exec);
        OrderBook book = getBookFromOrderBooks(exec.getOrderBookId());
        validateExecRequest(book, exec);

        Execution newExec = new Execution(exec.getQuantity(),
                exec.getExecPrice(), exec.getOrderBookId());

        book.addExecution(newExec);
        updateOrderBook(book, newExec);

        return new SimpleResponse(newExec.getId(),
                "Execution added successfully to order book");

    }

    private void validateExecRequest(OrderBook book, ExecutionRequest exec) {
        if (OrderBookStatus.CLOSED != book.getStatus()) {
            throw new OTException(
                    "Order Book for given order id is " + book.getStatus()
                    + ", executions cannot be added");
        }

        if (!book.getExecList().isEmpty()) {
         // check for 1st price
            Execution firstExec = book.getExecList().get(0);
            if (firstExec.getExecPrice().compareTo(exec.getExecPrice()) != 0) {
                throw new OTException("Exec price for new exec should be "
                        + firstExec.getExecPrice());
            }
        }

        if (exec.getExecPrice().compareTo(BigDecimal.ZERO) < 0
                || exec.getQuantity() < 0) {
            throw new OTException("Execution price or quantity is invalid ");
        }

    }

    private void updateOrderBook(OrderBook book, Execution exec) {
        LOGGER.info("updating order book after adding execution");
        Predicate<Order> limitType =
                order -> (OrderType.LIMIT == order.getType());
        Predicate<Order> lesserPrice = order -> (order.getOrderPrice()
                .compareTo(exec.getExecPrice()) < 0);
        // check if this is the first execution to be added by checking
        //execution list size
        boolean isFirstExec = book.getExecList().size() == 1;

        // invalidate orders if this is first execution
        if (isFirstExec) {
            book.getOrderList().stream().filter(limitType.and(lesserPrice))
                    .forEach(order -> {
                        order.setStatus(OrderStatus.INVALID);
                        order.setExecQty(0L);
                    });
        }

        //Update exec price for valid orders
        book.getOrderList().stream().filter(Order::isValid)
            .forEach(order -> order.setExecPrice(exec.getExecPrice()));

        // get valid demand
        Long totalValidDemand = book.getTotalValidDemand();

        // check if total exec quantity matches total demand
        Long totalExecQty = book.getTotalExecQty();

        if (totalExecQty.compareTo(totalValidDemand) > 0) {
            //Remove added execution to maintain correct state
            book.removeLastExecution();
            throw new OTException("total execution quantity " + totalExecQty
                    + " cannot exceed total demand " + totalValidDemand);
        }

        // calculate allocation factor (order quantity / total demand)
        // and store it on each valid order
        book.getOrderList().stream().filter(Order::isValid)
            .forEach(order -> order.setAllocationFactor(
                        new OTBigFraction(order.getOrderQty(),
                                totalValidDemand)));

        // distribute valid demand
        book.getOrderList().stream().filter(Order::isValid)
                .forEach(order -> order.setExecQty(
                        (order.getAllocationFactor()
                                .multiply(totalExecQty)).longValue()));

        adjustExecQtyForOrders(book, totalExecQty);
        executeOrders(book, totalValidDemand, totalExecQty);

    }

    private void adjustExecQtyForOrders(OrderBook book,
            Long totalExecQty) {
        // Check if there is still mismatch in accumulated
        // execution on orders and total execution
        Long accumulatedExecQty = book.getAccumulatedExecQty();

        // redistribute the unallocated execution quantity
        //from Larger to smaller orders.
        if (!totalExecQty.equals(accumulatedExecQty)) {
            LOGGER.info("total execution quantity {} does not exactly match"
                    + " accumulated order quantity {}, distributing again",
                    totalExecQty, accumulatedExecQty);
            List<Order> sortedOrders = book.
                    getValidOrdersRevereseSortedByAllocationFactor();

            Long unallocatedExecQty = totalExecQty - accumulatedExecQty;

            LOGGER.info("unallocatedExecQty = {}", unallocatedExecQty);
            ListIterator<Order> iter = sortedOrders.listIterator();
            while (iter.hasNext()) {
                Order order = iter.next();
                Long qtyDiff = order.getOrderQty() - order.getExecQty();
                LOGGER.info("qtyDiff = {}", qtyDiff);
                if (qtyDiff.longValue() != 0
                        && qtyDiff.compareTo(unallocatedExecQty) < 0) {
                    order.setExecQty(order.getExecQty() + qtyDiff);
                    unallocatedExecQty = unallocatedExecQty - qtyDiff;
                    LOGGER.info("Exec Quantity updated to {} for Order Id {},"
                            + " unallocated exec qty is {}",
                            order.getExecQty(), order.getId(),
                            unallocatedExecQty);
                } else if (qtyDiff.longValue() != 0
                        && unallocatedExecQty.compareTo(qtyDiff) <= 0) {
                    order.setExecQty(order.getExecQty() + unallocatedExecQty);
                    unallocatedExecQty = 0L;
                    LOGGER.info("Exec Quantity updated to {} for Order Id {},"
                            + " unallocated exec qty is {}",
                            order.getExecQty(),
                            order.getId(), unallocatedExecQty);

                }
            }
        }
    }

    private void executeOrders(OrderBook book, Long totalDemand,
            Long totalExecQty) {
        LOGGER.info("book id {}, totalDemand {}, totalExecQty {}",
                book.getId(), totalDemand, totalExecQty);
       if (totalDemand.equals(totalExecQty)) {

            LOGGER.info("total execution quantity matches total demand");
            // Recalculate the execution quantity to accurately allocate it
            book.getOrderList().stream().filter(Order::isValid)
                    .forEach(order -> order.setExecQty(
                            (order.getAllocationFactor().multiply(totalExecQty))
                                    .longValue()));

            book.setStatus(OrderBookStatus.EXECUTED);
        }
    }

    public String getJson() {

        String json = null;
        ObjectMapper mapper = new ObjectMapper();

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            json = ow.writeValueAsString(books);
        } catch (IOException e) {
            LOGGER.error(
                    "There was an error converting order book array to json {}",
                    e.getMessage());
        }

        return json;
    }

    private void setOrdersByComparison(BookStats bookStats,
            List<Order> orderList) {
        // Get earliest and latest order
        Comparator<Order> dateComparator =
                Comparator.comparing(Order::getEntryDate);

        Optional<Order> earliest = orderList.stream().min(dateComparator);
        Optional<Order> latest = orderList.stream().max(dateComparator);

        bookStats.setEarliestOrder(earliest.orElse(null));
        bookStats.setLastOrder(latest.orElse(null));

        Comparator<Order> qtyComparator =
                Comparator.comparing(Order::getOrderQty);

        Optional<Order> biggestValidOrder = orderList.stream()
                .filter(Order::isValid).max(qtyComparator);
        Optional<Order> smallestValidOrder = orderList.stream()
                .filter(Order::isValid).min(qtyComparator);

        bookStats.setBiggestValidOrder(biggestValidOrder.orElse(null));
        bookStats.setSmallestValidOrder(smallestValidOrder.orElse(null));

    }

    private void setStatsByOrderValidity(BookStats bookStats,
            List<Order> orderList) {
        // Stats based on valid/invalid orders
        Map<Boolean, LongSummaryStatistics> orderStatsByValidity =
                orderList.stream()
                        .collect(partitioningBy(Order::isValid,
                                collectingAndThen(
                                        summarizingLong(Order::getOrderQty),
                                        x -> x)));

        bookStats.setTotalInvalidOrders(
                orderStatsByValidity.get(Boolean.FALSE).getCount());

        bookStats.setTotalValidOrders(
                orderStatsByValidity.get(Boolean.TRUE).getCount());

    }

    private void setLimitTables(BookStats bookStats, List<Order> orderList) {
        Predicate<Order> limitType =
                order -> (OrderType.LIMIT == order.getType());
        Collector<Order, ?, Map<BigDecimal, Long>> mapper =
                Collectors.toMap(Order::getOrderPrice, Order::getOrderQty,
                (x1, x2) -> (x1 + x2));

        // Get table for limit prices and demand per limit price
        Map<BigDecimal, Long> validLimitTable = orderList.stream().filter(
                limitType.and(Order::isValid)).collect(mapper);

        Map<BigDecimal, Long> invalidLimitTable = orderList.stream().filter(
                limitType.and(order -> !order.isValid())).collect(mapper);

        Map<BigDecimal, Long> limitTable = orderList.stream().filter(limitType)
                .collect(mapper);

        bookStats.setLimitTable(limitTable);
        bookStats.setValidLimitTable(validLimitTable);
        bookStats.setInvalidLimitTable(invalidLimitTable);
    }
}
