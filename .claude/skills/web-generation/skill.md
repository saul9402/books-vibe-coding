---
name: web-generation
description: "Generates complete, production-grade web frontends from a backend API, Swagger spec, or feature description. Use this skill whenever the user asks to: build a web page, create a frontend, generate a UI from a backend, make a dashboard, design a web app, or anything like 'make me a page for this API', 'build the frontend', 'create a web interface', 'generate a UI', 'make me a web app'. Applies the user's preferred modern SaaS design system: vivid violet gradients, glassmorphism, animated orbs, dark sidebar, React 19 + TypeScript + Tailwind CSS v4 + shadcn/ui."
license: Complete terms in LICENSE.txt
---

# Web Generation — Design System & Preferences

Este skill captura las preferencias de diseño del usuario para generar frontends modernos. **SIEMPRE** aplicar este sistema de diseño a menos que el usuario lo indique explícitamente.

---

## Stack Tecnológico (Por defecto)

```
React 19 + TypeScript + Vite
Tailwind CSS v4 (con @theme inline, @custom-variant dark)
shadcn/ui (Radix UI primitives)
lucide-react (iconos)
recharts (gráficas)
```

**Comandos de setup:**
```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
npx shadcn@latest init
npm install lucide-react recharts
```

---

## Paleta de Colores — Sistema Vivid Modern

Usar siempre **oklch()** para todos los colores. Esta es la paleta canónica:

### Light mode
```css
--background: oklch(0.984 0.003 265);      /* casi blanco con tinte azul */
--foreground: oklch(0.138 0.020 265);      /* casi negro */
--primary: oklch(0.52 0.26 275);           /* violeta vivido */
--primary-foreground: oklch(0.985 0 0);
--secondary: oklch(0.94 0.022 270);
--muted: oklch(0.960 0.008 265);
--muted-foreground: oklch(0.52 0.032 265);
--accent: oklch(0.920 0.048 280);
--border: oklch(0.900 0.012 265);
--card: oklch(1 0 0);
--sidebar: oklch(0.148 0.030 265);         /* azul marino oscuro */
--sidebar-foreground: oklch(0.880 0.020 265);
--sidebar-primary: oklch(0.70 0.22 275);   /* violeta brillante en sidebar */
```

### Dark mode
```css
--background: oklch(0.100 0.018 265);
--primary: oklch(0.68 0.22 275);
--card: oklch(0.148 0.022 265);
--muted: oklch(0.238 0.025 265);
--sidebar: oklch(0.095 0.022 265);
```

### Chart colors (orden)
```css
--chart-1: oklch(0.65 0.22 35);   /* naranja */
--chart-2: oklch(0.58 0.18 185);  /* teal */
--chart-3: oklch(0.55 0.24 275);  /* violeta */
--chart-4: oklch(0.65 0.22 330);  /* rosa/fucsia */
--chart-5: oklch(0.72 0.18 130);  /* lima */
```

---

## Tipografía

```html
<!-- En index.html -->
<link href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap" rel="stylesheet" />
```

```css
--font-sans: 'Inter', ui-sans-serif, system-ui, sans-serif;
font-feature-settings: "cv02", "cv03", "cv04", "cv11";
-webkit-font-smoothing: antialiased;
```

---

## Animaciones CSS (SIEMPRE incluir)

```css
/* Orbes flotantes en el fondo del dashboard */
@keyframes float-orb-1 {
  0%, 100% { transform: translate(0px, 0px) scale(1); }
  33%       { transform: translate(24px, -32px) scale(1.06); }
  66%       { transform: translate(-18px, 22px) scale(0.97); }
}
@keyframes float-orb-2 {
  0%, 100% { transform: translate(0px, 0px) scale(1); }
  33%       { transform: translate(-28px, 24px) scale(1.08); }
  66%       { transform: translate(20px, -18px) scale(0.94); }
}
@keyframes float-orb-3 {
  0%, 100% { transform: translate(0px, 0px) scale(1); }
  50%       { transform: translate(16px, -24px) scale(1.04); }
}
@keyframes gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50%       { background-position: 100% 50%; }
}
@keyframes shimmer {
  0%   { background-position: -200% center; }
  100% { background-position: 200% center; }
}
```

