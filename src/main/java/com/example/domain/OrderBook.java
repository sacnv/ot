
package com.example.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.util.IDGenerator;

public class OrderBook {

    private Long id;

    private List<Order> orderList = new CopyOnWriteArrayList<>();

    private List<Execution> execList = new CopyOnWriteArrayList<>();

    private OrderBookStatus status;

    private Long instrId;

    public OrderBook() {
    }

    public OrderBook(Long bookId) {
        super();
        this.id = IDGenerator.generateId(OrderBook.class);
        this.instrId = bookId;
        this.status = OrderBookStatus.OPEN;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Execution> getExecList() {
        return execList;
    }

    public void setExecList(List<Execution> execList) {
        this.execList = execList;
    }

    public OrderBookStatus getStatus() {
        return status;
    }

    public void setStatus(OrderBookStatus status) {
        this.status = status;
    }

    public Long getInstrId() {
        return instrId;
    }

    public void setInstrId(Long instrId) {
        this.instrId = instrId;
    }

    public Long getId() {
        return id;
    }

    public void addExecution(Execution exec) {
        execList.add(exec);
    }

    public Execution removeLastExecution() {
        return execList.remove(execList.size() - 1);
    }

    public Long getTotalDemand() {
        return orderList.stream().filter(Order::isValid)
                .mapToLong(Order::getOrderQty).sum();
    }

    public Long getTotalExecQty() {
        return execList.stream().mapToLong(Execution::getQuantity).sum();
    }
}
