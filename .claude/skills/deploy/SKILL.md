---
name: deploy
description: "Deploys the full-stack application using Supabase (cloud PostgreSQL database) and Vercel (frontend hosting). Use this skill whenever the user mentions deploying, publishing, going live, pushing to production, hosting the app, setting up cloud database, migrating to Supabase, deploying to Vercel, or any variation of 'deploy', 'ship', 'launch', 'go live', 'put online', 'subir a produccion', 'desplegar'. Also trigger when the user asks about connecting to a cloud database, setting up Supabase, or deploying the frontend."
allowed_tools:
  - mcp__vercel__list_teams
  - mcp__vercel__list_projects
  - mcp__vercel__get_project
  - mcp__vercel__deploy_to_vercel
  - mcp__vercel__list_deployments
  - mcp__vercel__get_deployment
  - mcp__vercel__get_deployment_build_logs
  - mcp__vercel__get_runtime_logs
  - mcp__vercel__check_domain_availability_and_price
  - mcp__vercel__get_access_to_vercel_url
  - mcp__vercel__web_fetch_vercel_url
  - mcp__vercel__search_vercel_documentation
  - mcp__supabase__list_organizations
  - mcp__supabase__get_organization
  - mcp__supabase__list_projects
  - mcp__supabase__get_project
  - mcp__supabase__get_project_url
  - mcp__supabase__get_publishable_keys
  - mcp__supabase__list_tables
  - mcp__supabase__list_migrations
  - mcp__supabase__apply_migration
  - mcp__supabase__execute_sql
  - mcp__supabase__get_logs
  - mcp__supabase__get_advisors
  - mcp__supabase__restore_project
  - mcp__supabase__list_extensions
  - mcp__supabase__generate_typescript_types
  - mcp__supabase__search_docs
  - Bash
  - Read
  - Write
  - Edit
  - Glob
  - Grep
  - mcp__filesystem__read_file
  - mcp__filesystem__read_multiple_files
  - mcp__filesystem__list_directory
  - mcp__filesystem__search_files
  - mcp__filesystem__get_file_info
  - mcp__filesystem__list_allowed_directories
  - mcp__railway__check-railway-status
  - mcp__railway__create-project-and-link
  - mcp__railway__list-projects
  - mcp__railway__list-services
  - mcp__railway__link-service
  - mcp__railway__link-environment
  - mcp__railway__set-variables
  - mcp__railway__list-variables
  - mcp__railway__deploy
  - mcp__railway__list-deployments
  - mcp__railway__get-logs
  - mcp__railway__generate-domain
  - mcp__railway__create-environment
---

# Deploy Skill

## File Verification with Filesystem MCP

Use the filesystem MCP to verify configuration files before deployment:

- **Find config files**: Use `mcp__filesystem__search_files` for `application*.yml` or `.env*` files
- **Verify deploy artifacts**: Use `mcp__filesystem__list_directory` to check `deploy/` folder contents
- **Read multiple configs**: Use `mcp__filesystem__read_multiple_files` to inspect all config files at once
- **Check SQL migrations**: Use `mcp__filesystem__search_files` for `*.sql` files in the project

Orchestrates the deployment of a full-stack Spring Boot + React application using **Supabase** (cloud PostgreSQL) and **Vercel** (frontend hosting). The backend requires a JVM-compatible host since Vercel only supports frontend/serverless workloads.

## CORS Configuration (Pre-requisite)

The backend uses a configurable CORS setup via the environment variable `CORS_ALLOWED_ORIGINS`. This is **critical** for the frontend to communicate with the backend across different domains.

### How it works

- **`CorsConfig.java`** reads `cors.allowed-origins` from `application.yml`
- **`application.yml`** maps it to `${CORS_ALLOWED_ORIGINS:*}` (defaults to `*` for local dev)
- In production, set `CORS_ALLOWED_ORIGINS` to the Vercel frontend URL (e.g., `https://myapp.vercel.app`)
- Multiple origins are supported as comma-separated values (e.g., `https://myapp.vercel.app,https://custom-domain.com`)
- When origins are NOT `*`, `allowCredentials` is automatically enabled

