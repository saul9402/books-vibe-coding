# Plan: Frontend React — MitoBooks

**Fecha:** 2026-03-12
**Solicitado por:** Usuario
**Estado:** PENDIENTE APROBACION

---

## Objetivo
Crear un frontend React (Vite + TypeScript + Tailwind CSS v4 + shadcn/ui) que consuma la API REST de MitoBooks (Spring WebFlux en `localhost:8080`), cubriendo las entidades principales: Books, Clients, Categories, Authors y Sales.

## Alcance
- [ ] Proyecto Vite + React 19 + TypeScript en `../mito-books-react`
- [ ] Configuración de Tailwind CSS v4 y shadcn/ui
- [ ] Cliente HTTP (fetch/axios) apuntando a `http://localhost:8080`
- [ ] Dashboard con tarjetas de resumen (conteo de books, clients, authors, categories)
- [ ] Página CRUD Books (tabla + modal crear/editar + eliminar)
- [ ] Página CRUD Clients (tabla + modal crear/editar + eliminar)
- [ ] Página CRUD Categories (tabla + modal crear/editar + eliminar)
- [ ] Página CRUD Authors (tabla + modal crear/editar + eliminar)
- [ ] Página de listado Sales (tabla solo lectura)
- [ ] Sidebar de navegación responsivo
- [ ] Routing con React Router v6

## Arquitectura
**Patrón:** Frontend SPA (React + Vite)
**Capas afectadas:** solo frontend (nuevo proyecto)
**Backend:** Spring WebFlux `http://localhost:8080` (ya existente, CORS habilitado con `@CrossOrigin(origins = "*")`)

## Archivos a Crear
| # | Ruta | Tipo | Descripción |
|---|------|------|-------------|
| 1 | `../mito-books-react/` | Proyecto Vite | Scaffolding inicial |
| 2 | `src/lib/api.ts` | TypeScript | Cliente HTTP base con tipos |
| 3 | `src/types/index.ts` | TypeScript | Interfaces: Book, Client, Author, Category, Sale, GenericResponse |
| 4 | `src/components/Layout.tsx` | React | Sidebar + header layout |
| 5 | `src/components/Sidebar.tsx` | React | Navegación lateral |
| 6 | `src/pages/Dashboard.tsx` | React | Home con stats cards |
| 7 | `src/pages/Books.tsx` | React | CRUD Books |
| 8 | `src/pages/Clients.tsx` | React | CRUD Clients |
| 9 | `src/pages/Categories.tsx` | React | CRUD Categories |
| 10 | `src/pages/Authors.tsx` | React | CRUD Authors |
| 11 | `src/pages/Sales.tsx` | React | Listado Sales |
| 12 | `src/App.tsx` | React | Router principal |
| 13 | `src/main.tsx` | React | Entry point |

## Archivos a Modificar
| # | Ruta | Cambio |
|---|------|--------|
| 1 | `../mito-books-react/index.html` | Título y meta |
| 2 | `../mito-books-react/vite.config.ts` | Proxy `/api` → `localhost:8080` |

## Dependencias
- [ ] Requiere backend corriendo (Spring Boot en `localhost:8080`)
- [ ] Node.js / npm disponible
- [ ] MCP: context7 (docs React 19, Tailwind v4)

## Tareas (ordenadas por dependencia)

### Tarea 1: Scaffolding del proyecto Vite
**Ruta:** `../mito-books-react/`
**Pasos:**
1. `npm create vite@latest mito-books-react -- --template react-ts`
2. Instalar dependencias: `tailwindcss`, `@tailwindcss/vite`, `react-router-dom`, `axios`, `lucide-react`
3. Instalar shadcn/ui y sus peer deps

### Tarea 2: Configuración base (Tailwind, paths, vite proxy)
**Archivos:** `vite.config.ts`, `src/index.css`, `tailwind.config.ts`
**Pasos:**
1. Configurar Tailwind v4 con `@import "tailwindcss"`
2. Agregar proxy en vite para `/api` → `http://localhost:8080`
3. Inicializar shadcn: `npx shadcn@latest init`

### Tarea 3: Tipos e interfaces TypeScript
**Archivo:** `src/types/index.ts`
**Pasos:**
1. Definir `Book`, `Client`, `Author`, `Category`, `Sale`, `SaleDetail`, `GenericResponse<T>`

### Tarea 4: Cliente HTTP
**Archivo:** `src/lib/api.ts`
**Pasos:**
1. Crear funciones CRUD genéricas: `getAll`, `getById`, `create`, `update`, `remove`
2. Una sección por entidad: `booksApi`, `clientsApi`, `categoriesApi`, `authorsApi`, `salesApi`

### Tarea 5: Layout y Sidebar
**Archivos:** `src/components/Layout.tsx`, `src/components/Sidebar.tsx`
**Pasos:**
1. Sidebar con links a Dashboard, Books, Clients, Categories, Authors, Sales
2. Header con título de la sección activa
3. Layout con grid sidebar + main content

### Tarea 6: Página Dashboard
**Archivo:** `src/pages/Dashboard.tsx`
**Pasos:**
1. Cards con conteo de books, clients, authors, categories (fetch paralelo)
2. Tabla de últimas ventas

### Tarea 7: Páginas CRUD (Books, Clients, Categories, Authors)
**Archivos:** `src/pages/Books.tsx`, `src/pages/Clients.tsx`, `src/pages/Categories.tsx`, `src/pages/Authors.tsx`
**Pasos:**
1. Tabla con columnas relevantes usando shadcn Table
2. Botón "New" abre modal/dialog con formulario
3. Botones Edit/Delete por fila
4. Confirmación antes de eliminar

### Tarea 8: Página Sales (solo lectura)
**Archivo:** `src/pages/Sales.tsx`
**Pasos:**
1. Tabla con columnas: ID, Client, Date, Total, Status
2. Detalle expandible con `SaleDetail`

### Tarea 9: App Router
**Archivo:** `src/App.tsx`
**Pasos:**
1. BrowserRouter con rutas para todas las páginas
2. Layout wrapping todas las rutas autenticadas

## Criterios de Verificación
| # | Criterio | Cómo verificar | Herramienta |
|---|----------|----------------|-------------|
| 1 | `npm run build` sin errores | Ejecutar build | Bash |
| 2 | Dev server arranca | `npm run dev` → 200 en localhost:5173 | Bash |
| 3 | Dashboard muestra stats | Screenshot visual | Visual |
| 4 | Tabla Books carga datos del backend | Con backend corriendo | Manual |
| 5 | CRUD Books funciona (create/edit/delete) | Flujo manual | Manual |
| 6 | Navegación entre páginas funciona | React Router | Manual |

## Riesgos
- CORS: el backend ya tiene `@CrossOrigin(origins = "*")` en todos los controllers — no hay riesgo
- Node.js debe estar disponible en el sistema
- shadcn puede requerir configuración adicional de paths

---
**APROBACION REQUERIDA: Escribir "aprobado" para iniciar desarrollo.**