---

## Utilidades CSS Especiales

```css
/* Texto con gradiente violeta→azul */
.gradient-text {
  background: linear-gradient(135deg,
    oklch(0.62 0.26 280) 0%,
    oklch(0.55 0.28 300) 40%,
    oklch(0.58 0.22 240) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Borde gradiente en hover */
.gradient-border::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg,
    oklch(0.52 0.26 275 / 40%),
    oklch(0.58 0.22 240 / 20%),
    oklch(0.52 0.26 275 / 40%));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s;
}
.gradient-border:hover::before { opacity: 1; }
```

---

## Layout Base: App con Sidebar

```tsx
// Estructura siempre: sidebar fijo oscuro (250px) + contenido principal

<div className="flex h-screen overflow-hidden bg-background">
  {/* Sidebar — siempre dark, nunca light */}
  <aside className="w-[250px] bg-sidebar text-sidebar-foreground flex flex-col shrink-0">
    {/* Logo con gradiente */}
    <div className="p-4 border-b border-sidebar-border">
      <div className="flex items-center gap-2.5">
        <div style={{
          background: "linear-gradient(135deg, oklch(0.62 0.26 280) 0%, oklch(0.55 0.26 310) 100%)",
          boxShadow: "0 4px 14px oklch(0.52 0.26 275 / 45%)",
        }} className="size-8 rounded-xl flex items-center justify-center">
          <Icon className="size-[17px] text-white" />
        </div>
        <span className="font-bold text-base">AppName</span>
      </div>
    </div>
    {/* Nav links */}
    <nav className="flex-1 p-3 space-y-0.5">
      {/* Cada link: activo=bg-sidebar-accent, hover=bg-sidebar-accent/60 */}
    </nav>
    {/* Footer: dark mode toggle + user avatar */}
  </aside>

  {/* Main */}
  <main className="flex-1 flex flex-col overflow-hidden">
    {/* Sticky header */}
    <header className="h-12 border-b border-border flex items-center justify-between px-6 shrink-0 bg-background/80 backdrop-blur-sm">
      <nav className="text-xs text-muted-foreground">...</nav>
      <div className="flex items-center gap-2">...</div>
    </header>
    {/* Page content */}
    <div className="flex-1 overflow-auto">
      {children}
    </div>
  </main>
</div>
```

---

## Dashboard Hero Section (patrón)

```tsx
{/* Orbes animados en el fondo */}
<div className="absolute inset-0 overflow-hidden pointer-events-none">
  <div style={{
    animation: "float-orb-1 14s ease-in-out infinite",
    background: "radial-gradient(circle, oklch(0.52 0.26 275 / 15%) 0%, transparent 70%)",
  }} className="absolute -top-24 -left-24 size-96 rounded-full" />
  <div style={{
    animation: "float-orb-2 18s ease-in-out infinite",
    background: "radial-gradient(circle, oklch(0.55 0.24 220 / 10%) 0%, transparent 70%)",
  }} className="absolute -top-16 right-0 size-80 rounded-full" />
  <div style={{
    animation: "float-orb-3 22s ease-in-out infinite",
    background: "radial-gradient(circle, oklch(0.65 0.22 330 / 8%) 0%, transparent 70%)",
  }} className="absolute bottom-0 left-1/3 size-72 rounded-full" />
</div>

{/* Badge */}
<div style={{
  background: "linear-gradient(135deg, oklch(0.52 0.26 275 / 10%), oklch(0.58 0.22 240 / 10%))",
  border: "1px solid oklch(0.52 0.26 275 / 25%)",
}} className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold text-primary uppercase tracking-wider mb-4">
  <Sparkles className="size-3" /> Panel de Control
</div>

{/* Heading con gradient text */}
<h1 className="text-4xl font-bold gradient-text mb-2">Bienvenido a AppName</h1>
```

---