### Local development

No changes needed — the default `*` allows all origins, and the Vite dev proxy handles API routing.

### Production deployment

Set the environment variable on the backend host (Railway/Render/Docker):
```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

If you have a custom domain on Vercel, include both:
```
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app,https://your-custom-domain.com
```

---

## Deployment Flow

Execute these phases in order. Each phase uses specific MCP tools. Always confirm with the user before proceeding to the next phase.

---

### Phase 0: Project Analysis & Domain Selection

Before any deployment, analyze the project to suggest smart domain names and let the user choose their preferred Vercel project name.

#### Step 0.1 — Read project metadata

Collect the following automatically (no user input needed yet):
- `package.json` → `name`, `description` (frontend)
- `CLAUDE.md` → project overview, entity names
- `src/main/java/com/mitocode/model/` → entity class names (to understand domain)
- Check existing Vercel projects with `mcp__vercel__list_projects` to avoid name collisions

#### Step 0.2 — Generate domain name suggestions

Based on the project name and entities, generate **5 candidate names** following these rules:
- Short (2-3 words max), lowercase, hyphened
- Reflect the domain (e.g., bookstore → "mito-books", "libro-store", "bookshelf-api")
- Include both brand-style (`mitobooks`) and descriptive-style (`book-manager`) options
- Avoid names already taken in the existing Vercel projects list

Example for a bookstore app named "mito-books":
```
Suggested Vercel project names:
  1. mito-books          → https://mito-books.vercel.app
  2. mitobooks-app       → https://mitobooks-app.vercel.app
  3. mito-bookstore      → https://mito-bookstore.vercel.app
  4. libros-mito         → https://libros-mito.vercel.app
  5. mito-catalog        → https://mito-catalog.vercel.app
```

#### Step 0.3 — Check custom domain availability (optional)

If the user wants a custom domain (not just `.vercel.app`), check availability:

```
Tool: mcp__vercel__check_domain_availability_and_price
  names: ["mitobooks.com", "mito-books.app", "mitobooks.io", "mito-books.com"]
```

Show results clearly:
```
Domain availability:
  ✅ mito-books.app     — $15/year
  ✅ mitobooks.io       — $35/year
  ❌ mitobooks.com      — taken
  ❌ mito-books.com     — taken
```

#### Step 0.4 — Ask user to confirm project name

Present the suggestions and ask:
> "¿Cuál nombre quieres usar para el proyecto en Vercel? El frontend quedará en `https://<name>.vercel.app`. Si quieres un dominio personalizado, puedo ayudarte a comprarlo también."

Wait for user confirmation before continuing. Store the chosen name as `VERCEL_PROJECT_NAME` — it will be used in Phase 3.

---

### Phase 1: Supabase Database Setup

Use the Supabase MCP tools to configure the cloud database.

#### Step 1.1 — Get project connection info

```
Tool: mcp__supabase__get_project_url
Tool: mcp__supabase__get_publishable_keys
```

Save the project URL and anon key — the frontend will need these if using Supabase client directly.

#### Step 1.2 — Review current local schema

Read the entity classes to understand the database schema:
- Check `src/main/java/com/mitocode/model/` for all `@Entity` classes
- Note relationships (`@ManyToOne`, `@OneToMany`, cascade types)
- Note column constraints (`@Column`, `nullable`, `length`)

#### Step 1.3 — Create migration SQL

Generate a SQL migration script from the entity model. The script should:
- Use `CREATE TABLE IF NOT EXISTS` for safety
- Include all constraints, foreign keys, and indexes
- Respect the entity relationships defined in the Java model

```
Tool: mcp__supabase__apply_migration
  name: "initial_schema"
  sql: "<the generated SQL>"
```

#### Step 1.4 — Verify migration

```
Tool: mcp__supabase__list_tables
Tool: mcp__supabase__list_migrations
```

Confirm all tables were created correctly.

#### Step 1.5 — Seed data (optional)

If the project has `data.sql` or `import.sql`, convert and apply:

```
Tool: mcp__supabase__execute_sql
  sql: "INSERT INTO ..."
```

