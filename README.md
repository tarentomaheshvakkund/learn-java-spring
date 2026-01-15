# Learn Java Spring

A Spring Boot 3.x application with Java 21.

## Prerequisites

- Java 21 or higher
- Maven 3.6+

## Project Structure

```
learn-java-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── igot/
│   │   │           ├── LearnJavaSpringApplication.java
│   │   │           └── controller/
│   │   │               └── HealthController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/
│           └── com/
│               └── igot/
│                   └── LearnJavaSpringApplicationTests.java
└── pom.xml
```

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.2.1
- **Spring Web**: For REST APIs
- **Spring Data JPA**: For database operations
- **H2 Database**: In-memory database for development
- **Lombok**: To reduce boilerplate code
- **Spring Boot DevTools**: For hot reload during development

## Getting Started

### Build the project

```bash
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run
```

Or with a specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run tests

```bash
mvn test
```

## Endpoints

- **Health Check**: `GET http://localhost:8080/api/health`
- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)

## Configuration

The application uses Spring profiles for different environments:
- `application.properties` - Default configuration
- `application-dev.properties` - Development profile
- `application-prod.properties` - Production profile

## Development

The project includes Spring Boot DevTools for automatic restart during development. Simply make changes to your code and the application will restart automatically.

## Database

The project uses H2 in-memory database by default. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## License

This project is for educational purposes.
