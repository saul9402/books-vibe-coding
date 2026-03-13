---
name: dev-policy
description: >
  Each modify code must used. Development lifecycle policy. Enforces: PLAN → APPROVE → BUILD → VERIFY → LOOP.
  Use this skill BEFORE any development task. Triggers: "crear", "desarrollar", "implementar",
  "agregar", "build", "create", "add", "implement", "new feature", "nuevo", "quiero que hagas",
  "hazme", "construir", "develop". This skill intercepts development requests and enforces
  structured planning, execution with real testing, and iterative completion.
  IMPORTANT: This skill takes PRIORITY over direct development. Never start coding
  without first completing the planning phase.
version: 1.0.0
---

# Dev Policy — Plan, Build, Verify, Loop

## Purpose

This is a **governance skill** that enforces a disciplined development lifecycle. It ensures NO code is written without a plan, NO delivery is assumed working without real tests, and NO incomplete work is shipped.

**Announce at start:** "Activando dev-policy: Plan -> Approve -> Build -> Verify -> Loop"

## Lifecycle Phases

```
  [REQUEST]
      |
      v
  [1. PLAN]  -----> planning/<feature>/PLAN.md
      |
      v
  [2. APPROVE] ---> User says "aprobado" / "approved"
      |
      v
  [3. BUILD]  -----> Execute development (use MCPs, skills)
      |
      v
  [4. VERIFY] -----> planning/<feature>/RESULT.md + real tests
      |               |
      |          pass? |
      |           NO --+---> [3. BUILD] (loop back, fix issues)
      |           YES -+
      v
  [5. DONE]   -----> planning/<feature>/RESULT.md updated: COMPLETED
```

---

## Phase 1: PLAN

### When Triggered

Any time the user requests a development task (new feature, entity, endpoint, page, bug fix, refactor).

### What to Produce

Create directory: `planning/<feature-name>/`

Create file: `planning/<feature-name>/PLAN.md`

### Plan Template

```markdown
# Plan: <Feature Name>

**Fecha:** YYYY-MM-DD
**Solicitado por:** Usuario
**Estado:** PENDIENTE APROBACION

---

## Objetivo
[1-2 sentences: what will be built and why]

## Alcance
- [ ] [Deliverable 1]
- [ ] [Deliverable 2]
- [ ] [Deliverable N]

## Arquitectura
**Patrón:** [MVC Classic | Hexagonal | Frontend React | Full-stack]
**Capas afectadas:** [list: model, dto, repo, service, controller, frontend, etc.]

## Archivos a Crear
| # | Ruta | Tipo | Descripción |
|---|------|------|-------------|
| 1 | `src/main/java/...` | Java class | ... |
| 2 | `frontend/src/...` | React component | ... |

## Archivos a Modificar
| # | Ruta | Cambio |
|---|------|--------|
| 1 | `src/main/.../GlobalErrorHandler.java` | Add exception handler |

## Dependencias
- [ ] Requiere backend corriendo (Spring Boot)
- [ ] Requiere frontend corriendo (Vite dev server)
- [ ] Requiere base de datos (PostgreSQL/Docker)
- [ ] Requiere MCP: context7 (documentacion actualizada)
- [ ] Requiere MCP: puppeteer (testing visual)
- [ ] Requiere MCP: shadcn (componentes React)
- [ ] Requiere MCP: supabase/postgres (datos)

## Tareas (ordenadas por dependencia)

### Tarea 1: [Nombre]
**Archivos:** `ruta/exacta.java`
**Pasos:**
1. [Paso concreto con código esperado]
2. [Paso concreto]

### Tarea 2: [Nombre]
...

## Criterios de Verificacion
Cada criterio DEBE ser testeado, no asumido.

| # | Criterio | Cómo verificar | Herramienta |
|---|----------|----------------|-------------|
| 1 | Endpoint GET /entities retorna 200 | `curl localhost:8080/entities` | Bash |
| 2 | UI muestra tabla de datos | Puppeteer screenshot | MCP puppeteer |
| 3 | Formulario crea registro | Puppeteer fill + click + verify | MCP puppeteer |
| 4 | Unit tests pasan | `./mvnw test` | Bash |
| 5 | Build compila sin errores | `./mvnw clean package -DskipTests` | Bash |

## Riesgos
- [Risk 1 and mitigation]

---
**APROBACION REQUERIDA: Escribir "aprobado" para iniciar desarrollo.**
```

### Planning Rules

1. **Be specific** — Exact file paths, exact class names, exact method signatures
2. **Order by dependency** — Don't plan a controller before its service
3. **Every deliverable gets a verification criterion** — NO exceptions
4. **Identify ALL MCPs needed** — Tag them in the dependencies section
5. **List ALL files** — Both new and modified. No surprises during build.

---

## Phase 2: APPROVE

**DO NOT proceed to Build until the user explicitly approves.**

Present the plan and ask:

```
Plan creado en planning/<feature>/PLAN.md

Resumen:
- X archivos nuevos, Y archivos modificados
- Patrón: [MVC/Hexagonal/React/Full-stack]
- N criterios de verificación definidos
- MCPs requeridos: [list]

¿Aprobado? (escribe "aprobado" para iniciar, o indica cambios)
```

