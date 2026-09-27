package com.nimbleways.springboilerplate.services.interfaces;

import java.time.LocalDate;

public interface NotificationService {
    void sendDelayNotification(int leadTime, String productName);

    void sendOutOfStockNotification(String productName);

    void sendExpirationNotification(String productName, LocalDate expiryDate);
}
