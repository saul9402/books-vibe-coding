# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start MongoDB (required before running the app)
docker-compose up -d

# Run the application
./mvnw spring-boot:run

# Build
./mvnw clean install
./mvnw clean install -DskipTests

# Run tests
./mvnw test
```

## Architecture

MitoBooks is a **reactive REST API** built with Spring Boot 3 (WebFlux) + MongoDB. All I/O must be non-blocking — never use blocking calls in reactive chains.

### Tech Stack
- **Java 21**, Spring Boot 3.5.6, Spring WebFlux
- **Spring Data MongoDB Reactive** (`ReactiveMongoRepository`)
- **MapStruct** for DTO↔Entity mapping (compile-time generated)
- **Lombok** for boilerplate; annotation processor order matters: Lombok before MapStruct in `pom.xml`

### Layered Structure

```
Controller → Service (interface + impl) → Repository → MongoDB Document
                ↕
           DTO ↔ Mapper ↔ Entity
```

- **Controllers** return `Mono<ResponseEntity<T>>` or `Flux<T>`; all responses are wrapped in `GenericResponse<T>` (status, message, data list)
- **Services** implement `ICRUD<T, ID>` and extend `CRUDImpl<T, ID>`, which provides default reactive CRUD with `switchIfEmpty(Mono.error(new ModelNotFoundException(...)))` for not-found cases
- **Repositories** extend `IGenericRepo<T, ID>` → `ReactiveMongoRepository<T, ID>`
- **Global error handling** via `GlobalErrorHandler` (@RestControllerAdvice): maps `ModelNotFoundException` → 404, `WebExchangeBindException` → 400, `AccessDeniedException` → 403

### Domain Model
- **Book** embeds Category and Author as snapshots (denormalized). BookDTO uses flat `idCategory`/`idAuthor` fields; `BookMapper` reconstructs the embedded objects.
- **Sale** embeds Client snapshot and a list of `SaleDetail` subdocuments.
- Parallel reactive lookups use `Mono.zip()` (e.g., fetching Category + Author when saving a Book).

### MongoDB Configuration
- URI: `mongodb://root:123@localhost:27017/mito_books?authSource=admin` (set in `application.yml`)
- Credentials in `.env`, loaded by `docker-compose.yml`
- `application.yml` sets `spring.main.web-application-type: reactive` to force WebFlux (no Servlet/Tomcat)

## Code Conventions

- Use `Mono`/`Flux` in all public service/controller signatures — no blocking I/O
- Use `ReactiveMongoRepository`, not JPA; use `WebClient`, not `RestTemplate`
- English for identifiers and code comments
- MapStruct mappers are in `com.mitocode.mapper`; add new mappers as interfaces with `@Mapper(componentModel = "spring")`
- New entities follow the pattern: `model/` → `repo/` (extends `IGenericRepo`) → `service/` (interface extends `ICRUD` + impl extends `CRUDImpl`) → `controller/` → `dto/` + `mapper/`