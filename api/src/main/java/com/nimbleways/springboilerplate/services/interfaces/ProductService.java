package com.nimbleways.springboilerplate.services.interfaces;

import com.nimbleways.springboilerplate.entities.Product;

import java.util.Set;

public interface ProductService {
    void handleProduct(Set<Product> products);
}
