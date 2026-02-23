package com.learning.systemdesign.resilience;

import com.learning.systemdesign.resilience.idempotency.controller.PaymentController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Idempotency Controller Integration Tests")
class IdempotencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Payment with valid X-Request-Id succeeds")
    void paymentWithValidRequestIdSucceeds() throws Exception {
        mockMvc.perform(post("/api/v14/payment")
                        .header("X-Request-Id", "unique-id-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Processed Successfully for ID: unique-id-1"));
    }

    @Test
    @DisplayName("Duplicate payment with same X-Request-Id is rejected")
    void duplicatePaymentIsRejected() throws Exception {
        String requestId = "duplicate-test-id-" + System.nanoTime();

        // First request succeeds
        mockMvc.perform(post("/api/v14/payment")
                        .header("X-Request-Id", requestId))
                .andExpect(status().isOk());

        // Second request with same ID should fail (caught by IdempotencyAspect)
        mockMvc.perform(post("/api/v14/payment")
                        .header("X-Request-Id", requestId))
                .andExpect(status().is5xxServerError());
    }
}