If the user requests changes, update `PLAN.md` and ask again. Loop until approved.

---

## Phase 3: BUILD

### Execution Rules

Execute tasks IN ORDER from the plan. For each task:

1. **Announce** what you're building: "Ejecutando Tarea N: [nombre]"
2. **Use the right MCP/skill** for the job (see MCP Guide below)
3. **Log progress** by updating the plan checklist mentally
4. **Don't skip ahead** — complete one task before starting the next

### MCP Usage Guide

#### Backend Development (Java/Spring Boot)

**context7 MCP** — Use for ALL library documentation lookups:
```
BEFORE writing Spring Boot code:
1. mcp__context7__resolve-library-id → get library ID for "spring-boot" or "spring-data-jpa"
2. mcp__context7__query-docs → get updated docs for the specific API you need

This saves tokens and ensures you use CURRENT APIs, not outdated patterns.
```

**When to use context7:**
- Adding a new Spring annotation you're unsure about
- Checking JPA query syntax
- Verifying Spring Boot 3.5.6 specific features
- Any library API where version matters (ModelMapper, springdoc, etc.)

**java-layered-arch skill** — Use for code generation:
- Triggers automatically when creating entities, services, controllers
- Provides exact patterns from this project (MVC and Hexagonal)

#### Frontend Development (React/TypeScript)

**shadcn MCP** — Use when building React UIs:
```
1. mcp__shadcn__search_items_in_registries → find components you need
2. mcp__shadcn__get_item_examples_from_registries → get usage examples
3. mcp__shadcn__get_add_command_for_items → get install commands
```

**context7 MCP** — Use for React/Tailwind/Vite docs:
```
1. mcp__context7__resolve-library-id → "react" or "tailwindcss"
2. mcp__context7__query-docs → query specific APIs
```

**web-generation skill** — Use for full page generation with design system

#### Testing & Verification

**puppeteer MCP** — Use for ALL visual/interactive testing:
```
1. mcp__puppeteer__puppeteer_navigate → open the page
2. mcp__puppeteer__puppeteer_screenshot → capture current state
3. mcp__puppeteer__puppeteer_click → click buttons, links
4. mcp__puppeteer__puppeteer_fill → fill form fields
5. mcp__puppeteer__puppeteer_evaluate → run JS assertions in browser
```

**webapp-testing skill** — Use for comprehensive QA audits

**Bash** — Use for backend verification:
```bash
# Compile check
./mvnw clean compile

# Run tests
./mvnw test

# Start backend for integration testing
./mvnw spring-boot:run &

# Test endpoints
curl -s http://localhost:8080/<endpoint> | python -m json.tool

# Start frontend
cd frontend && npm run dev &
```

### Server Lifecycle

When verification requires running servers:

```bash
# Backend: Start Spring Boot (background)
cd /path/to/project && ./mvnw spring-boot:run &
BACKEND_PID=$!

# Frontend: Start Vite dev server (background)
cd /path/to/project/frontend && npm run dev &
FRONTEND_PID=$!

# Wait for servers to be ready
sleep 10

# ... run tests with puppeteer/curl ...

# Cleanup after testing
kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
```

**IMPORTANT:** Always clean up server processes after testing.

---

## Phase 4: VERIFY

### Create Result File

After all tasks are executed, create: `planning/<feature-name>/RESULT.md`

### Result Template

```markdown
# Result: <Feature Name>

**Fecha ejecución:** YYYY-MM-DD
**Estado:** EN VERIFICACION

---

## Checklist del Plan

| # | Entregable | Estado | Evidencia |
|---|-----------|--------|-----------|
| 1 | [from plan] | PASS/FAIL | [what was checked] |
| 2 | [from plan] | PASS/FAIL | [what was checked] |

## Verificación de Criterios

| # | Criterio | Resultado | Detalle |
|---|----------|-----------|---------|
| 1 | GET /entities retorna 200 | PASS | `curl` returned 200 with 3 items |
| 2 | UI muestra tabla | PASS | Screenshot captured, table visible |
| 3 | Formulario crea registro | FAIL | Submit button throws 400, validation missing |

## Tests Ejecutados

### Backend
- [ ] `./mvnw compile` — PASS/FAIL
- [ ] `./mvnw test` — PASS/FAIL (N tests, M failures)
- [ ] Endpoints responden correctamente — PASS/FAIL

### Frontend
- [ ] `npm run build` — PASS/FAIL
- [ ] Puppeteer visual check — PASS/FAIL
- [ ] Interactive test (CRUD) — PASS/FAIL

## Issues Encontrados
| # | Issue | Severidad | Acción |
|---|-------|-----------|--------|
| 1 | [description] | ALTA/MEDIA/BAJA | [fix needed] |

---
**ESTADO FINAL:** COMPLETADO / INCOMPLETO (requiere iteración)
```

### Verification Rules — NEVER Skip

