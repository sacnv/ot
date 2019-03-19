
package com.cs.orderbook.domain;

public enum OrderBookStatus {

    OPEN("open"), CLOSED("closed"), EXECUTED("executed");

    private String status;

    OrderBookStatus(String stat) {
        this.status = stat;
    }

    public String getStatus() {
        return status;
    }

}
