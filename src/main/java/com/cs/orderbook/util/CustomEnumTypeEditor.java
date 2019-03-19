package com.cs.orderbook.util;

import java.beans.PropertyEditorSupport;

import com.cs.orderbook.domain.OrderType;

public class CustomEnumTypeEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        OrderType exoticType = (OrderType) getValue();
        return exoticType == null ? "" : exoticType.getType().toLowerCase();
    }

    @Override
    public void setAsText(String text) {
        setValue(OrderType.valueOf(text.toUpperCase()));
    }
}