## Stat Cards (patrón glassmorphism)

```tsx
// Colores disponibles: "amber" | "violet" | "teal" | "pink"
const GLOW = {
  amber:  "oklch(0.65 0.22 35 / 40%)",
  violet: "oklch(0.55 0.24 275 / 40%)",
  teal:   "oklch(0.58 0.18 185 / 40%)",
  pink:   "oklch(0.65 0.22 330 / 40%)",
}

<div
  className="relative overflow-hidden rounded-xl border border-border bg-card dark:bg-white/[0.04] dark:backdrop-blur-sm p-5 gradient-border group cursor-default transition-all duration-300"
  onMouseEnter={(e) => e.currentTarget.style.boxShadow = `0 8px 30px -4px ${GLOW[color]}`}
  onMouseLeave={(e) => e.currentTarget.style.boxShadow = "none"}
>
  {/* Top gradient bar */}
  <div style={{ background: `linear-gradient(90deg, ${COLOR_MAP[color].from}, transparent)` }}
    className="absolute top-0 left-0 right-0 h-0.5 opacity-25 group-hover:opacity-100 transition-opacity duration-300" />
  {/* Icon */}
  <div style={{ background: COLOR_MAP[color].bg }} className="size-9 rounded-lg flex items-center justify-center mb-4">
    <Icon style={{ color: COLOR_MAP[color].icon }} className="size-4" />
  </div>
  {/* Value + label */}
  <p className="text-muted-foreground text-xs mb-1">{label}</p>
  <p className="text-3xl font-bold tracking-tight">{value}</p>
</div>
```

---

## Tablas (patrón estándar)

```tsx
<Table>
  <TableHeader>
    <TableRow className="bg-muted/40 hover:bg-muted/40">
      <TableHead className="text-xs font-semibold text-muted-foreground/70 uppercase tracking-wide">
        Columna
      </TableHead>
    </TableRow>
  </TableHeader>
  <TableBody>
    {data.map((item) => (
      <TableRow key={item.id} className="hover:bg-muted/30 transition-colors group">
        <TableCell className="font-mono text-xs text-muted-foreground/60">{item.id}</TableCell>
        <TableCell className="font-semibold text-sm">{item.name}</TableCell>
        {/* Status pill */}
        <TableCell>
          {item.active ? (
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-600 dark:text-emerald-400">
              <span className="size-1.5 rounded-full bg-emerald-500 inline-block" />
              Activo
            </span>
          ) : (
            <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium bg-muted text-muted-foreground">
              <span className="size-1.5 rounded-full bg-muted-foreground/40 inline-block" />
              Inactivo
            </span>
          )}
        </TableCell>
        {/* Hover-revealed action buttons */}
        <TableCell>
          <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
            <Button variant="ghost" size="icon" className="size-8 hover:bg-primary/10 hover:text-primary" onClick={() => onEdit(item)}>
              <Pencil className="size-3.5" />
            </Button>
            <Button variant="ghost" size="icon" className="size-8 hover:bg-destructive/10 text-muted-foreground hover:text-destructive" onClick={() => onDelete(item)}>
              <Trash2 className="size-3.5" />
            </Button>
          </div>
        </TableCell>
      </TableRow>
    ))}
  </TableBody>
</Table>
```

---

## Formularios Complejos: Wizard de 3 Pasos

Para registrar ventas, pedidos, o cualquier entidad con múltiples sub-entidades relacionadas (ej: venta con cliente + líneas de artículos), usar un **wizard de 3 pasos** en lugar de un formulario plano:

```
Paso 1 — Entidad principal (ej: Cliente)
  - Input de búsqueda con filtro client-side (useMemo)
  - Lista scrollable de items como <button> con avatar de iniciales
  - Opción inline para crear entidad si no existe
  - Selección con highlight violeta + checkmark

Paso 2 — Sub-entidades (ej: Artículos / Líneas)
  - Chip resumen de lo seleccionado en Paso 1 + enlace "cambiar"
  - Rows de items: búsqueda de libro + precio + stepper (−/qty/+)
  - Botón "Agregar artículo"
  - Total estimado en tiempo real (violet, font-bold)

Paso 3 — Resumen y confirmación
  - Card con avatar + nombre del cliente
  - Tabla de items con BookOpen icon + price×qty + subtotal
  - Total en grande (primary color)
  - CTA "Confirmar" con CheckCircle icon
```

