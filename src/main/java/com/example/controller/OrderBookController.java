
package com.example.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.BookStats;
import com.example.domain.Order;
import com.example.request.ExecutionRequest;
import com.example.request.OrderRequest;
import com.example.response.SimpleResponse;
import com.example.service.OrderBookService;
import com.example.trade.ApplicationLiterals;

import io.swagger.annotations.ApiOperation;

@Component
@RestController
public class OrderBookController {

    @Autowired
    private OrderBookService orderBookService;

    private static final Logger logger = LoggerFactory.getLogger(OrderBookController.class);

    @ApiOperation(
            value = ApplicationLiterals.CREATE_BOOK)

    @PostMapping(
            value = "/v1/order-books",
            produces = MediaType.APPLICATION_JSON_VALUE)

    public SimpleResponse createOrderBook(@RequestParam("instrId") Long instrId) {

        return orderBookService.createBook(instrId);

    }

    @ApiOperation(
            value = ApplicationLiterals.CLOSE_BOOK)

    @PutMapping(
            value = "/v1/order-books/{orderBookId}")

    public SimpleResponse closeOrderBook(@PathVariable("orderBookId") Long orderBookId) {
        logger.debug("Request received");

        return orderBookService.closeOrderBook(orderBookId);

    }

    @ApiOperation(
            value = ApplicationLiterals.GET_STATS)

    @GetMapping(
            value = "/v1/order-books/{orderBookId}/stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BookStats getOrderBookStats(@PathVariable("orderBookId") Long orderBookId) {

        return orderBookService.getStats(orderBookId);

    }

    @ApiOperation(
            value = ApplicationLiterals.ADD_EXEC)

    @PostMapping(
            value = "/v1/order-books/{orderBookId}/executions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse addExecution(@PathVariable("orderBookId") Long orderBookId,
            @RequestBody ExecutionRequest exec) {

        return orderBookService.addExecution(exec);

    }

    @ApiOperation(
            value = ApplicationLiterals.ADD_ORDER)

    @PostMapping(
            value = "/v1/order-books/{orderBookId}/orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse addOrder(@PathVariable("orderBookId") Long orderBookId,
            @RequestBody OrderRequest orderRequest) {

        logger.debug("Request for Order received");

        return orderBookService.addOrder(orderBookId, orderRequest);

    }

    @ApiOperation(
            value = ApplicationLiterals.GET_ORDER)

    @GetMapping(
            value = "/v1/order-books/{orderBookId}/orders/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(@PathVariable("orderBookId") Long orderBookId,
            @PathVariable("orderId") Long orderId) {

        Optional<Order> storedOrder = orderBookService.getOrder(orderBookId, orderId);

        if (storedOrder.isPresent()) {
            return new ResponseEntity<>(storedOrder.get(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
