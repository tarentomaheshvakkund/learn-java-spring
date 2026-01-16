# Module 10: Resilience (Circuit Breaker)

## 1. Overview
In distributed systems, failures are inevitable.
A **Circuit Breaker** prevents a single failing service from crashing the entire application ("Cascading Failure").

We use **Resilience4j**, a lightweight fault tolerance library.

---

## 2. The Circuit Breaker Pattern

### States
1.  **CLOSED** (🟢):
    *   Everything is normal. Calls go through to the external service.
2.  **OPEN** (🔴):
    *   Failures exceeded the threshold (e.g., 50%).
    *   **The Circuit Trips**. All calls fail *immediately* without waiting for the slow service.
    *   Fallback logic is executed.
3.  **HALF-OPEN** (🟡):
    *   After a wait duration (e.g., 10s), the circuit allows a few calls to "test" if the service is back.
    *   If they succeed -> **CLOSED**.
    *   If they fail -> **OPEN**.

---

## 3. Implementation Details

### The Simulated Service (`ExternalApiService`)
We intentionally made it "flaky":
```java
// 70% chance of throwing RuntimeException
if (new Random().nextInt(10) < 7) throw new RuntimeException("Down!");
```

### The Circuit Breaker (`ResilienceController`)
We wrapped the call with `@CircuitBreaker`.
```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
public String processPayment() { ... }
```

### Configuration (`application.properties`)
```properties
resilience4j.circuitbreaker.instances.paymentService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.paymentService.sliding-window-size=5
resilience4j.circuitbreaker.instances.paymentService.wait-duration-in-open-state=10000ms
```

---

## 4. Verification

### Step 1: Hit the Endpoint
**GET** `/api/v10/payment`
*   Refresh multiple times rapidly.

### Step 2: Observe Behavior
1.  **Success**: `Success: Payment Processed` (approx 30% of time).
2.  **Initial Failures**: The server tries to call the service, and it fails. You see an error (or fallback if instant).
3.  **Circuit OPENS**: After 5 calls (if >50% failed), you will see **Immediate Fallback**.
    *   Response: `Fallback: Payment Queued (Server is busy)`.
    *   *Note*: The fallback is instant. It doesn't even try to call the simulated service.

### Step 3: Wait and Recover
1.  Wait for **10 seconds** (configured wait duration).
2.  Try again. Using "Half-Open" state, if it succeeds, the circuit closes again.

This concludes **Module 10**. Your system is now Fault Tolerant!
