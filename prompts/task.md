# Task: Generate Angular 20 Frontend (Standalone + Material + Signals)

**Goal:** Build a production-ready Angular 20 frontend for the "Mito Books" Spring Boot MVC backend.

**Functional scope (per resource: Book, Category, Client, Sale):**
- List page with table (search/filter/client pagination).
- Create/Edit forms with validation.
- Delete with confirmation.

- Components creation example
   src/app   
   └── components/
         └── book/
            └── /book-edit    #in edit component use form to has new and edit functionality
         └── client/
            └── /client-edit  #in edit component use form to has new and edit functionality
         └── sale/
            └── /sale-edit   #in edit component use form to has new and edit functionality
         └── category/
            └── /category-edit  #in edit component use form to has new and edit functionality
 

**UI/UX constraints:**
- Angular 20 **standalone components**.
- **Angular Material** for layout and controls.
- **Signals** for local UI state (loading, selection, errors).
- Use **new control flow** (`@for`, `@if`, `@else`) in templates.
- Responsive and accessible (ARIA where relevant). Use `MatSnackBar` for feedback.

**Data & integration:**
- There is **no Swagger/OpenAPI**. Endpoints must be **discovered from Spring controllers**.
- Use `HttpClient` services, base URL from `environment.apiUrl`.
- Strong typing based on JPA entities.

**Deliverables:**
1. `environment.ts` with `apiUrl`.
2. API services (`src/app/services/*.service.ts`) + types.
3. Not use zoneless
4. Feature folders with routes and components:
   - `src/app/components/books/*`
   - `src/app/components/categories/*`
   - `src/app/components/clients/*`
   - `src/app/components/sales/*`
5. Build passes and app runs.

**Non-goals:**
- Global state libraries (NgRx) — not required for this module.
- Authentication/authorization flows — out of scope.
