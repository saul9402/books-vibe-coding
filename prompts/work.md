# Work Context for Agents

## Backend (Spring Boot MVC)
- Root: `/mito-books`
- Packages of interest:
  - Controllers: `/src/main/java/com/mitocode/controller/**`
  - Services: `/src/main/java/com/mitocode/service/**` and `/src/main/java/com/mitocode/service/impl/**`
  - Repositories: `/src/main/java/com/mitocode/repo/**`
  - Models (JPA entities): `/src/main/java/com/mitocode/model/**`
- Tech:
  - Spring Boot 3 (MVC)
  - Spring Data JPA
  - PostgreSQL (via `docker-compose.yml`)
- There is **no OpenAPI/Swagger**. Endpoints must be derived from controller annotations (`@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`), method signatures, and DTOs/entities.

**Resources:**
- `Book`, `Category`, `Client`, `Sale`
  - Typical fields: IDs (UUID or Long), names, prices, relations (`@ManyToOne`, `@OneToMany`).

## Frontend (Angular 20 Target)
- Target folder: `../mito-books-ui/` (created outside backend).


## Environment:
export const environment = { apiUrl: 'http://localhost:8080' };