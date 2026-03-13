# Catálogo de Componentes shadcn

Referencia completa de componentes disponibles en el registro `@shadcn`.

## Primitivas UI (registry:ui)

| Componente | Uso |
|-----------|-----|
| `accordion` | Secciones colapsables |
| `alert` / `alert-dialog` | Notificaciones, confirmaciones |
| `avatar` | Imágenes de perfil de usuario |
| `badge` | Indicadores de estado, etiquetas, contadores |
| `breadcrumb` | Ruta de navegación (SIEMPRE usar en headers de página) |
| `button` / `button-group` | Acciones (variantes: outline, ghost, destructive, link, icon) |
| `calendar` | Selección de fechas |
| `card` | Contenedores (Header, Title, Description, Content, Footer, Action) |
| `carousel` | Presentaciones deslizantes |
| `chart` | Visualización de datos |
| `checkbox` / `switch` / `toggle` | Entradas booleanas |
| `collapsible` | Secciones expandibles |
| `command` | **Paleta de comandos (Cmd+K)** — SIEMPRE incluir en dashboards |
| `combobox` | Búsqueda + selección |
| `context-menu` / `dropdown-menu` | Acciones clic derecho / dropdown |
| `dialog` / `drawer` / `sheet` | Overlays modales (Dialog: centrado, Drawer: inferior, Sheet: lateral) |
| `empty` | Estados vacíos |
| `field` / `form` / `input` / `input-group` / `label` | Controles de formulario |
| `hover-card` / `popover` / `tooltip` | Info contextual en hover/clic |
| `kbd` | Mostrar atajos de teclado |
| `menubar` / `navigation-menu` | Navegación superior |
| `native-select` / `select` | Dropdowns |
| `pagination` | Navegación por páginas |
| `progress` / `spinner` / `skeleton` | Estados de carga (**preferir skeleton sobre spinner**) |
| `radio-group` | Selección única de un grupo |
| `resizable` | Paneles redimensionables |
| `scroll-area` | Scrollbars personalizados |
| `separator` | Divisores visuales |
| `sidebar` | Navegación de app (colapsable, con grupos/secciones/badges) |
| `slider` | Inputs de rango |
| `sonner` | Notificaciones toast (esquina inferior derecha, colores ricos) |
| `table` | Tablas de datos (Header, Body, Row, Head, Cell, Footer) |
| `tabs` | Contenido con pestañas |
| `textarea` | Texto multilínea |
| `toggle-group` | Botones de selección múltiple |

## Bloques de Layout (registry:block)

| Bloque | Descripción |
|--------|------------|
| `dashboard-01` | Dashboard completo: sidebar + charts + tabla + stat cards |
| `sidebar-01` a `sidebar-16` | 16 variaciones de sidebar (simple, colapsable, flotante, iconos, sticky) |
| `login-01` a `login-05` | Variaciones de página de login |
| `signup-01` a `signup-05` | Variaciones de página de registro |
| `chart-*` | 50+ variaciones de gráficos (area, bar, line, pie, radar, radial) |

## Temas (registry:theme)

| Tema | Color Base |
|------|-----------|
| `theme-stone` | Gris cálido |
| `theme-zinc` | Gris frío |
| `theme-neutral` | Gris verdadero |
| `theme-gray` | Gris azulado |
| `theme-slate` | Azul pizarra |
