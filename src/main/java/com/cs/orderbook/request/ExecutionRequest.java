
package com.cs.orderbook.request;

import java.math.BigDecimal;

public class ExecutionRequest {

    private Long quantity;

    private BigDecimal execPrice;

    public ExecutionRequest() {
    }

    public ExecutionRequest(BigDecimal execPrice, Long quantity) {
        super();
        this.quantity = quantity;
        this.execPrice = execPrice;
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

}
