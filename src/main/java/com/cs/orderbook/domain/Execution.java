
package com.cs.orderbook.domain;

import java.math.BigDecimal;

import com.cs.orderbook.util.IDGenerator;

public class Execution {

    private Long id;

    private Long quantity;

    private BigDecimal execPrice;

    public Execution() {
    }

    public Execution(Long quantity, BigDecimal execPrice) {
        super();
        this.id = IDGenerator.generateId(Execution.class);
        this.quantity = quantity;
        this.execPrice = execPrice;
    }

    public Execution(Long id, Long quantity, BigDecimal execPrice) {
        super();
        this.id = id;
        this.quantity = quantity;
        this.execPrice = execPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getExecPrice() {
        return execPrice;
    }

    public void setExecPrice(BigDecimal execPrice) {
        this.execPrice = execPrice;
    }

    public void reduceExecQuantity(Long quantity) {
        this.quantity -= quantity;
    }

}
