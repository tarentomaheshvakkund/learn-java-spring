package com.learning.systemdesign.module14_idempotency.aspect;

import com.learning.systemdesign.module14_idempotency.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class IdempotencyAspect {

    // In a real system, use Redis/Memcached with TTL (Time To Live)
    private final Map<String, Object> processedRequests = new ConcurrentHashMap<>();

    @Around("@annotation(idempotent)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestId = request.getHeader(idempotent.headerName());

        if (requestId == null || requestId.isEmpty()) {
            throw new IllegalArgumentException("Header '" + idempotent.headerName() + "' is missing in idempotent request.");
        }

        // Check if processed
        if (processedRequests.containsKey(requestId)) {
            throw new IllegalStateException("Duplicate Request Detected: " + requestId);
        }

        // Process request
        Object result = joinPoint.proceed();

        // Mark as processed (Store key)
        processedRequests.put(requestId, Boolean.TRUE);

        return result;
    }
}
