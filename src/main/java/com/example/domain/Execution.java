
package com.example.domain;

import java.math.BigDecimal;

import com.example.util.IDGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;

public class Execution {

    private Long id;

    private Long quantity;

    private BigDecimal execPrice;

    @JsonBackReference
    private Long orderBookId;

    public Execution() {
    }

    public Execution(Long quantity, BigDecimal execPrice, Long orderBookId) {
        super();
        this.id = IDGenerator.generateId(Execution.class);
        this.quantity = quantity;
        this.execPrice = execPrice;
        this.orderBookId = orderBookId;
    }

    public Execution(Long id, Long quantity, BigDecimal execPrice,
            Long orderBookId) {
        super();
        this.id = id;
        this.quantity = quantity;
        this.execPrice = execPrice;
        this.orderBookId = orderBookId;
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

    public Long getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(Long orderBookId) {
        this.orderBookId = orderBookId;
    }

}
