
package com.cs.orderbook.app;

public final class ApplicationLiterals {

    private ApplicationLiterals() {
    }

    public static final String CREATE_BOOK =
            "create an order book for given instrument id";

    public static final String OPEN_BOOK =
            "open order book for adding orders";

    public static final String CLOSE_BOOK =
            "close order book to allow adding executions";

    public static final String GET_STATS =
            "get stats for Order book with given order book id";

    public static final String ADD_EXEC =
            "add execution to an order book with given order book id";

    public static final String ADD_ORDER =
            "add order to an order book";

    public static final String GET_ORDER =
            "get order for given order book and order id";

    public static final String LICENSE_URL = "www.example.com/api/license";

    public static final String API_LICENSE = "API License";

    public static final String CONTACT_MAILID = "myeaddress@example.com";

    public static final String CONTACT_URL = "www.example.com";

    public static final String CONTACT_NAME = "Sac nv";

    public static final String TOS = "Terms of service";

    public static final String API_VERSION = "1.0.0";

    public static final String API_DESCRIPTION =
            "API for managing order Books, adding orders, executions and "
                    + "getting statistics for the books";

    public static final String API_TITLE = "Order Book REST API";
}
