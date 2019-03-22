
package com.cs.orderbook.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cs.orderbook.domain.BookStats;
import com.cs.orderbook.domain.Order;
import com.cs.orderbook.domain.OrderType;
import com.cs.orderbook.request.ExecutionRequest;
import com.cs.orderbook.request.OrderRequest;
import com.cs.orderbook.response.SimpleResponse;
import com.cs.orderbook.service.OrderBookService;
import com.cs.orderbook.app.ApplicationLiterals;
import com.cs.orderbook.util.CustomEnumTypeEditor;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class OrderBookController {

    @Autowired
    private OrderBookService orderBookService;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderBookController.class);

    @ApiOperation(
            value = ApplicationLiterals.CREATE_BOOK)

    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 201,
                        message = "Created",
                        response = SimpleResponse.class)
            })

    @PostMapping(
            value = "/v1/order-books",
            produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<SimpleResponse> createOrderBook(
            HttpServletRequest request, @RequestParam("instrId") Long instrId) {

        SimpleResponse resp = orderBookService.createBook(instrId);
        return new ResponseEntity<>(resp, getReponseHeaders(request, resp),
                HttpStatus.CREATED);
    }

    @ApiOperation(
            value = ApplicationLiterals.CLOSE_BOOK)

    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 200,
                        message = "OK",
                        response = SimpleResponse.class)
            })

    @PutMapping(
            value = "/v1/order-books/{orderBookId}")

    public SimpleResponse
            closeOrderBook(@PathVariable("orderBookId") Long orderBookId) {
        LOGGER.debug("Request received");

        return orderBookService.closeOrderBook(orderBookId);

    }

    @ApiOperation(
            value = ApplicationLiterals.GET_STATS)
    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 200,
                        message = "OK",
                        response = BookStats.class),
                @ApiResponse(
                        code = 404,
                        message = "Not Found",
                        response = Void.class)
            })

    @GetMapping(
            value = "/v1/order-books/{orderBookId}/stats",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BookStats
            getOrderBookStats(@PathVariable("orderBookId") Long orderBookId) {

        return orderBookService.getStats(orderBookId);

    }

    @ApiOperation(
            value = ApplicationLiterals.ADD_EXEC)

    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 201,
                        message = "Created",
                        response = SimpleResponse.class)
            })

    @PostMapping(
            value = "/v1/order-books/{orderBookId}/executions",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleResponse> addExecution(
            HttpServletRequest request,
            @PathVariable("orderBookId") Long orderBookId,
            @RequestBody ExecutionRequest exec) {

        SimpleResponse resp = orderBookService.addExecution(orderBookId, exec);
        return new ResponseEntity<>(resp, getReponseHeaders(request, resp),
                HttpStatus.CREATED);

    }

    @ApiOperation(
            value = ApplicationLiterals.ADD_ORDER)

    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 201,
                        message = "Created",
                        response = SimpleResponse.class)
            })

    @PostMapping(
            value = "/v1/order-books/{orderBookId}/orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleResponse> addOrder(HttpServletRequest request,
            @PathVariable("orderBookId") Long orderBookId,
            @RequestBody OrderRequest orderRequest) {

        LOGGER.debug("Request for Order received");

        SimpleResponse resp =
                orderBookService.addOrder(orderBookId, orderRequest);
        return new ResponseEntity<>(resp, getReponseHeaders(request, resp),
                HttpStatus.CREATED);
    }

    @ApiOperation(
            value = ApplicationLiterals.GET_ORDER)

    @ApiResponses(
            value = {
                @ApiResponse(
                        code = 200,
                        message = "OK",
                        response = Order.class),
                @ApiResponse(
                        code = 404,
                        message = "Not Found",
                        response = Void.class)
            })

    @GetMapping(
            value = "/v1/order-books/{orderBookId}/orders/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(
            @PathVariable("orderBookId") Long orderBookId,
            @PathVariable("orderId") Long orderId) {

        Optional<Order> storedOrder =
                orderBookService.getOrder(orderBookId, orderId);

        if (storedOrder.isPresent()) {
            return new ResponseEntity<>(storedOrder.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private HttpHeaders getReponseHeaders(HttpServletRequest request,
            SimpleResponse resp) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Location",
                request.getRequestURL() + "/" + resp.getId());
        return responseHeaders;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(OrderType.class,
            new CustomEnumTypeEditor());
    }
}
