package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.Set;

import com.nimbleways.springboilerplate.services.interfaces.NotificationService;
import com.nimbleways.springboilerplate.services.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public final ProductRepository productRepository;
    public final NotificationService notificationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);


    @Override
    public void handleProduct(Set<Product> products) {
        for (Product p : products) {
            switch (p.getProductType()) {
                case NORMAL:
                    LOGGER.info("Handle a NORMAL product:{}", p.getName());
                    handleNormalProduct(p);
                    break;
                case SEASONAL:
                    LOGGER.info("Handle a SEASONAL product:{}", p.getName());
                    handleSeasonalProduct(p);
                    break;
                case EXPIRABLE:
                    LOGGER.info("Handle a EXPIRABLE product:{}", p.getName());
                    handleExpiredProduct(p);
                    break;
                case FLASHSALE:
                    LOGGER.info("Handle a FLASHSALE product :{}", p.getName());
                    handleFlashSaleProduct(p);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown product type: " + p.getProductType());
            }
        }
    }


    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }


    private void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

    public void handleSeasonalProduct(Product product) {
        LocalDate today = LocalDate.now();

        boolean isInSeason = !today.isBefore(product.getSeasonStartDate())
                        && !today.isAfter(product.getSeasonEndDate());

        if (isInSeason && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }
        notificationService.sendOutOfStockNotification(product.getName());
    }

    private void handleExpiredProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    public void handleFlashSaleProduct(Product p) {
        boolean saleEnded = LocalDate.now().isAfter(p.getFlashSaleEndDate());
        boolean maximumReached =
                p.getFlashSaleQuantitySold() >= p.getMaximumFlashSaleQuantity();
        boolean outOfStock = p.getAvailable() <= 0;

        if (saleEnded || maximumReached || outOfStock) {
            notificationService.sendOutOfStockNotification(p.getName());
            return;
        }

        p.setAvailable(p.getAvailable() - 1);
        p.setFlashSaleQuantitySold(p.getFlashSaleQuantitySold() + 1);

        productRepository.save(p);
    }
}