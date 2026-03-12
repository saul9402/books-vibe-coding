# AGENTS.md - MitoBooks AI Agent Guidelines

## Project Overview
**MitoBooks** is a Spring Boot 3.5.6 REST API (Java 21) for managing a bookstore system with sales tracking. It uses PostgreSQL, Spring Data JPA, ModelMapper, Lombok, and bean validation.

## Architecture & Key Patterns

### Layered Architecture
The project strictly follows a 3-layer pattern:
1. **Controllers** (`/controller`) - Handle HTTP requests, use `@Valid` for DTO validation
2. **Services** (`/service`) - Business logic via interfaces + implementations (`impl/`)
3. **Repositories** (`/repo`) - Data access extending `IGenericRepo<T, ID>`

**Critical insight**: Always implement service interfaces (e.g., `IBookService extends ICRUD<Book, Integer>`), never call repositories directly from controllers.

### Generic CRUD Interface
```java
// service/ICRUD.java - All services extend this
public interface ICRUD<T, ID> {
    T save(T t);
    T update(ID id, T t);
    List<T> findAll();
    T findById(ID id);
    void delete(ID id);
}
```
Specialized services add domain methods (e.g., `IBookService.getBooksByCategory(String name)`).

### DTO ↔ Entity Mapping
- **ModelMapper** beans configured in `/config/MapperConfig.java`
- Three mappers: `defaultMapper` (simple cases), `clientMapper` (field renaming), `saleMapper` (complex nested)
- Controllers inject mappers via `@Qualifier` and call `convertToDto()` / `convertToEntity()` methods
- DTOs enforce validation using `@NotNull`, `@Min`, `@Max` (jakarta.validation)

**Example**: `BookDTO` has `@NotNull @Min(1) @Max(100) idCategory` while `Book` has `@ManyToOne Category category`.

### Response Format
All endpoints return wrapped responses using `GenericResponse<T>`:
```java
new GenericResponse<>(200, "success", Arrays.asList(data))
new GenericResponse<>(404, "not-found", Arrays.asList(error))
```
This is enforced via `GlobalErrorHandler` for centralized exception handling.

### Exception Handling
- `GlobalErrorHandler` catches all exceptions and maps to `GenericResponse`
- Custom `ModelNotFoundException` throws for missing entities (caught as 404)
- `MethodArgumentNotValidException` catches DTO validation failures
- `AccessDeniedException` returns 403 FORBIDDEN

## Development Workflows

### Build & Run
```bash
# Maven wrapper (cross-platform)
./mvnw clean install              # Full build
./mvnw spring-boot:run            # Run application (connects to postgres via docker-compose)
./mvnw test                        # Run tests (minimal test suite exists)
```

### Database Setup
PostgreSQL 16 is containerized via `docker-compose.yml`:
- Container: `postgres_db_iad`
- Hibernate auto-creates schema with `ddl-auto: create` in `application.yml`
- Dialect: `PostgreSQLDialect`

### Testing Strategy
- Single test file: `MitoBooksApplicationTests.java` (context loading test only)
- **No comprehensive unit/integration tests** - AI agents should add tests when modifying services
- Follow Spring Boot testing patterns: `@SpringBootTest`, `@MockBean`, `TestRestTemplate`

## Project-Specific Conventions

### Naming Rules
- **Model Classes**: English with camelCase fields (`idBook`, `idCategory`) but `@Column` names use snake_case in DB
- **DTOs**: Mirror model names with "DTO" suffix, simplify nested objects to IDs
- **Controllers**: Plural naming (`/books`, `/categories`, `/clients`, `/sales`)
- **Repository Methods**: Use `@Query` with JPQL prefixed by domain (e.g., `getBooksByCategory`)

### Lombok Usage
- **@Data**: Auto-generates getters, setters, equals, hashCode, toString
- **@AllArgsConstructor / @NoArgsConstructor**: For JPA entities and DTOs
- **@RequiredArgsConstructor**: For dependency injection in controllers (`final` fields)

### Key Interdependencies
- **Book** → **Category** (ManyToOne, FK: `FK_BOOK_CATEGORY`)
- **Sale** → **Client** (ManyToOne) and **SaleDetail** → **Book** (OneToMany relationship)
- Services must validate foreign key references; use `service.findById()` before persisting

### Validation Patterns
- **Client-side**: Bean validation in DTOs (`@NotNull`, `@Min`, `@Max`)
- **Server-side**: Service layer throws `ModelNotFoundException` if ID not found
- Example: `BookDTO.idCategory` ranges 1-100; service must verify category exists before saving

## IDE & Commit Strategy
- **IDE**: IntelliJ IDEA (detected from target structure)
- **VCS**: Git (standard Spring Boot project)
- **Maven Wrapper**: Use `./mvnw` or `mvnw.cmd` (Windows) to avoid JDK version conflicts

## Common AI Agent Tasks

### Adding New Entity
1. Create `Model` class in `/model` with Lombok + JPA annotations
2. Create corresponding `DTO` in `/dto` with validation
3. Add `IRepository extends IGenericRepo<T, ID>` in `/repo`
4. Create `IService extends ICRUD<T, ID>` and `ServiceImpl extends CRUDImpl<T, ID>`
5. Add ModelMapper bean in `/config/MapperConfig.java` if field mapping needed
6. Create `Controller` with `@RestController @RequestMapping("/resource-name")`
7. Update `GlobalErrorHandler` if custom exceptions needed

### Modifying Service Logic
- Always extend `CRUDImpl` in implementations for inherited methods
- Use `@Query` JPQL in repositories, not native SQL
- Leverage `ModelMapper` in controllers, not services (keep services data-agnostic)

### Testing Additions
- Mock repositories with `@MockBean` in `@SpringBootTest` context
- Verify exception handling returns correct `GenericResponse` status codes
- Test DTOs validate constraints (e.g., idCategory bounds)

