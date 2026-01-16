# Module 16: Distributed Tracing

## 1. Overview
**Distributed Tracing** allows us to track a request as it flows through our system.
Even in a monolith, correlating logs is vital.
- **Trace ID**: A unique ID for the entire request chain.
- **Span ID**: A unique ID for a specific unit of work (e.g., a method call or a sub-request).

## 2. Implementation
We use **Micrometer Tracing** (with Brave) to automatically inject these IDs into our logs.

### Configuration
`pom.xml`: Added `micrometer-tracing-bridge-brave`.
`application.properties`:
```properties
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

### The Controller (`TracingController`)
We added a simple endpoint `/api/v16/trace` that logs two messages.

## 3. Verification

### Step 1: Call Endpoint
```bash
curl http://localhost:8080/api/v16/trace
```

### Step 2: Check Logs
You will see output like this:
```
INFO [learn-java-spring,65a1b2c...,65a1b2c...] - Request received at TracingController
INFO [learn-java-spring,65a1b2c...,1234567...] - Processing inside internalServiceMethod...
```
Notice that the **Trace ID** (`65a1b2c...`) is identical for both lines! This proves they belong to the same request.
