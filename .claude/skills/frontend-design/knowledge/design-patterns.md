# Patrones de Diseño de Producción

Patrones validados a través de refinamiento iterativo de diseño.

## 1. Stat Cards con Contadores Animados
Usar `requestAnimationFrame` con ease-out cúbico para animaciones de números. Agregar mini gráficos sparkline de barras para contexto visual. Incluir líneas de acento con gradiente en hover (cada card un color diferente).

## 2. Paleta de Comandos (Cmd+K)
**OBLIGATORIO para todas las apps tipo dashboard.** Incluir:
- Atajos de navegación (páginas)
- Atajos de acciones (crear entidades)
- Configuración (cambio de tema)
- Indicaciones de teclado en cada item

## 3. Skeleton Loading (Nunca Spinners)
Reemplazar TODOS los spinners con animaciones skeleton shimmer para áreas de contenido. Los skeletons deben coincidir con la forma del contenido real que se está cargando.

## 4. Headers de Página con Breadcrumbs
Cada página necesita: SidebarTrigger + Separator + Breadcrumb (Inicio / Página Actual) + Acciones.

## 5. Sidebar Mejorado
Incluir: Logo, navegación con badges (conteo), indicadores de atajos de teclado, avatar de perfil de usuario, toggle de tema, badge de versión.

## 6. Estados Vacíos
Contenedor de icono centrado + encabezado + descripción + botón CTA. Nunca mostrar una tabla vacía.

## 7. Atajos de Teclado
Números 1-6 para navegación de páginas, Ctrl+K para paleta de comandos, Escape para cerrar overlays.

## 8. Tema Oscuro/Claro
Usar estrategia de clase CSS (`.dark`) con persistencia en localStorage. shadcn lo maneja nativamente.

## 9. Charts y Visualización de Datos (OBLIGATORIO en dashboards)

**Librería**: Recharts via `shadcn chart` component. **NUNCA** usar barras de progreso div-based como sustituto de gráficos reales.

### Cuándo usar cada tipo de gráfico:
| Tipo | Uso | Ejemplo |
|------|-----|---------|
| **Donut/Pie** | Distribución/proporción de un total | Ventas por categoría, participación de mercado |
| **Barra horizontal** | Rankings (top N de algo) | Top clientes, libros más vendidos |
| **Barra vertical** | Comparaciones entre entidades | Ventas por sucursal, ingresos por mes |
| **Área/Línea** | Tendencias temporales | Ventas mensuales, crecimiento |
| **Radar** | Comparación multidimensional | Perfil de habilidades, métricas de rendimiento |

### Agregación de datos client-side:
Cuando el backend no tiene endpoints de agregación, calcular en el frontend con `useMemo`:
- Cross-reference entidades (ej: Sale → SaleDetail → Book → Category)
- Usar `Map<key, aggregate>` para acumular
- Ordenar por valor descendente y tomar `.slice(0, 5)` para top 5
- Asignar colores `var(--chart-1)` a `var(--chart-5)` por índice

### Layout de dashboard analítico:
```
┌──────────────┬──────────────┐
│  Donut Chart │  H-Bar Chart │ ← grid-cols-2
│ (categorías) │ (clientes)   │
├──────────────┼──────────────┤
│  V-Bar Chart │  H-Bar Chart │
│ (sucursales) │ (productos)  │
└──────────────┴──────────────┘
```

### Selección de librería (decidido):
- **Recharts/shadcn** = opción por defecto (MIT, ligero ~40KB, dark mode nativo, CSS variables)
- **NUNCA amCharts** (licencia comercial, API imperativa, 500KB+)
- **NUNCA ApexCharts** (licencia dual desde 2025, 112KB+)
- Si se necesitan treemaps/sankey: agregar `@nivo/treemap` con lazy loading

## 10. Paginación en Tablas de Datos (OBLIGATORIO)

**NUNCA** renderizar todos los registros de una entidad en una tabla sin paginar. Con datos de producción (100+ registros) el DOM se destruye y el UX es inaceptable.