#### Step 1.6 — Update Spring Boot configuration

Modify `application.yml` to use the Supabase connection string:

```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}

spring:
  datasource:
    url: jdbc:postgresql://db.<PROJECT_REF>.supabase.co:5432/postgres
    username: postgres
    password: <DB_PASSWORD>
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # IMPORTANT: change from 'update' to 'validate' for production
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    properties:
      hibernate:
        default_schema: public
```

**Critical**: Change `ddl-auto` from `update` to `validate` — otherwise the app may alter tables on every restart.

Use environment variables for sensitive values:
```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}

spring:
  datasource:
    url: ${SUPABASE_DB_URL}
    username: ${SUPABASE_DB_USER:postgres}
    password: ${SUPABASE_DB_PASSWORD}
```

---

### Phase 2: Backend Deployment

Vercel cannot run Spring Boot applications (JVM). Present these options to the user:

#### Option A: Railway (Recommended — use MCP tools, no dashboard needed)

**Pre-requisite**: Run `railway login` once in terminal (browser auth). Then all steps are via MCP.

##### Step A.1 — Verify Railway CLI is authenticated
```
Tool: mcp__railway__check-railway-status
```

##### Step A.2 — Create Railway project and link it
```
Tool: mcp__railway__create-project-and-link
  projectName: "mito-books-backend"
  workspacePath: "<absolute path to project root>"
```

##### Step A.3 — Link the service
```
Tool: mcp__railway__list-services
  workspacePath: "<absolute path>"

Tool: mcp__railway__link-service
  workspacePath: "<absolute path>"
  serviceName: "mito-books-backend"
```

##### Step A.4 — Set ALL environment variables (skip deploy on first set)
```
Tool: mcp__railway__set-variables
  workspacePath: "<absolute path>"
  skipDeploys: true
  variables:
    - "SPRING_PROFILES_ACTIVE=prod"
    - "SUPABASE_DB_URL=jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
    - "SUPABASE_DB_USER=postgres.<PROJECT_REF>"
    - "SUPABASE_DB_PASSWORD=<password>"
    - "CORS_ALLOWED_ORIGINS=*"
```

**IMPORTANT — Supabase connection on Railway:**
- The direct Supabase host (`db.<ref>.supabase.co`) resolves to **IPv6 only** → Railway cannot reach it
- Use the **Session Pooler** instead: `aws-0-<region>.pooler.supabase.com:5432`
- Username format for pooler: `postgres.<PROJECT_REF>` (NOT just `postgres`)
- If you get "Tenant or user not found": get the exact connection string from Supabase Dashboard → Project Settings → Database → Connection string → URI

##### Step A.5 — Deploy via bash (railway up)
The MCP `deploy` tool sometimes fails. Use bash directly:
```bash
cd <project_root>
railway up
```

##### Step A.6 — Check deployment status
```
Tool: mcp__railway__list-deployments
  workspacePath: "<absolute path>"
  json: true
  limit: 1
```

Status values: `BUILDING` → `DEPLOYING` → `SUCCESS` | `CRASHED` | `FAILED`

##### Step A.7 — Read logs if crashed
```
Tool: mcp__railway__get-logs
  workspacePath: "<absolute path>"
  deploymentId: "<id from list-deployments>"
  logType: "deploy"   ← or "build"
```

**Common errors and fixes:**
| Error | Cause | Fix |
|-------|-------|-----|
| `permission denied: ./mvnw` | mvnw not executable | Add `RUN chmod +x mvnw` in Dockerfile before first `./mvnw` call |
| `COPY target/*.jar: no such file` | Dockerfile copies JAR but doesn't build | Use multi-stage Dockerfile (build stage with JDK, run stage with JRE) |
| `Network unreachable` | Supabase direct host is IPv6 only | Switch to Supabase Session Pooler URL |
| `UnknownHostException` | Same IPv6 issue but with forced IPv4 | Switch to pooler URL |
| `Tenant or user not found` | Pooler username wrong | Use `postgres.<PROJECT_REF>` not just `postgres` |
| `FATAL: password authentication failed` | Wrong DB password | Check Supabase Dashboard → Settings → Database |

