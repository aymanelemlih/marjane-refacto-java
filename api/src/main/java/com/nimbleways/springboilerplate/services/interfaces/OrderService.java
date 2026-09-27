package com.nimbleways.springboilerplate.services.interfaces;

import com.nimbleways.springboilerplate.entities.Order;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {


    /**
     * Save and process an Order with their types using productService to update their existences
     * and sending notification's
     * @param orderId an Order id to get
     */
    @Transactional
    Order processOrder(Long orderId);
}
