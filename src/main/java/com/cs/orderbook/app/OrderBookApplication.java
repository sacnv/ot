
package com.cs.orderbook.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.cs.orderbook")
public class OrderBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBookApplication.class, args);
    }

}
