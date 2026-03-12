# GEMINI.md - MitoBooks Project Context

## Project Overview
**MitoBooks** is a reactive REST API for managing a bookstore, built with **Spring Boot 3** and **Java 21**. It utilizes **Spring WebFlux** for non-blocking operations and **Spring Data MongoDB Reactive** for data persistence.

The project follows a standard Spring architecture adapted for reactive programming, including controllers, services, repositories, and DTO/Mapper layers.

## Tech Stack
- **Runtime:** Java 21
- **Framework:** Spring Boot 3.5.6 (WebFlux)
- **Database:** MongoDB (Reactive)
- **Infrastructure:** Docker Compose
- **Libraries:**
  - **Lombok:** Boilerplate reduction (getters, setters, etc.)
  - **MapStruct:** DTO to Entity mapping.
  - **Project Reactor:** Core reactive library (`Mono`, `Flux`).
  - **JaCoCo:** Test coverage reporting.

## Project Structure
- `com.mitocode.controller`: Reactive REST controllers.
- `com.mitocode.service`: Business logic interfaces and implementations (`impl`).
- `com.mitocode.repo`: Reactive repositories extending `ReactiveMongoRepository`.
- `com.mitocode.model`: MongoDB document definitions.
- `com.mitocode.dto`: Data Transfer Objects for API requests/responses.
- `com.mitocode.mapper`: MapStruct interfaces for DTO/Entity conversion.
- `com.mitocode.exception`: Global error handling and custom exceptions.

## Building and Running
### Prerequisites
- Java 21
- Docker (for MongoDB)

### Commands
- **Start Infrastructure:** `docker-compose up -d`
- **Build Project:** `./mvnw clean install`
- **Run Application:** `./mvnw spring-boot:run`
- **Run Tests:** `./mvnw test`
- **Check Coverage:** `target/site/jacoco/index.html` (after running tests)

## Development Conventions
- **Reactive Streams:** Always use `Mono<T>` or `Flux<T>` for asynchronous/non-blocking data flows.
- **Avoid Blocking I/O:** Ensure no blocking operations (e.g., `Thread.sleep()`, synchronous `RestTemplate`, JPA) are introduced. This project has been migrated from Spring MVC/JPA to WebFlux/MongoDB Reactive to achieve full non-blocking performance.
- **DTOs:** Do not expose Entities directly. Use `MapStruct` mappers to convert between Entities and DTOs.
- **CRUD Abstraction:** Common CRUD operations are abstracted in `ICRUD` and `CRUDImpl`.
- **Validation:** Use `jakarta.validation` annotations in DTOs.
- **Standardized Responses:** API responses are wrapped in `GenericResponse` for consistency.
- **Error Handling:** Centralized in `GlobalErrorHandler`.

## Key Files
- `pom.xml`: Project dependencies and build configuration.
- `docker-compose.yml`: Local infrastructure (MongoDB).
- `src/main/resources/application.yml`: Application configuration and MongoDB URI.
- `prompts/`: Historical planning documents for the migration to reactive.
