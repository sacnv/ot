
package com.cs.orderbook.domain;

public enum OrderStatus {

    VALID("valid"), INVALID("invalid");

    private String status;

    OrderStatus(String stat) {
        this.status = stat;
    }

    public String getStatus() {
        return status;
    }

}
