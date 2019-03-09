
package com.example.domain;

public enum OrderBookStatus {

    OPEN("open"), CLOSED("closed"), EXECUTED("executed");

    private String status;

    private OrderBookStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
