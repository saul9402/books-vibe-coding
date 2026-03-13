---
name: frontend-design
description: Create distinctive, production-grade frontend interfaces with high design quality. Use this skill when the user asks to build web components, pages, artifacts, posters, or applications (examples include websites, landing pages, dashboards, React components, HTML/CSS layouts, or when styling/beautifying any web UI). Generates creative, polished code and UI design that avoids generic AI aesthetics. Integrates with the shadcn MCP server for component research and design patterns.
license: Complete terms in LICENSE.txt
allowedTools:
  - mcp__filesystem__read_file
  - mcp__filesystem__read_multiple_files
  - mcp__filesystem__write_file
  - mcp__filesystem__create_directory
  - mcp__filesystem__list_directory
  - mcp__filesystem__directory_tree
  - mcp__filesystem__move_file
  - mcp__filesystem__search_files
  - mcp__filesystem__get_file_info
  - mcp__filesystem__list_allowed_directories
---

## Gestión de Archivos con Filesystem MCP

Usa el MCP de filesystem para gestionar archivos web en la carpeta Documents del usuario (`C:/Users/Axel/Documents`):

- **Buscar assets**: Usa `mcp__filesystem__search_files` para localizar imágenes, fuentes o archivos de referencia
- **Explorar carpetas**: Usa `mcp__filesystem__list_directory` para navegar directorios del usuario
- **Entregar output**: Usa `mcp__filesystem__move_file` para mover archivos generados a la ubicación preferida del usuario
- **Árbol de archivos**: Usa `mcp__filesystem__directory_tree` para mostrar estructura de carpetas disponibles

## Directorio de Salida

**Todos los archivos HTML/CSS/JS generados DEBEN guardarse en `generate/web/`.** Antes de crear cualquier archivo, ejecutar:
```bash
mkdir -p generate/web/
```

**Excepción**: Cuando se modifica un frontend existente del proyecto (ej. `src/main/resources/static/` o `frontend/`), escribir directamente en la estructura del proyecto.

---

## Idioma: Español (OBLIGATORIO)

**Todo el contenido generado DEBE estar en español.** Esto incluye:

- **Textos de interfaz**: títulos, subtítulos, descripciones, placeholders, tooltips, labels
- **Mensajes del sistema**: notificaciones (toast), alertas, confirmaciones, estados vacíos, errores
- **Navegación**: menús, breadcrumbs, sidebar, tabs, command palette
- **Botones y acciones**: "Guardar", "Eliminar", "Nuevo", "Buscar", "Cancelar", etc.
- **Tablas**: encabezados de columnas, paginación ("Anterior", "Siguiente", "Mostrando X de Y")
- **Metadatos HTML**: `<html lang="es">`, `<title>` en español

**Excepciones** (mantener en inglés):
- Nombres de variables, funciones y clases en el código fuente
- Nombres de componentes shadcn/React (ej. `<Button>`, `<Card>`)
- Términos técnicos sin traducción directa (ej. "dashboard", "responsive")

```tsx
// ✅ Correcto
<Button>Agregar Libro</Button>
<Input placeholder="Buscar por título o autor..." />
onToast("Libro creado")

// ❌ Incorrecto
<Button>Add Book</Button>
<Input placeholder="Search by title or author..." />
onToast("Book created")
```

---

## Separación de Archivos (OBLIGATORIO)

**NUNCA poner todo en un solo archivo.** Siempre separar responsabilidades:

### Proyectos React (PREFERIDO)
```
frontend/
├── src/
│   ├── components/
│   │   ├── ui/              ← componentes shadcn (auto-generados)
│   │   ├── layout/          ← Sidebar, Header, PageHeader
│   │   ├── dashboard/       ← StatCard, DashboardAnalytics (charts)
│   │   ├── [entidad]/       ← EntityPage, EntityForm por entidad
│   │   ├── command-palette.tsx
│   │   └── confirm-dialog.tsx
│   ├── hooks/               ← useTheme, useApi, useMobile
│   ├── lib/                 ← api.ts, utils.ts
│   ├── types/               ← interfaces TypeScript
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css            ← Tailwind + tema shadcn
├── components.json
├── vite.config.ts
└── package.json
```

### HTML/CSS/JS Vanilla
```
static/
├── index.html               ← Solo estructura, sin CSS/JS inline
├── css/styles.css            ← Todos los estilos
├── js/app.js                 ← Todo el JavaScript
└── assets/                   ← Imágenes, fuentes
```

