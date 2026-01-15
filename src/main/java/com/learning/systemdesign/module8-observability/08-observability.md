# Module 8: Observability (Production Readiness)

## 1. Overview
"It works on my machine" is not enough. You need to know if it works in **Production**.
**Observability** is the ability to understand the internal state of a system by looking at its outputs.

We use **Spring Boot Actuator** to expose these outputs.

---

## 2. The "Big Three" of Observability
Actuator exposes operational endpoints.

### A. Health (`/actuator/health`)
*   **Purpose**: Is the app alive?
*   **Response**: `{"status": "UP"}`
*   **Usage**: Kubernetes checks this. If "DOWN", Kubernetes kills and restarts the pod.
*   **Customization**: We added `CustomHealthIndicator` to simulate a DB check.

### B. Metrics (`/actuator/metrics`)
*   **Purpose**: Numerical data over time.
*   **Examples**:
    *   `jvm.memory.used`: How much RAM?
    *   `http.server.requests`: How many API calls?
    *   `system.cpu.usage`: CPU load?
*   **Usage**: Exported to **Prometheus** and visualized in **Grafana**.

### C. Info (`/actuator/info`)
*   **Purpose**: Static metadata.
*   **Usage**: Which version is deployed? What is the Git commit hash?

---

## 3. Implementation Details

### Configuration
`application.properties`:
```properties
# Expose everything (Don't do this in Prod without Security!)
management.endpoints.web.exposure.include=health,info,metrics,loggers
# Show details (UP/DOWN reasons)
management.endpoint.health.show-details=always
```

### Security Integration
Since we have `Module 7 (Security)` active, we had to explicitly **allow** these endpoints in `SecurityConfig`.
```java
requestMatchers("/actuator/**").permitAll()
```

---

## 4. Verification

### Step 1: Check Health
**GET** `/actuator/health`
*   **Response**:
    ```json
    {
      "status": "UP",
      "components": {
        "custom": {
          "status": "UP",
          "details": {
            "Database Service": "Available"
          }
        },
        "db": { "status": "UP" },
        "diskSpace": { "status": "UP" }
      }
    }
    ```
    *(Run it multiple times. Our custom indicator might randomly return DOWN!)*

### Step 2: Check Metrics
**GET** `/actuator/metrics/jvm.memory.used`
*   **Response**:
    ```json
    {
      "name": "jvm.memory.used",
      "measurements": [
        { "statistic": "VALUE", "value": 123456.0 }
      ]
    }
    ```

This concludes **Module 8**. Your application is now observable!
