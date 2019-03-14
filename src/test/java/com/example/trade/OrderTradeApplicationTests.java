
package com.example.trade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.controller.OrderBookController;
import com.example.domain.Order;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT)

public class OrderTradeApplicationTests {

    @Autowired
    private OrderBookController controller;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getOrder() {
        ResponseEntity<Order> storedOrder = this.restTemplate
                .getForEntity("http://localhost:" + port 
                        + "/order-books/1/orders/1", Order.class);
        assertEquals(HttpStatus.NOT_FOUND, storedOrder.getStatusCode());
    }

}
