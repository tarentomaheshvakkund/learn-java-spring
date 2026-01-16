# Module 1: Layered Architecture

## Goal
Establish a clean separation of concerns using **Controller**, **Service**, and **Repository** layers.

## Structure
*   `com.learning.systemdesign.module1.user.UserController`: Handles HTTP.
*   `com.learning.systemdesign.module1.user.UserService`: Handles Business Logic.
*   `com.learning.systemdesign.module1.user.UserRepository`: Handles Database.

## Key Concept
The **Service Layer** protects the Domain model and ensures consistency.
We use **DTOs** (`CreateUserRequest`, `UserResponse`) to decouple the API from the Database.

## Request Flow
```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant Repository
    participant Database

    Client->>Controller: POST /api/users
    Note right of Client: Sends DTO (JSON)
    
    Controller->>Service: createUser(request)
    Note right of Controller: Validates Input
    
    Service->>Repository: save(entity)
    Note right of Service: Business Rules
    
    Repository->>Database: INSERT INTO users...
    Database-->>Repository: Returns ID
    
    Repository-->>Service: Returns Entity
    Service-->>Controller: Returns Response DTO
    Controller-->>Client: 201 Created (JSON)
```
