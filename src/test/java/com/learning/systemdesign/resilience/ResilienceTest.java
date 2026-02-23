package com.learning.systemdesign.resilience;

import com.learning.systemdesign.resilience.resilience.controller.ResilienceController;
import com.learning.systemdesign.resilience.resilience.service.ExternalApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Resilience Pattern Tests")
class ResilienceTest {

    @Nested
    @DisplayName("ExternalApiService")
    class ExternalApiServiceTest {

        private final ExternalApiService service = new ExternalApiService();

        @Test
        @DisplayName("callExternalSystem either succeeds or throws RuntimeException")
        void callExternalSystemBehavior() {
            int successes = 0;
            int failures = 0;

            for (int i = 0; i < 100; i++) {
                try {
                    String result = service.callExternalSystem();
                    assertThat(result).startsWith("Success: Payment Processed");
                    successes++;
                } catch (RuntimeException e) {
                    assertThat(e.getMessage()).isEqualTo("External System is Down!");
                    failures++;
                }
            }

            // With 70% failure rate, we should see both successes and failures in 100 tries
            assertThat(successes).isGreaterThan(0);
            assertThat(failures).isGreaterThan(0);
        }
    }

    @Nested
    @DisplayName("ResilienceController - Fallback")
    class ResilienceControllerTest {

        @Test
        @DisplayName("fallbackPayment returns fallback message with error details")
        void fallbackPaymentReturnsMessage() {
            ExternalApiService service = new ExternalApiService();
            ResilienceController controller = new ResilienceController(service);

            String fallback = controller.fallbackPayment(new RuntimeException("Service down"));

            assertThat(fallback).contains("Fallback");
            assertThat(fallback).contains("Service down");
        }
    }

    @Nested
    @DisplayName("Idempotency Annotation")
    class IdempotencyAnnotationTest {

        @Test
        @DisplayName("Idempotent annotation has correct default header name")
        void idempotentAnnotationDefaults() throws NoSuchMethodException {
            var annotation = com.learning.systemdesign.resilience.idempotency.controller.PaymentController.class
                    .getMethod("processPayment", String.class)
                    .getAnnotation(com.learning.systemdesign.resilience.idempotency.annotation.Idempotent.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.headerName()).isEqualTo("X-Request-Id");
        }
    }
}