**StepBar component:**
```tsx
function StepBar({ step }: { step: 1 | 2 | 3 }) {
  return (
    <div className="flex items-center gap-0 mb-6">
      {STEPS.map((s, i) => (
        <div key={s.n} className="flex items-center flex-1">
          <div className="flex flex-col items-center gap-1 shrink-0">
            <div className={cn(
              "size-7 rounded-full flex items-center justify-center text-xs font-bold",
              step > s.n  ? "bg-primary text-primary-foreground"
              : step === s.n ? "bg-primary text-primary-foreground ring-4 ring-primary/20"
              : "bg-muted text-muted-foreground",
            )}>
              {step > s.n ? <Check className="size-3.5" /> : s.n}
            </div>
            <span className={cn(
              "text-[10px] font-medium uppercase tracking-wide",
              step >= s.n ? "text-primary" : "text-muted-foreground",
            )}>{s.label}</span>
          </div>
          {i < STEPS.length - 1 && (
            <div className={cn("flex-1 h-px mx-2 mb-4", step > s.n ? "bg-primary" : "bg-border")} />
          )}
        </div>
      ))}
    </div>
  )
}
```

---

## Dark Mode

**Toggle con localStorage:**
```tsx
function useTheme() {
  const [theme, setTheme] = useState<"light" | "dark">(() =>
    (localStorage.getItem("theme") as "light" | "dark") || "light"
  )
  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark")
    localStorage.setItem("theme", theme)
  }, [theme])
  return { theme, toggle: () => setTheme(t => t === "light" ? "dark" : "light") }
}
```

**Botón en sidebar:**
```tsx
<button onClick={toggle} className="flex items-center gap-2 text-xs text-sidebar-foreground/60 hover:text-sidebar-foreground">
  {theme === "dark" ? <Sun className="size-3.5" /> : <Moon className="size-3.5" />}
  {theme === "dark" ? "Modo claro" : "Modo oscuro"}
</button>
```

**CSS en index.css:**
```css
@custom-variant dark (&:is(.dark *));

/* Fix para <select> nativo en dark mode */
select { background-color: var(--background); color: var(--foreground); }
select option { background-color: var(--popover); color: var(--popover-foreground); }
```

---

## Scrollbar personalizado

```css
::-webkit-scrollbar { width: 5px; height: 5px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb {
  background: oklch(0.52 0.26 275 / 30%);
  border-radius: 999px;
}
::-webkit-scrollbar-thumb:hover { background: oklch(0.52 0.26 275 / 60%); }
```

---

## Formato de Fechas — Convención `dd/MM/yyyy`

**SIEMPRE** mostrar fechas como `dd/MM/yyyy`. El backend envía strings ISO `yyyy-MM-dd`.

```tsx
// Fecha corta (para tablas) — ISO date string "yyyy-MM-dd" → "dd/MM/yyyy"
// ⚠️ NO usar new Date() con strings date-only — convierte a UTC y puede desfasar 1 día
function formatDate(ds: string | undefined) {
  if (!ds) return "—"
  const [y, m, d] = ds.split("-")
  return `${d}/${m}/${y}`
}

// Fecha larga con hora (para sheets/detalles)
// ⚠️ NO usar hour12 ni incluir hour/minute en toLocaleDateString — produce "a. m." feo
// Separar fecha y hora: localeDateString + toLocaleTimeString con hour12: false
function formatDateTime(ds: string) {
  if (!ds) return "—"
  try {
    const d = new Date(ds)
    const date = d.toLocaleDateString("es-PE", { year: "numeric", month: "long", day: "numeric" })
    const time = d.toLocaleTimeString("es-PE", { hour: "2-digit", minute: "2-digit", hour12: false })
    return `${date}, ${time}`
  } catch { return ds }
}
```

