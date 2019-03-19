
package com.cs.orderbook.exception;

public class OTException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String shortMessage;

    @Override
    public String getMessage() {
        return shortMessage;
    }

    public OTException() {
        this.shortMessage = "";
    }

    public OTException(String message) {
        super();
        this.shortMessage = message;
    }

}
