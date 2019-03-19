
package com.cs.orderbook.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.cs.orderbook.util.IDGenerator;
import com.cs.orderbook.util.OTBigFraction;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Order {

    private final Long id;

    @NotNull
    private final Long orderQty;

    // Can be changed to Instant, if nanosecond level precision is required
    @JsonIgnore
    private final LocalDateTime entryDate;

    @NotNull
    private final Long instrId;

    @NotNull
    private final OrderType type;

    private OrderStatus status = OrderStatus.VALID;

    @NotNull
    private BigDecimal orderPrice;

    private Long execQty;

    private BigDecimal execPrice;

    private OTBigFraction allocationFactor;

    public Order() {
        this.id = IDGenerator.generateId(Order.class);
        this.orderPrice = BigDecimal.ZERO;
        this.orderQty = 0L;
        this.instrId = -1L;
        this.type = OrderType.LIMIT;
        this.entryDate = LocalDateTime.now();
    }

    public Long getOrderQty() {
        return orderQty;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public Order(@NotNull Long orderQty, @NotNull Long instrId,
            @NotNull OrderType type, @NotNull BigDecimal orderPrice) {
        super();
        this.id = IDGenerator.generateId(Order.class);
        this.orderQty = orderQty;
        this.instrId = instrId;
        this.type = type;
        this.orderPrice = orderPrice;
        this.entryDate = LocalDateTime.now();
        this.execQty = 0L;
        this.execPrice = BigDecimal.ZERO;
    }

    public Long getInstrId() {
        return instrId;
    }

    public OrderType getType() {
        return type;
    }

    public boolean isValid() {
        return (OrderStatus.VALID.equals(status));
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getExecQty() {
        return execQty;
    }

    public void setExecQty(Long execQty) {
        this.execQty = execQty;
    }

    public BigDecimal getExecPrice() {
        return execPrice;
    }

    public void setExecPrice(BigDecimal execPrice) {
        this.execPrice = execPrice;
    }

    public OTBigFraction getAllocationFactor() {
        return allocationFactor;
    }

    public void setAllocationFactor(OTBigFraction allocationFactor) {
        this.allocationFactor = allocationFactor;
    }

}
