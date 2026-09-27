package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.interfaces.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
@UnitTest
class MyUnitTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    void shouldDecreaseStockForNormalProductWhenAvailable() {
        // GIVEN
        Product product = createProduct(10, 5, ProductType.NORMAL, "RJ45 Cable");

        // WHEN
        productServiceImpl.handleProduct(Set.of(product));

        // THEN
        assertEquals(9, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, never())
                .sendDelayNotification(5, "RJ45 Cable");
    }

    @Test
    void shouldNotifyDelayForNormalProductWhenOutOfStock() {
        // GIVEN
        Product product = createProduct(0, 15, ProductType.NORMAL, "RJ45 Cable");

        // WHEN
        productServiceImpl.handleProduct(Set.of(product));

        // THEN
        assertEquals(0, product.getAvailable());

        verify(notificationService, times(1)).sendDelayNotification(15, "RJ45 Cable");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldDecreaseStockForSeasonalProductDuringSeason() {
        // GIVEN
        Product product = createProduct(15, 5, ProductType.SEASONAL, "Watermelon");

        product.setSeasonStartDate(LocalDate.now().minusDays(2));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));

        // WHEN
        productServiceImpl.handleProduct(Set.of(product));

        // THEN
        assertEquals(14, product.getAvailable());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldSetStockToZeroForExpiredProduct() {
        // GIVEN
        Product product = createProduct(10, 0, ProductType.EXPIRABLE, "Milk");

        product.setExpiryDate(LocalDate.now().minusDays(1));

        // WHEN
        productServiceImpl.handleProduct(Set.of(product));

        // THEN
        assertEquals(0, product.getAvailable());

        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification(eq("Milk"), eq(product.getExpiryDate()));
    }

    @Test
    void shouldProcessFlashSaleProductWhenSaleIsActive() {
        // GIVEN
        Product product = createProduct(20, 0, ProductType.FLASHSALE, "Headphones");

        product.setFlashSaleEndDate(LocalDate.now().plusDays(2));
        product.setMaximumFlashSaleQuantity(10);
        product.setFlashSaleQuantitySold(2);

        // WHEN
        productServiceImpl.handleProduct(Set.of(product));

        // THEN
        assertEquals(19, product.getAvailable());
        assertEquals(3, product.getFlashSaleQuantitySold());

        verify(productRepository, times(1)).save(product);
    }

    private Product createProduct(Integer available, Integer leadTime, ProductType productType, String name) {

        Product product = new Product();
        product.setAvailable(available);
        product.setLeadTime(leadTime);
        product.setProductType(productType);
        product.setName(name);

        return product;
    }
}