# Module 13: API Rate Limiting

## 1. Overview
**Rate Limiting** restricts the number of requests a user (or client) can make to an API within a specific time window.
This is crucial for:
1.  **Preventing Abuse**: Stopping scrapers or malicious users.
2.  **Stability**: Preventing the server from being overwhelmed (DDoS protection).
3.  **Fairness**: Ensuring one heavy user doesn't degrade performance for others.

## 2. Implementation with Resilience4j
We reuse the **Resilience4j** library (from Module 10).

### Configuration (`application.properties`)
We defined a limiter named `basic`:
```properties
# 5 requests allowed every 60 seconds
resilience4j.ratelimiter.instances.basic.limit-for-period=5
resilience4j.ratelimiter.instances.basic.limit-refresh-period=60s
resilience4j.ratelimiter.instances.basic.timeout-duration=0ms
```

### The Controller (`RateLimitedController`)
We applied `@RateLimiter` to the endpoint:
```java
@GetMapping
@RateLimiter(name = "basic", fallbackMethod = "rateLimitFallback")
public ResponseEntity<String> limitedEndpoint() { ... }
```

## 3. Verification

### Step 1: Hit the Endpoint
**GET** `/api/v13/limited`

### Step 2: Spam the Endpoint
1.  Send 5 requests quickly.
    *   Response: `200 OK - Request Successful`
2.  Send the 6th request.
    *   Response: `429 Too Many Requests - Rate Limit Exceeded`
    *   The `fallbackMethod` handles the exception (`RequestNotPermitted`) and returns a polite error message.

## 4. Difference from Circuit Breaker
*   **Circuit Breaker** (Module 10): Stops calling a *downstream* service when IT is failing. (Protects YOU from THEM).
*   **Rate Limiter** (Module 13): Stops an *upstream* user when THEY are calling too fast. (Protects YOU from THEM).
