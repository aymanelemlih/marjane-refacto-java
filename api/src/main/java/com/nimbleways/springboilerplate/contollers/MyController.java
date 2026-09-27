package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;


import com.nimbleways.springboilerplate.services.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class MyController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/processOrder")
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        Order order = orderService.processOrder(orderId);
        return new ProcessOrderResponse(order.getId());
    }
}