---

## Detail Sheet — Patrón para Ver Detalles de una Entidad

Para entidades con sub-items (ej: Sale → SaleDetails), usar un **Sheet lateral** que se abre al hacer clic en el ícono `<Eye>`.

**Botón en la tabla** (en la columna de acciones hover-revealed):
```tsx
import { Eye } from "lucide-react"

// Estado:
const [detailItem, setDetailItem] = useState<Sale | null>(null)

// Botón (antes de Edit/Delete):
<Button variant="ghost" size="icon" className="size-8 hover:bg-muted hover:text-foreground"
  onClick={() => setDetailItem(s)}>
  <Eye className="size-3.5" />
</Button>

// Render del sheet:
<DetailSheet item={detailItem} open={!!detailItem} onOpenChange={(o) => { if (!o) setDetailItem(null) }} />
```

**Estructura del Sheet de detalle:**
```tsx
<Sheet open={open} onOpenChange={onOpenChange}>
  <SheetContent className="w-full sm:max-w-lg overflow-y-auto">
    <SheetHeader>
      <SheetTitle>Detalle #{item.id}</SheetTitle>
    </SheetHeader>

    {/* Meta info en 2 columnas */}
    <div className="grid grid-cols-2 gap-3 mb-5">
      <div className="flex items-start gap-2.5 rounded-xl border bg-muted/30 p-3">
        <Icon className="size-4 text-muted-foreground mt-0.5 shrink-0" />
        <div>
          <p className="text-[10px] font-semibold uppercase tracking-wide text-muted-foreground mb-0.5">Label</p>
          <p className="text-sm font-semibold">{value}</p>
        </div>
      </div>
    </div>

    {/* Status */}
    <div className="flex items-center justify-between mb-4">
      <span className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Estado</span>
      {/* status pill (igual que en tablas) */}
    </div>

    <Separator className="mb-4" />

    {/* Sub-items */}
    <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground mb-3">Artículos</p>
    <div className="rounded-xl border overflow-hidden mb-4">
      <Table>
        <TableHeader>
          <TableRow className="bg-muted/40 hover:bg-muted/40">
            <TableHead className="text-[10px] font-semibold uppercase tracking-wide text-muted-foreground/70 py-2">Ítem</TableHead>
            <TableHead className="text-[10px] ... text-right w-24">Subtotal</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {item.details.map((d, i) => (
            <TableRow key={i} className="hover:bg-muted/20">
              <TableCell className="text-sm py-2.5 font-medium">{d.name}</TableCell>
              <TableCell className="text-sm py-2.5 text-right font-mono font-semibold">${d.subtotal.toFixed(2)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>

    {/* Total */}
    <div className="flex items-center justify-between rounded-xl bg-primary/5 border border-primary/10 px-4 py-3">
      <span className="text-sm font-semibold text-muted-foreground">Total</span>
      <span className="text-xl font-bold text-primary font-mono">${item.total.toFixed(2)}</span>
    </div>
  </SheetContent>
</Sheet>
```

---

## Generación desde Backend / Swagger

Cuando el usuario provee un backend Spring Boot, una spec OpenAPI/Swagger, o una lista de endpoints:

1. **Analizar los endpoints** — identificar entidades (ej: `/books`, `/clients`, `/sales`)
2. **Mapear tipos TypeScript** para cada entidad
3. **Crear `src/lib/api.ts`** con `apiFetch`, `apiPost`, `apiPut`, `apiDelete`
4. **Generar una página por entidad** con:
   - `PageHeader` con título + botón de acción
   - `Table` con los campos relevantes (patrón de arriba)
   - `Dialog` con formulario (o wizard si hay sub-entidades)
   - `ConfirmDialog` para eliminación
   - `TablePagination` si hay más de 10 registros
5. **Dashboard** con stat cards (una por entidad principal) + gráficas si hay datos históricos
6. **Sidebar** con navegación a todas las secciones

