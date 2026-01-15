package com.learning.systemdesign.module12_aop;

import com.learning.systemdesign.module12_aop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class Module12AopTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testAopLogging(CapturedOutput output) {
        // Act
        String response = orderService.placeOrder("TestItem", 5);

        // Assert
        assertThat(response).contains("Order placed for 5 x TestItem");
        
        // Verify AOP Log
        // The aspect logs: "[AOP] ⏱️ ... executed in ... ms"
        assertThat(output.getOut()).contains("[AOP] ⏱️");
        assertThat(output.getOut()).contains("OrderService.placeOrder");
    }
}
