# Agents for "Mito Books" Frontend Generation (Angular 20)

**Project config (TOML):**
@{.gemini/project.toml}

> Read the TOML block above. Use the `[project]` keys as authoritative config:
- `name`, `framework`, `frontend`, `api_base`.

<!-- context: ./base-instructions.md ./task.md ./work.md -->
<!--
All agents below use the base instructions, task goals, and technical context.
This file is the main entry point for Gemini CLI.
-->

> Purpose: Orchestrate agents to produce an Angular 20 frontend (standalone + Angular Material + Signals + new control flow) from a Spring Boot MVC backend **without** OpenAPI. Endpoints must be discovered by reading Spring controllers.

The current project is {{project.name}}, built with {{project.framework}} and agent must create frontend with {{project.frontend}}.

Apply all this steps automcatically

---

## Agent: planner
**Goal:** Produce a concise, executable plan to build the Angular frontend based on the Spring Boot backend.

Set up the frontend project structure **outside** the `mito-books` backend folder.

BASE/
├── mito-books/          # Spring Boot backend
└── mito-books-ui/       # Angular 20 frontend

**Deliverables:**
- Resource inventory (Book, Category, Client, Sale).
- View map: List, Detail, Create/Edit per resource.
- Ordered list of tasks for the following agents.
**Rules:**
- Read all controller classes to infer API endpoints.
- Ensure the execution order: endpoint-discovery → client-gen → routing → ui-component → ui-component-edit → signals-state..
---

## Agent: endpoint-discovery
**Goal:** Discover REST endpoints by reading controller annotations and method signatures.
**Deliverables:**
- A table `{method, path, params, requestBody, responseType}` per resource.
- List assumptions if annotations are incomplete.
**Rules:**
- Derive data models from JPA entities in `/model/`.
- Do not invent endpoints; match method names and HTTP verbs.

---

## Agent: client-gen
**Goal:** Generate Angular HttpClient services for each resource.
**Deliverables:**
- `src/app/services/<resource>-.service.ts`
- Type-safe interfaces (`types.ts`).
**Rules:**
- Use `environment.apiUrl` as the base URL.
- Use RxJS observables.
- Handle errors with `HttpErrorResponse`.
- No UI logic here.

---

## Agent: routing
**Goal:** Define standalone routes and feature routing modules.
**Deliverables:**
- `src/app/app.routes.ts`
- No lazy loading is needed
**Rules:**
- Standalone components only (no NgModules).
- Path structure: `/books`, `/books/new`, `/books/edit/:id`. #new and edit path use the **same component**

---

## Agent: ui-component
**Goal:** Generate List views with Angular Material and new control flow syntax.
**Deliverables:**
- `BookComponent` and similar components.
- HTML with `@for`, `@if`, `@else`.
**Rules:**
- Use `MatTable`, `MatPaginator`, `MatSort`.
- Include toolbar actions (New, Edit, Delete).
- Responsive and accessible layout.

---

## Agent: ui-component-edit
**Goal:** Create standalone reactive form components.
**Deliverables:**
- `<Resource>-edit.component` with validation and Angular Material fields.
**Rules:**
- Use `MatFormField`, `MatInput`, `MatSelect`.
- Map form values to DTOs.
- Show errors via `MatSnackBar`.
- Use `@if` for error or success states.

---

## Agent: signals-state
**Goal:** Manage local UI state with Angular Signals.
**Deliverables:**
- Integrate `signal`, `computed`, and `effect` in list and form components.
**Rules:**
- Use Signals only for local state (loading, selection, errors).

---

<!-- @ignore start -->
## Agent: tests
**Goal:** Provide basic unit tests.
**Deliverables:**
- Service tests with `HttpClientTestingModule`.
- Component tests that verify rendering, bindings, and form validation.
**Rules:**
- At least 1–2 tests per resource (one service, one component).

<!-- @ignore end -->
---

<!-- @ignore start -->
## Agent: fixer
**Goal:** Ensure compilation and linting succeed.
**Rules:**
- Fix imports, providers, routes, and Angular Material modules.
- Verify all standalone components compile without NgModules.
<!-- @ignore end -->