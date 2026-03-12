# AGENTS.md

## Agent: plan
**Description:** Planner for the MVC → WebFlux + Mongo Reactive migration.
**Instructions:**
- Read the prompts in /prompts.
- Produce a short checklist of files to change and the order of execution.
- Never apply code changes; only plan.

## Agent: infra
**Description:** Infra migration (PostgreSQL→MongoDB) for this repo.
**Context:** May edit only `docker-compose.yml`, `.env`, and Spring config files.
**Guardrails:**
- Do not add healthchecks or mongo-express.
- Always show DIFF and ask before applying.
**Must do:**
- Replace postgres service with `mongo:7`, port `${MONGO_LOCAL_PORT}:27017`, volume `mongo-data:/data/db`.
- Switch `.env` to Mongo variables and comment out Postgres ones.
- Remove datasource/JPA properties because the project has docker-compose dependency in pom.xml (`spring.data.mongodb.uri`).

## Agent: pom
**Description:** Maven dependency migration.
**Instructions:**
- Replace JPA/JDBC with spring-boot-starter-data-mongodb-reactive.
- Ensure spring-boot-starter-webflux is present.
- Keep validation, lombok, modelmapper; add reactor-test (test scope).
- Show DIFF before applying.

## Agent: config
**Description:** App configuration migration.
**Instructions:**
- Remove datasource/Hikari settings.
- Add reactive Mongo settings (spring.data.mongodb.uri/database).
- Keep logging; show DIFF before applying.

## Agent: model
**Description:** JPA entity → Mongo @Document.
**Instructions:**
- Replace @Entity/@Table → @Document("<collection>").
- Use org.springframework.data.annotation.Id.
- Remove @GeneratedValue; use String/ObjectId.
- Add @Indexed where it makes sense.
- Show DIFF per file; small batches.

## Agent: repo
**Description:** Repository migration.
**Instructions:**
- JpaRepository → ReactiveMongoRepository<Domain, String>.
- Convert method returns to Mono/Flux; adapt queries (derived or @Query JSON).
- Show DIFF per file.

## Agent: service
**Description:** Service migration to reactive types.
**Instructions:**
- List→Flux, Optional/Entity→Mono.
- No blocking I/O; if unavoidable, wrap with boundedElastic and TODO comment.
- Propagate errors with Mono.error; show DIFF.

## Agent: web
**Description:** Controllers to WebFlux.
**Instructions:**
- Return Mono/Flux; ResponseEntity wrappers where needed.
- Replace RestTemplate by WebClient bean.
- Keep validation; add basic Slf4j logs.
- Show DIFF.

## Agent: tests
**Description:** Reactive tests.
**Instructions:**
- Controllers: @WebFluxTest + WebTestClient.
- Services: StepVerifier.
- Generate minimal but runnable tests.

## Agent: verify
**Description:** Global verifications.
**Instructions:**
- Use fileSearch to confirm no JpaRepository/@Entity/RestTemplate remain.
- Suggest fixes; run mvn verify (terminal) if asked.
- Report a final checklist.