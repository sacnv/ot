package com.example.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.example")
public class OrderTradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderTradeApplication.class, args);
	}

	
}