**Reglas:**
- Un componente por archivo (React) o una responsabilidad por archivo (vanilla)
- CSS en archivos dedicados, nunca bloques `<style>` inline de más de 20 líneas
- JS en archivos dedicados, nunca bloques `<script>` inline
- Tipos/interfaces en su propio módulo

---

## Integración shadcn MCP (OBLIGATORIO)

Antes de diseñar cualquier UI, **siempre consultar el shadcn MCP** para investigar patrones modernos de componentes.

### Flujo de Investigación (Hacer CADA vez)

1. **Buscar componentes necesarios**
   → `mcp__shadcn__search_items_in_registries` con registries: `["@shadcn"]`

2. **Estudiar el bloque ejemplo más cercano**
   → `mcp__shadcn__get_item_examples_from_registries` (ej. "dashboard-01")

3. **Obtener detalles de componentes individuales**
   → `mcp__shadcn__view_items_in_registries` (ej. `["@shadcn/card", "@shadcn/table"]`)

4. **Obtener código demo de cada componente**
   → `mcp__shadcn__get_item_examples_from_registries` (ej. "card-demo", "table-demo")

5. **Obtener comandos de instalación para proyectos React**
   → `mcp__shadcn__get_add_command_for_items`

6. **Implementar** usando componentes shadcn reales (React) o traducir patrones (vanilla)

7. **Verificar con checklist de auditoría**
   → `mcp__shadcn__get_audit_checklist()`

> **Referencia completa**: Ver `knowledge/shadcn-catalog.md` para el catálogo de 50+ componentes, bloques y temas disponibles.

---

## Patrones de Producción

> **Referencia completa**: Ver `knowledge/design-patterns.md` para patrones detallados y directrices estéticas.

### Resumen de Patrones Obligatorios:
1. **Stat Cards** con contadores animados y sparklines
2. **Paleta de Comandos (Cmd+K)** — OBLIGATORIO en dashboards
3. **Skeleton Loading** — NUNCA usar spinners
4. **Page Headers** con breadcrumbs (Inicio / Página Actual)
5. **Sidebar mejorado** con logo, badges, atajos, avatar, toggle de tema
6. **Estados vacíos** con icono + título + descripción + botón CTA
7. **Atajos de teclado** (1-6 navegación, Ctrl+K paleta, Escape cerrar)
8. **Tema oscuro/claro** con clase CSS `.dark` + localStorage
9. **Charts con Recharts** — OBLIGATORIO en dashboards con datos. Donut para proporciones, barras para rankings/comparaciones. NUNCA barras de progreso div-based como sustituto de gráficos reales
10. **Paginación en tablas** — TODA tabla de entidades debe tener paginación (10 filas/página por defecto). Crear componente `TablePagination` reutilizable con controles primera/anterior/siguiente/última y contador "X–Y de N". Nunca renderizar todos los registros sin paginar
11. **DialogDescription obligatorio** — Todo `DialogContent` de Radix/shadcn DEBE incluir `DialogDescription` dentro del `DialogHeader`. Sin ella, Radix lanza un warning de accesibilidad en cada apertura. Usar texto contextual dinámico: `isEdit ? "Modifica los datos..." : "Completa los datos para crear..."`

---

## Setup React + shadcn

> **Referencia completa**: Ver `knowledge/react-setup.md` para comandos de instalación y patrones de código.

**Setup rápido:**
```bash
npm create vite@latest frontend -- --template react-ts
npm install -D tailwindcss @tailwindcss/vite
npx shadcn@latest init
npx shadcn@latest add button card badge table dialog input label select command sidebar separator breadcrumb skeleton sonner dropdown-menu tooltip avatar tabs checkbox chart
```

---

## Pensamiento de Diseño

Antes de codificar: entender el contexto y comprometerse con una dirección estética **AUDAZ**.

- **Propósito**: ¿Qué problema resuelve? ¿Quién lo usa?
- **Tono**: Elegir una dirección clara (minimalista, maximalista, retro-futurista, editorial, brutalista, etc.)
- **Diferenciación**: ¿Qué lo hace INOLVIDABLE?

**NUNCA** usar estéticas genéricas de IA: fuentes sobreusadas (Inter, Roboto, Arial), esquemas de color cliché, diseño tipo molde. Cada interfaz debe sentirse genuinamente diseñada para su contexto.