### Componente `TablePagination` reutilizable:
```tsx
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-react"
import { Button } from "@/components/ui/button"

interface TablePaginationProps {
  total: number
  page: number
  pageSize: number
  onPageChange: (page: number) => void
}

export function TablePagination({ total, page, pageSize, onPageChange }: TablePaginationProps) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  const from = total === 0 ? 0 : (page - 1) * pageSize + 1
  const to = Math.min(page * pageSize, total)

  if (total === 0) return null

  return (
    <div className="flex items-center justify-between px-4 py-3 border-t text-sm text-muted-foreground">
      <span>{from}–{to} de {total}</span>
      <div className="flex items-center gap-1">
        <Button variant="ghost" size="icon" className="size-8" disabled={page === 1} onClick={() => onPageChange(1)}>
          <ChevronsLeft className="size-4" />
        </Button>
        <Button variant="ghost" size="icon" className="size-8" disabled={page === 1} onClick={() => onPageChange(page - 1)}>
          <ChevronLeft className="size-4" />
        </Button>
        <span className="px-2 font-medium text-foreground">{page} / {totalPages}</span>
        <Button variant="ghost" size="icon" className="size-8" disabled={page === totalPages} onClick={() => onPageChange(page + 1)}>
          <ChevronRight className="size-4" />
        </Button>
        <Button variant="ghost" size="icon" className="size-8" disabled={page === totalPages} onClick={() => onPageChange(totalPages)}>
          <ChevronsRight className="size-4" />
        </Button>
      </div>
    </div>
  )
}
```

### Patrón de uso en páginas de entidad:
```tsx
const PAGE_SIZE = 10
const [page, setPage] = useState(1)
const paged = data.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

// En el JSX — dentro del CardContent, después de la tabla:
{!loading && data.length > 0 && (
  <TablePagination total={data.length} page={page} pageSize={PAGE_SIZE} onPageChange={setPage} />
)}
```

**Reglas:**
- `PAGE_SIZE = 10` por defecto (ajustable según contexto)
- La barra de paginación se oculta automáticamente si `data.length === 0`
- Resetear `page` a `1` al hacer búsqueda/filtrado: `setPage(1)`
- Colocar `<TablePagination>` **dentro** del `CardContent`, después del `</Table>`, antes del cierre del card

---

## 11. Accesibilidad de Diálogos — DialogDescription Obligatorio

**SIEMPRE** incluir `DialogDescription` dentro de `DialogHeader` en todo componente que use `DialogContent` de Radix UI / shadcn. Sin ella, Radix lanza el warning:
> `Warning: Missing Description or aria-describedby={undefined} for {DialogContent}`

Este warning se dispara **una vez por apertura de diálogo** y puede generar docenas en una sesión.

### Patrón correcto:
```tsx
import {
  Dialog,
  DialogContent,
  DialogDescription,  // ← NUNCA omitir
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"

// En el JSX:
<DialogHeader>
  <DialogTitle>{isEdit ? "Editar" : "Nuevo"} {entityName}</DialogTitle>
  <DialogDescription>
    {isEdit
      ? `Modifica los datos del ${entityName.toLowerCase()}.`
      : `Completa los datos para crear un nuevo ${entityName.toLowerCase()}.`}
  </DialogDescription>
</DialogHeader>
```

**Reglas:**
- `DialogDescription` siempre dentro de `DialogHeader`, inmediatamente después de `DialogTitle`
- Texto contextual: diferente para modo crear vs. editar
- Importar junto con los demás componentes de dialog en la misma línea

---

## 12. Pensamiento de Diseño

Antes de codificar, entender el contexto y comprometerse con una dirección estética AUDAZ:
- **Propósito**: ¿Qué problema resuelve esta interfaz? ¿Quién la usa?
- **Tono**: Elegir un extremo: minimalismo brutal, caos maximalista, retro-futurista, orgánico/natural, lujo/refinado, juguetón, editorial/revista, brutalista/crudo, art deco/geométrico, suave/pastel, industrial/utilitario, etc.
- **Restricciones**: Requisitos técnicos (framework, rendimiento, accesibilidad).
- **Diferenciación**: ¿Qué hace esto INOLVIDABLE?

## 10. Directrices Estéticas del Frontend

- **Tipografía**: Elegir fuentes hermosas, únicas e interesantes. Evitar fuentes genéricas como Arial e Inter. Combinar una fuente display distintiva con una fuente body refinada.
- **Color y Tema**: Comprometerse con una estética cohesiva. Usar variables CSS para consistencia. Colores dominantes con acentos marcados superan a paletas tímidas y distribuidas uniformemente.
- **Movimiento**: Revelaciones escalonadas en carga de página crean más satisfacción que micro-interacciones dispersas. Usar estados hover que sorprendan.
- **Composición Espacial**: Layouts inesperados. Asimetría. Elementos que rompen la cuadrícula. Espacio negativo generoso O densidad controlada.
- **Fondos y Detalles Visuales**: Meshes de gradiente, texturas de ruido, patrones geométricos, transparencias en capas, sombras dramáticas, overlays de grano.

**NUNCA** usar estéticas genéricas de IA como familias de fuentes sobreusadas (Inter, Roboto, Arial, fuentes del sistema), esquemas de color cliché (gradientes púrpura sobre blanco), o diseño tipo molde.
