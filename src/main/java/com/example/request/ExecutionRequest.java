
package com.example.request;

import java.math.BigDecimal;

public class ExecutionRequest {

    private Long quantity;

    private BigDecimal execPrice;

    private Long orderBookId;

    public ExecutionRequest(Long orderBookId, BigDecimal execPrice,
            Long quantity) {
        super();
        this.quantity = quantity;
        this.execPrice = execPrice;
        this.orderBookId = orderBookId;
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
