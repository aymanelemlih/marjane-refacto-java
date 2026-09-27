package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.interfaces.OrderService;
import com.nimbleways.springboilerplate.services.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    final OrderRepository orderRepository;
    final ProductService productService;

    /**
     * {@Inheritence}
     */
    private Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found!"));
    }

    /**
     * {@Inheritence}
     */
    @Transactional
    @Override
    public Order processOrder(Long orderId) {
        Order order = getOrderById(orderId);

        productService.handleProduct(order.getItems());

        return order;
    }
}