1. **Backend code MUST compile:** Run `./mvnw clean compile`. If it fails, it's not done.
2. **Unit tests MUST pass:** Run `./mvnw test`. If any fail, it's not done.
3. **Frontend MUST build:** Run `cd frontend && npm run build`. If it fails, it's not done.
4. **Visual verification is MANDATORY for UI changes:** Use Puppeteer MCP to screenshot and validate.
5. **CRUD operations MUST be tested interactively** for any new entity:
   - Navigate to the page
   - Create a record (fill form, submit, verify it appears)
   - Read/list records (verify table shows data)
   - Update a record (click edit, change fields, save, verify)
   - Delete a record (click delete, confirm, verify it's gone)
6. **API endpoints MUST return expected status codes:** Use `curl` or Puppeteer evaluate.

### What Counts as "Tested"

| This is NOT testing | This IS testing |
|--------------------|--------------  |
| "The code looks correct" | `./mvnw test` passes with 0 failures |
| "The endpoint should work" | `curl localhost:8080/x` returns expected JSON |
| "The UI should render" | Puppeteer screenshot shows the expected layout |
| "The form should submit" | Puppeteer fill + click + screenshot confirms new record |
| "It compiles in my head" | `./mvnw clean compile` exits with BUILD SUCCESS |

---

## Phase 5: LOOP (if incomplete)

If RESULT.md shows any FAIL or INCOMPLETO:

1. **Identify** which criteria failed
2. **Go back to BUILD** — fix only the failing items
3. **Re-run VERIFY** — test ONLY the previously failing criteria + regression on passing ones
4. **Update RESULT.md** — mark fixed items
5. **Repeat** until all criteria are PASS

### Loop Limit

After 3 iterations on the same issue, STOP and ask the user:

```
He intentado resolver [issue] 3 veces sin éxito.
Intentos:
1. [what I tried]
2. [what I tried]
3. [what I tried]

¿Quieres que intente un enfoque diferente, o prefieres intervenir manualmente?
```

---

## Directory Structure

```
planning/
├── add-publisher-entity/
│   ├── PLAN.md          ← Created in Phase 1
│   └── RESULT.md        ← Created in Phase 4
├── dashboard-page/
│   ├── PLAN.md
│   └── RESULT.md
└── fix-book-validation/
    ├── PLAN.md
    └── RESULT.md
```

The `planning/` folder is at project root. Add it to `.gitignore` if you don't want it tracked:
```
planning/
```

---

## Quick Reference: MCP Decision Matrix

| Task | Primary MCP | Secondary | Skill |
|------|------------|-----------|-------|
| Spring Boot code | context7 | — | java-layered-arch |
| React component | shadcn | context7 | web-generation |
| Visual test | puppeteer | — | webapp-testing |
| CRUD test (interactive) | puppeteer | — | webapp-testing |
| API test | Bash (curl) | — | — |
| Unit test | Bash (mvnw) | — | java-layered-arch |
| DB query | postgres/supabase | — | — |
| Documentation lookup | context7 | — | — |
| Full frontend build | shadcn + context7 | puppeteer (verify) | web-generation |
| Full backend feature | context7 | Bash (verify) | java-layered-arch |

---

## Examples

### Example 1: "Crea una entidad Publisher"

1. **PLAN** → `planning/add-publisher-entity/PLAN.md`
   - Pattern: MVC Classic
   - Files: Publisher.java, PublisherDTO.java, IPublisherRepo.java, IPublisherService.java, PublisherServiceImpl.java, PublisherController.java
   - Verification: compile, test, curl GET/POST/PUT/DELETE
   - MCP: context7 (Spring Data JPA docs)

2. **APPROVE** → User says "aprobado"

3. **BUILD** → Generate all 6 files using java-layered-arch patterns
   - Use context7 to verify JPA annotations if needed

4. **VERIFY** → `planning/add-publisher-entity/RESULT.md`
   - `./mvnw clean compile` → PASS
   - `./mvnw test` → PASS
   - Start server, `curl localhost:8080/publishers` → 200 OK
   - POST a publisher → 201 Created
   - GET by ID → 200 with data

5. **DONE** → RESULT.md marked COMPLETADO

### Example 2: "Hazme una página de dashboard"

1. **PLAN** → `planning/dashboard-page/PLAN.md`
   - Pattern: React + shadcn/ui
   - Files: Dashboard.tsx, components/StatsCard.tsx, etc.
   - Verification: npm run build, Puppeteer screenshots, responsive check
   - MCPs: shadcn (components), context7 (React/Tailwind docs), puppeteer (verify)

2. **APPROVE** → User says "aprobado"

3. **BUILD** → Generate React components
   - Use shadcn MCP for DataTable, Card, Chart components
   - Use context7 for React 19 patterns
   - Use web-generation skill design system

4. **VERIFY** → `planning/dashboard-page/RESULT.md`
   - `npm run build` → PASS
   - Start dev server, Puppeteer navigate → screenshot
   - Check responsive (375px, 768px, 1024px, 1440px)
   - Click interactive elements → verify behavior

5. **LOOP** if any visual issues → fix CSS, re-screenshot
6. **DONE** → RESULT.md marked COMPLETADO
