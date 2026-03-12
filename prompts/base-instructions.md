# Base Instructions (read by all agents)

- Project name: **Mito Books**.
- Backend: Spring Boot 3 (MVC), Spring Data JPA, PostgreSQL.
- Frontend: Angular 20 + Angular Material, standalone components, Signals, new control flow.

- Code style:
- Standalone components only.
- Use SCSS and Angular Material.
- Prefer `@for` and `@if/@else` over legacy syntax.
- Use `environment.apiUrl` (no hardcoded URLs).
- Show feedback via `MatSnackBar`.
- One resource = one feature folder.
- Small, focused commits.
- Avoid generated noise or redundant files.
- Use only signals in components
- Use observables in services with HttpClient
