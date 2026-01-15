# Module 2: Event-Driven Architecture

## Goal
Decouple the "User Creation" logic from "Side Effects" (like sending emails) using **Events**.

## Why?
Refactoring `UserService` to simply publish an event allows us to add new behaviors (Notifications, Analytics, Logging) without modifying the core Service code. This adheres to the **Open/Closed Principle**.

## Architecture

### 1. High-Level Flowchart
```mermaid
graph TD
    User([User Action]) -->|Creates User| API[Rest API]
    API -->|Calls| Service[UserService]
    
    subgraph Core System
        Service -->|1. Save| DB[(Database)]
    end
    
    subgraph Async Events
        Service -.->|2. Publish Event| EventBus{Event Bus}
        EventBus -.->|Trigger| EmailService[Email Listener]
        EventBus -.->|Trigger| Analytics[Analytics Listener]
        EventBus -.->|Trigger| Logging[Audit Log]
    end
    
    EmailService -->|Send| SMTP(Mail Server)
    
    style Service fill:#f9f,stroke:#333
    style EventBus fill:#ff9,stroke:#333
```

### 2. Sequence Flow
```mermaid
sequenceDiagram
    participant Client as Client (API)
    participant C as UserController
    participant S as UserService
    participant R as UserRepository
    participant DB as Database
    participant E as Event Bus
    participant L as WelcomeEmailListener

    Client->>C: POST /api/v2/users
    C->>S: createUser(dto)
    
    rect rgb(200, 220, 240)
        note right of S: 1. Core Logic (Synchronous)
        S->>R: existsByUsername/Email?
        S->>R: save(UserEntity)
        R->>DB: INSERT INTO users...
        DB-->>R: User ID: 1
    end

    rect rgb(220, 240, 200)
        note right of S: 2. Side Effect (Decoupled)
        S->>E: publishEvent(UserCreatedEvent)
        E-->>L: triggers handleUserCreated()
        L->>L: Log "Sending Email..."
    end

    S-->>C: return UserResponse
    C-->>Client: 201 Created
```

## Changes from Module 1
1.  **Event**: Created `UserCreatedEvent` (a record holding data).
2.  **Listener**: Created `WelcomeEmailListener` annotated with `@EventListener`.
3.  **Publisher**: Injected `ApplicationEventPublisher` into `UserService`.

## Verification
When you create a user with `POST /api/v2/users`, check the logs:
```
[Module 2] EVENT RECEIVED: User Created
Sending Welcome Email to: ...
```
This proves the listener reacted to the event!
