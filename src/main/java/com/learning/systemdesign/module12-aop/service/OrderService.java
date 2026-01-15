package com.learning.systemdesign.module12_aop.service;

import com.learning.systemdesign.module12_aop.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @LogExecutionTime
    public String placeOrder(String item, int quantity) {
        // Simulate processing time
        try {
            Thread.sleep(500); // 500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Order placed for " + quantity + " x " + item + " [ID: " + UUID.randomUUID() + "]";
    }
}
