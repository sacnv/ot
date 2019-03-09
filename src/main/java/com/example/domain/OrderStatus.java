
package com.example.domain;

public enum OrderStatus {

    VALID("valid"), INVALID("invalid");

    private String status;

    private OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
