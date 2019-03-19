
package com.cs.orderbook.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderType {
    @JsonProperty("limit")
    LIMIT("limit"),
    @JsonProperty("market")
    MARKET("market");

    private String type;

    OrderType(String orderType) {
        this.type = orderType.toLowerCase();
    }

    @JsonValue
    public String getType() {
        return type;
    }

}
