
package com.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {

    LIMIT("limit"), MARKET("market");

    private String type;

    OrderType(String orderType) {
        this.type = orderType;
    }

    @JsonValue
    public String getType() {
        return type;
    }

}
