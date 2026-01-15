# Module 12: Aspect Oriented Programming (AOP)

## 1. Overview
Cross-cutting concerns (logging, security, caching, transaction management) should not be mixed with business logic.
**Aspect Oriented Programming (AOP)** allows us to separate these concerns into distinct **Aspects**.

## 2. Components

### The Annotation (`@LogExecutionTime`)
A marker annotation to identify which methods should be monitored.
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {}
```

### The Aspect (`LoggingAspect`)
The class that contains the logic to execute around the annotated methods.
- **Advice**: (`@Around`) Wraps the method execution.
- **Pointcut**: (`@annotation(...)`) Defines where to apply the advice.

```java
@Around("@annotation(LogExecutionTime)")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed(); // Run the actual method
    long executionTime = System.currentTimeMillis() - start;
    logger.info(...);
    return proceed;
}
```

### The Business Service (`OrderService`)
The service code remains clean. It focuses only on processing the order.
```java
@LogExecutionTime
public String placeOrder(String item, int quantity) {
    // Business logic...
}
```

## 3. Benefits
1.  **Clean Code**: Business logic is not cluttered with `start = System.currentTimeMillis()` and logging statements.
2.  **Reusability**: You can apply `@LogExecutionTime` to ANY method in ANY service.
3.  **Maintainability**: If you want to change the logging format, you only change it in ONE place (the Aspect).

## 4. Verification

### Step 1: Place an Order
**POST** `/api/v12/orders?item=Laptop&quantity=1`

### Step 2: Check Logs
You will see the AOP interceptor working:
```
[AOP] ⏱️ String com.learning.systemdesign.module12_aop.service.OrderService.placeOrder(String,I) executed in 505 ms
```