##### Step A.8 — Generate public domain
```
Tool: mcp__railway__generate-domain
  workspacePath: "<absolute path>"
```

##### Step A.9 — Update CORS with Railway URL
Once you have the Railway URL, update CORS (and set CORS_ALLOWED_ORIGINS to Vercel URL after frontend deploy):
```
Tool: mcp__railway__set-variables
  workspacePath: "<absolute path>"
  variables:
    - "CORS_ALLOWED_ORIGINS=https://<vercel-url>"
```

##### Dockerfile required for Railway (multi-stage)
```dockerfile
# Stage 1: Build with Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run with slim JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

#### Option B: Render

1. Push code to GitHub
2. Create a Web Service in Render
3. Build command: `./mvnw clean package -DskipTests`
4. Start command: `java -jar target/*.jar`
5. Set environment variables (same as Railway, including **`CORS_ALLOWED_ORIGINS`**)
6. Render provides a public URL

#### Option C: Docker + Any Cloud

Generate a `Dockerfile`:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and push to any container registry, deploy to any cloud (AWS ECS, GCP Cloud Run, Azure Container Apps, etc.).

#### After backend is deployed

Save the backend URL — the frontend needs it for API calls. Test the health endpoint:
```bash
curl https://<backend-url>/books
```

---

### Phase 3: Frontend Deployment to Vercel

Use the Vercel MCP tools to deploy the React frontend.

#### Step 3.1 — Configure API base URL

The frontend already reads `VITE_API_URL` from environment variables:

```typescript
// frontend/src/lib/api.ts
const API_BASE = import.meta.env.VITE_API_URL ?? ""
```

This will be set as a Vercel environment variable in Step 3.4.

#### Step 3.2 — Check Vercel project status

```
Tool: mcp__vercel__list_projects
```

If no project exists yet, the deploy tool will create one.

#### Step 3.3 — Deploy frontend

```
Tool: mcp__vercel__deploy_to_vercel
```

Or use Vercel CLI via the MCP:
```
Tool: mcp__vercel__use_vercel_cli
  command: "vercel --prod"
```

**Important**: Set the root directory to `frontend/` in Vercel project settings since the repo is a monorepo.

#### Step 3.4 — Set environment variables

The frontend needs `VITE_API_URL` pointing to the backend:

Use the Vercel dashboard or CLI to set environment variables:
```bash
vercel env add VITE_API_URL production
# Enter: https://<your-backend-url>
```

**Trigger a redeploy** after setting env vars so Vite bakes the value into the build.

#### Step 3.5 — Verify deployment

```
Tool: mcp__vercel__list_deployments
Tool: mcp__vercel__get_deployment
```

Check build logs if there are issues:
```
Tool: mcp__vercel__get_deployment_build_logs
  deploymentId: "<id from list_deployments>"
```

#### Step 3.6 — Custom domain (optional)

```
Tool: mcp__vercel__check_domain_availability_and_price
  domain: "myapp.com"
```

---

### Phase 4: CORS — Connect Frontend ↔ Backend

This phase is **critical**. Without it, the browser will block all API requests from the frontend to the backend.

#### Step 4.1 — Get the Vercel frontend URL

After the frontend is deployed (Phase 3), note the URL Vercel assigned (e.g., `https://mito-books.vercel.app`).

#### Step 4.2 — Set CORS on the backend host

Go to the backend hosting dashboard (Railway/Render/etc.) and set the environment variable:

```
CORS_ALLOWED_ORIGINS=https://mito-books.vercel.app
```

If you also use a custom domain:
```
CORS_ALLOWED_ORIGINS=https://mito-books.vercel.app,https://your-custom-domain.com
```

**Restart the backend** after setting the variable so `CorsConfig.java` picks up the new origins.

#### Step 4.3 — Verify CORS

Test with a preflight request:
```bash
curl -X OPTIONS https://<backend-url>/books \
  -H "Origin: https://mito-books.vercel.app" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

The response should include:
```
Access-Control-Allow-Origin: https://mito-books.vercel.app
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Credentials: true
```

If you see `403` or no CORS headers, double-check that `CORS_ALLOWED_ORIGINS` matches exactly (protocol + domain, no trailing slash).

---

### Phase 5: Post-Deployment Verification

#### 5.1 — Database health check

```
Tool: mcp__supabase__get_logs
  service: "postgres"
```

```
Tool: mcp__supabase__get_advisors
```

#### 5.2 — Frontend verification

```
Tool: mcp__vercel__web_fetch_vercel_url
  url: "<deployed-frontend-url>"
```

#### 5.3 — End-to-end test

Guide the user to:
1. Open the frontend URL in a browser
2. Open browser DevTools → Network tab
3. Create a test record (e.g., add a book)
4. **Verify no CORS errors** appear in the Console tab
5. Verify the record appears in the list
6. Delete the test record

#### 5.4 — Common CORS issues checklist

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Access-Control-Allow-Origin` header missing | `CORS_ALLOWED_ORIGINS` not set | Set env var and restart backend |
| Origin mismatch | Trailing slash or wrong protocol | Ensure exact match (e.g., `https://app.vercel.app` not `https://app.vercel.app/`) |
| Preflight fails (OPTIONS 403) | OPTIONS method not allowed | Verify `allowedMethods` includes `OPTIONS` |
| Credentials error | `allowCredentials(true)` with `*` origin | Use specific origin, not `*` |
| Works in Postman but not browser | CORS is browser-only enforcement | This is expected — fix CORS config |

---

## Troubleshooting

### CORS Issues

The CORS configuration is handled automatically by `CorsConfig.java` reading from `application.yml`:

```java
// CorsConfig.java reads:
@Value("${cors.allowed-origins:*}")
private String allowedOrigins;
```

```yaml
# application.yml maps to env var:
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}
```

To fix CORS issues:
1. Verify `CORS_ALLOWED_ORIGINS` is set on the backend host
2. Ensure the value matches the frontend URL exactly (no trailing slash)
3. Restart the backend after changing the variable
4. If using multiple origins, separate with commas (no spaces)

### Database Connection Refused
- Verify the Supabase connection string format
- Check that the password doesn't contain special characters that need URL encoding
- Ensure `?sslmode=require` is appended to the JDBC URL for Supabase

### Build Failures on Vercel
- Ensure `frontend/` has its own `package.json`
- Set the root directory in Vercel to `frontend/` if the repo contains both backend and frontend
- Check Node.js version compatibility

---

## Environment Variables Summary

### Backend Host (Railway/Render/Docker)

| Variable | Value | Required |
|----------|-------|----------|
| `SUPABASE_DB_URL` | `jdbc:postgresql://db.xxx.supabase.co:5432/postgres?sslmode=require` | Yes |
| `SUPABASE_DB_USER` | `postgres` | Yes |
| `SUPABASE_DB_PASSWORD` | From Supabase dashboard | Yes |
| **`CORS_ALLOWED_ORIGINS`** | `https://your-app.vercel.app` | **Yes** |

### Vercel (Frontend)

| Variable | Value | Required |
|----------|-------|----------|
| `VITE_API_URL` | `https://your-backend.up.railway.app` | Yes |

---

## Quick Reference: MCP Tools

### Supabase MCP
| Tool | Purpose |
|------|---------|
| `get_project_url` | Get API URL |
| `get_publishable_keys` | Get anon/service keys |
| `list_tables` | Verify schema |
| `list_migrations` | Check migration history |
| `apply_migration` | Run SQL migration |
| `execute_sql` | Run arbitrary SQL |
| `get_logs` | Debug issues |
| `get_advisors` | Security/performance tips |
| `search_docs` | Search Supabase docs |

### Vercel MCP
| Tool | Purpose |
|------|---------|
| `list_projects` | See existing projects |
| `deploy_to_vercel` | Deploy the app |
| `list_deployments` | Check deployment status |
| `get_deployment` | Deployment details |
| `get_deployment_build_logs` | Debug build issues |
| `get_runtime_logs` | Debug runtime issues |
| `check_domain_availability_and_price` | Custom domain |
| `search_documentation` | Search Vercel docs |
