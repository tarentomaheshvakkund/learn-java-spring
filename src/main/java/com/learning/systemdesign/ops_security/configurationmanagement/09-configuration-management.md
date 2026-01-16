# Module 9: Configuration Management (12-Factor App)

## 1. Overview
The **12-Factor App** methodology states: **"Store config in the environment"**.
Hardcoding settings (like DB URLs) is bad practice.
We use **Spring Profiles** to switch configurations based on the environment (Dev vs. Prod).

---

## 2. Profiles

### A. Development (`dev`)
*   **File**: `application-dev.properties`
*   **Database**: H2 (In-Memory)
*   **Features**: Debug logs, Beta features enabled.

### B. Production (`prod`)
*   **File**: `application-prod.properties`
*   **Database**: MySQL (Simulated URL)
*   **Features**: Info logs, Beta features disabled.

---

## 3. Implementation Details

### Configuration Files
Spring Boot automatically loads `application-{profile}.properties` based on the active profile.

### Configuration Files
Spring Boot automatically loads `application-{profile}.properties` based on the active profile.

## 3. How to Activate a Profile
There are 4 main ways to set `spring.profiles.active` (in order of precedence):

| Method | Command / Action | Use Case |
| :--- | :--- | :--- |
| **1. Command Line** | `java -jar app.jar --spring.profiles.active=prod` | CI/CD Pipelines |
| **2. JVM Option** | `-Dspring.profiles.active=prod` | Legacy Scripts |
| **3. Env Variable** | `export SPRING_PROFILES_ACTIVE=prod` | Docker / Kubernetes |
| **4. Properties** | `spring.profiles.active=dev` (in `application.properties`) | Default / Local |

> **Note**: Command Line arguments override Environment Variables, which override Properties files.

### `ConfigController`
We injected values using `@Value("${property.name}")`.
```java
@Value("${app.environment.name}")
private String environmentName;
```

---

## 4. Verification

### Step 1: Run in DEV (Default)
Run the app normally: `java -jar app.jar` (or Run in IDE).
*   **GET** `/api/v9/config`
*   **Response**:
    ```json
    {
      "environment": "Development Implementation",
      "betaFeatures": true,
      "connectedDatabase": "jdbc:h2:mem:devdb"
    }
    ```

### Step 2: Run in PROD
Run with the profile flag:
`java -jar app.jar --spring.profiles.active=prod`

*   **GET** `/api/v9/config`
*   **Response**:
    ```json
    {
      "environment": "PRODUCTION SYSTEM",
      "betaFeatures": false,
      "connectedDatabase": "jdbc:mysql://prod-db-cluster:3306/users_db"
    }
    ```

### Step 3: Check H2 Console
*   **DEV**: `http://localhost:8080/h2-console` -> **Works**.
*   **PROD**: `http://localhost:8080/h2-console` -> **404 Not Found** (We disabled it in `application-prod.properties`).

This concludes **Module 9**. Your app is now Environment Aware!
