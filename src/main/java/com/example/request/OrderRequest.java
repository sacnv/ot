
package com.example.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.example.domain.OrderType;

public class OrderRequest {

    @NotNull
    private Long orderQty;

    @NotNull
    private Long instrId;

    @NotNull
    private OrderType type;

    private BigDecimal orderPrice;

    public OrderRequest(@NotNull Long instrId, @NotNull Long orderQty,
            @NotNull OrderType type, BigDecimal orderPrice) {
        super();
        this.orderQty = orderQty;
        this.instrId = instrId;
        this.type = type;
        this.orderPrice = orderPrice;
    }

    public Long getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Long orderQty) {
        this.orderQty = orderQty;
    }

    public Long getInstrId() {
        return instrId;
    }

    public void setInstrId(Long instrId) {
        this.instrId = instrId;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

}
