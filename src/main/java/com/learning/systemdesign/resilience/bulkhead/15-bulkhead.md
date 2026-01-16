# Module 15: Bulkhead Pattern

## 1. Overview
The **Bulkhead Pattern** is about **Resource Isolation**.
Named after the watertight partitions in a ship's hull: if one compartment floods, the others stay dry, and the ship stays afloat.

In software:
- If `Service A` is slow and consuming all threads.
- `Service B` should not be affected.

## 2. Implementation
We use **Resilience4j** to limit the number of **Concurrent Calls**.

### Configuration (`application.properties`)
```properties
# Only allow 3 concurrent requests to this specific service
resilience4j.bulkhead.instances.bulkheadService.max-concurrent-calls=3
# If all 3 are busy, fail IMMEDIATELY (don't queue)
resilience4j.bulkhead.instances.bulkheadService.max-wait-duration=0ms
```

### The Controller (`BulkheadController`)
```java
@GetMapping
@Bulkhead(name = "bulkheadService", fallbackMethod = "bulkheadFallback")
public ResponseEntity<String> heavyOperation() {
    Thread.sleep(5000); // Simulate holding the thread for 5s
    return ...;
}
```

## 3. Verification

### Step 1: Open 4 Terminals
We need to hit the server faster than it can process (5 seconds).

### Step 2: Fire Requests
Run this in 4 separate terminals simultaneously:
```bash
curl -i http://localhost:8080/api/v15/bulkhead
```

### Step 3: Observe
1.  **Terminal 1, 2, 3**: Will hang for 5 seconds, then return `200 OK`.
2.  **Terminal 4**: Will **immediately** return `503 Service Busy`.

This proves that the 4th request was rejected because the "Bulkhead" (size 3) was full. The thread was saved for other parts of the system!
