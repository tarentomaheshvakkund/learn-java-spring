# Module 14: Idempotency

## 1. Overview
**Idempotency** ensures that an operation can be applied multiple times without changing the result beyond the initial application.
- **Safe**: `GET`, `PUT`, `DELETE` (usually).
- **Unsafe**: `POST` (e.g., "Pay $50"). If you send this twice, you might pay $100.

## 2. Implementation
We use **AOP** to intercept requests and check a unique Token (`X-Request-Id`).

### The Annotation (`@Idempotent`)
Marks methods that need protection.
```java
@Idempotent(headerName = "X-Request-Id")
public ResponseEntity<String> processPayment(...) { ... }
```

### The Aspect logic
1.  Extract `X-Request-Id` header.
2.  Check if it exists in our "Cache" (ConcurrentHashMap).
3.  **If Found**: Throw Exception (Stop processing!).
4.  **If Not Found**: Process request and store ID.

## 3. Verification

### Step 1: Send a Request (Success)
**POST** `/api/v14/payment`
Header: `X-Request-Id: 1001`
*   Response: `200 OK`

### Step 2: Send Duplicate Request (Fail)
**POST** `/api/v14/payment`
Header: `X-Request-Id: 1001`
*   Response: `500 Internal Server Error` (IllegalStateException: Duplicate Request Detected)
    *   *Note: In a real app, you'd map this exception to 409 Conflict using a Global Exception Handler.*
