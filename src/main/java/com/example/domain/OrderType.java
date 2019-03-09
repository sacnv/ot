
package com.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {

    LIMIT("limit"), MARKET("market");

    private String type;

    private OrderType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

}