**Archivo `src/lib/api.ts` base:**
```typescript
const BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api/v1"

async function request<T>(path: string, options?: RequestInit): Promise<T[]> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  })
  if (!res.ok) throw new Error(await res.text())
  const json = await res.json()
  return json.data ?? json
}

export const apiFetch  = <T>(path: string) => request<T>(path)
export const apiPost   = <T>(path: string, body: unknown) => request<T>(path, { method: "POST", body: JSON.stringify(body) })
export const apiPut    = <T>(path: string, body: unknown) => request<T>(path, { method: "PUT", body: JSON.stringify(body) })
export const apiDelete = <T>(path: string) => request<T>(path, { method: "DELETE" })
```

---

## Validación Visual con Puppeteer — Flujo Obligatorio

**Después de cada cambio en frontend, validar con Puppeteer MCP antes de dar por finalizado.**

### Flujo estándar de validación:

```
1. puppeteer_navigate → http://localhost:5173
2. puppeteer_evaluate → navegar a la sección afectada (click via JS)
3. puppeteer_screenshot → vista general (1400×800)
4. puppeteer_evaluate → hacer hover + disparar la interacción (modal/sheet/dialog)
5. puppeteer_screenshot → verificar el resultado
6. puppeteer_screenshot con selector → zoom en el componente específico
```

### Reglas críticas de Puppeteer:

**❌ NUNCA** usar `puppeteer_click` para abrir modales/sheets/dialogs → causa pantalla negra
**✅ SIEMPRE** usar `puppeteer_evaluate` con `.click()` en JS para interacciones que abren overlays:
```js
// ✅ Correcto — abre sheet/modal sin pantalla negra
document.querySelector('tbody tr:first-child td:last-child button:first-child')?.click()

// ❌ Incorrecto — puede dejar pantalla negra
// puppeteer_click('tbody tr:first-child td:last-child button:first-child')
```

**Navegación entre secciones:**
```js
// ✅ Siempre navegar con evaluate, nunca con click directo en SPA
Array.from(document.querySelectorAll('[data-slot="sidebar-menu-button"]'))
  .find(b => b.textContent?.includes('Ventas'))?.click()
```

### Checklist de revisión visual (como experto en diseño):
- [ ] El componente se abre sin errores ni pantalla negra
- [ ] Header con ícono gradient + título + badge de estado
- [ ] Meta-cards con ícono `bg-primary/10`, label uppercase tracking, valor legible
- [ ] Textos largos con `break-words` (no `truncate` salvo en tablas)
- [ ] Tabla con `bg-muted/50` en header, filas con `hover:bg-muted/20`
- [ ] Divisor: usar `<div className="mx-6 h-px bg-border mb-4" />` (no `<Separator>` — bug de orientación en Radix)
- [ ] Total con gradiente `oklch(0.52 0.26 275 / 10%)` y border `oklch(0.52 0.26 275 / 20%)`
- [ ] Sin espacio vacío excesivo — NO usar `mt-auto` en el total si hay pocos items

---

## Checklist antes de entregar

- [ ] Paleta oklch aplicada (violeta primary, sidebar oscuro)
- [ ] Inter font + font-feature-settings
- [ ] Orbes animados en dashboard hero
- [ ] `.gradient-text` en heading principal
- [ ] Stat cards con glassmorphism + glow al hover
- [ ] Tablas con headers uppercase, status pills verde/muted, botones hover-revealed
- [ ] Dark mode toggle funcional con localStorage
- [ ] `<select>` nativo corregido para dark mode
- [ ] Scrollbar violeta personalizado
- [ ] Formularios complejos → wizard 3 pasos
- [ ] Paginación en tablas con >10 items
- [ ] Fechas `dd/MM/yyyy` con split string (no `new Date()` para date-only — desfase UTC)
- [ ] Hora en 24h con `toLocaleTimeString` separado (nunca `hour12` — produce "a. m.")
- [ ] Entidades con sub-items → botón Eye + DetailSheet lateral
- [ ] Validado visualmente con Puppeteer (evaluate + click, no click directo)
