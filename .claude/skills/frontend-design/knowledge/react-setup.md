# Setup React + shadcn + Tailwind

Comandos y patrones para inicializar un proyecto React con shadcn.

## Comandos de Instalación

```bash
# 1. Crear proyecto
npm create vite@latest frontend -- --template react-ts

# 2. Instalar Tailwind CSS v4
npm install -D tailwindcss @tailwindcss/vite

# 3. Agregar @import "tailwindcss" a src/index.css

# 4. Configurar aliases en tsconfig.json y vite.config.ts

# 5. Inicializar shadcn
npx shadcn@latest init

# 6. Agregar componentes base
npx shadcn@latest add button card badge table dialog input label select command sidebar separator breadcrumb skeleton sonner dropdown-menu tooltip avatar tabs checkbox

# 7. Agregar componente chart (instala recharts automáticamente)
npx shadcn@latest add chart
```

> **Nota**: `npx shadcn@latest add chart` instala `recharts` como dependencia y crea `src/components/ui/chart.tsx` con `ChartContainer`, `ChartTooltip`, `ChartTooltipContent`, `ChartLegend`, `ChartLegendContent`.

## Patrones Clave de React

### SidebarProvider + SidebarInset — Layout estándar de dashboard:
```tsx
<SidebarProvider>
  <AppSidebar />
  <SidebarInset>
    <PageHeader />
    <PageContent />
  </SidebarInset>
</SidebarProvider>
```

### CommandDialog — Paleta Cmd+K (obligatoria en dashboards):
```tsx
<CommandDialog open={open} onOpenChange={setOpen}>
  <CommandInput placeholder="Buscar..." />
  <CommandList>
    <CommandGroup heading="Navegación">
      <CommandItem>...</CommandItem>
    </CommandGroup>
  </CommandList>
</CommandDialog>
```

### Sonner toasts — Siempre posición inferior-derecha:
```tsx
import { Toaster, toast } from "sonner"
<Toaster position="bottom-right" richColors />
```

### Skeleton loading — Siempre preferir sobre spinners:
```tsx
{loading ? (
  <div className="space-y-2">
    <Skeleton className="h-4 w-full" />
    <Skeleton className="h-4 w-3/4" />
  </div>
) : <ContenidoReal />}
```

### Charts con Recharts + shadcn — Visualización de datos en dashboards:

**ChartConfig** mapea claves de datos a labels y colores (usa CSS variables del tema):
```tsx
import { type ChartConfig } from "@/components/ui/chart"

const chartConfig = {
  ventas: { label: "Ventas", color: "var(--chart-1)" },
  gastos: { label: "Gastos", color: "var(--chart-2)" },
} satisfies ChartConfig
```

**Donut / Pie Chart**:
```tsx
import { Pie, PieChart, Cell, Label } from "recharts"
import { ChartContainer, ChartTooltip, ChartTooltipContent, ChartLegend, ChartLegendContent } from "@/components/ui/chart"

<ChartContainer config={config} className="mx-auto aspect-square max-h-[250px]">
  <PieChart>
    <ChartTooltip content={<ChartTooltipContent nameKey="name" />} />
    <Pie data={data} dataKey="value" nameKey="name" innerRadius={60} outerRadius={95}
         strokeWidth={2} stroke="hsl(var(--background))" paddingAngle={2}>
      {data.map((e) => <Cell key={e.name} fill={e.fill} />)}
      <Label content={({ viewBox }) => /* center text */} />
    </Pie>
    <ChartLegend content={<ChartLegendContent nameKey="name" />} />
  </PieChart>
</ChartContainer>
```

**Horizontal Bar Chart**:
```tsx
import { Bar, BarChart, CartesianGrid, XAxis, YAxis, Cell } from "recharts"

<ChartContainer config={config} className="min-h-[250px] w-full">
  <BarChart data={data} layout="vertical" margin={{ left: 10, right: 12 }} accessibilityLayer>
    <CartesianGrid horizontal={false} strokeDasharray="3 3" />
    <YAxis dataKey="name" type="category" tickLine={false} axisLine={false} width={100} />
    <XAxis type="number" tickLine={false} axisLine={false} />
    <ChartTooltip content={<ChartTooltipContent />} />
    <Bar dataKey="value" radius={[0, 4, 4, 0]}>
      {data.map((e) => <Cell key={e.name} fill={e.fill} />)}
    </Bar>
  </BarChart>
</ChartContainer>
```

**Vertical Bar Chart**:
```tsx
<ChartContainer config={config} className="min-h-[250px] w-full">
  <BarChart data={data} margin={{ top: 16 }} accessibilityLayer>
    <CartesianGrid vertical={false} strokeDasharray="3 3" />
    <XAxis dataKey="name" tickLine={false} axisLine={false} />
    <YAxis tickLine={false} axisLine={false} />
    <ChartTooltip content={<ChartTooltipContent />} />
    <Bar dataKey="value" radius={[4, 4, 0, 0]}>
      {data.map((e) => <Cell key={e.name} fill={e.fill} />)}
    </Bar>
  </BarChart>
</ChartContainer>
```

**Colores**: Usar `var(--chart-1)` a `var(--chart-5)` definidos en `index.css`. Adaptan automáticamente a dark/light mode.

**Reglas para charts en dashboards:**
- Usar `ChartContainer` siempre (provee ResponsiveContainer + estilos)
- Grid de 2 columnas (`lg:grid-cols-2`) para paneles de gráficos
- Donut para proporciones/distribuciones (categorías, participación)
- Barras horizontales para rankings (top clientes, top libros)
- Barras verticales para comparaciones (sucursales, meses)
- Skeleton de chart en loading: `<Skeleton className="h-[220px] w-full rounded-lg" />`
- Tooltips siempre con `<ChartTooltipContent />` de shadcn (no custom)
- Formatear moneda con `$${value.toLocaleString("es-ES", { minimumFractionDigits: 2 })}`

---

## Traducción shadcn React → HTML/CSS Vanilla

Cuando se construyen páginas vanilla HTML/CSS (no React), traducir los patrones shadcn:

### Sistema de Variables CSS de shadcn:
```css
:root {
  --background: 0 0% 100%;
  --foreground: 0 0% 3.9%;
  --card: 0 0% 100%;
  --primary: 0 0% 9%;
  --primary-foreground: 0 0% 98%;
  --muted: 0 0% 96.1%;
  --muted-foreground: 0 0% 45.1%;
  --destructive: 0 84.2% 60.2%;
  --border: 0 0% 89.8%;
  --ring: 0 0% 3.9%;
  --radius: 0.5rem;
}
```

### Patrones Componente → HTML:
- `<Card>` → `<div class="card">` con border, radius, shadow
- `<Badge variant="secondary">` → `<span class="badge badge-secondary">`
- `<Button variant="outline">` → `<button class="btn btn-outline">`
- `<Table>` → `<table class="data-table">` con bordes limpios
- `<Dialog>` → `<div class="modal-overlay"><div class="modal">` con backdrop-filter
- `<Sidebar>` → `<aside class="sidebar">` con posición fija, colapsable
- `<Skeleton>` → `<div class="skeleton">` con animación shimmer
