# Plan: Add Age Field to Client

**Fecha:** 2026-03-12
**Solicitado por:** Usuario
**Estado:** PENDIENTE APROBACION

---

## Objetivo
Agregar el campo `age` (entero) al modelo `Client` y exponerlo en el DTO, para que los clientes puedan registrar su edad al momento de crear o actualizar su perfil.

## Alcance
- [ ] Agregar campo `age` en `Client.java` (modelo / documento MongoDB)
- [ ] Agregar campo `age` en `ClientDTO.java` con validación `@Min(1) @Max(120) @NotNull`
- [ ] Actualizar `ClientMapper.java` para mapear el nuevo campo (mismo nombre → mapeo automático de MapStruct, pero se documenta explícitamente)

## Arquitectura
**Patrón:** MVC Reactivo (Spring WebFlux + MongoDB)
**Capas afectadas:** model, dto, mapper

> **Nota:** No se toca controller ni service porque el campo nuevo fluye automáticamente a través del mapper y del repositorio genérico. Sale embebe un snapshot de `Client`, por lo que nuevas ventas también incluirán el campo automáticamente.

## Archivos a Crear
_Ninguno nuevo._

## Archivos a Modificar
| # | Ruta | Cambio |
|---|------|--------|
| 1 | `src/main/java/com/mitocode/model/Client.java` | Agregar `private Integer age;` |
| 2 | `src/main/java/com/mitocode/dto/ClientDTO.java` | Agregar `@NotNull @Min(1) @Max(120) private Integer age;` |
| 3 | `src/main/java/com/mitocode/mapper/ClientMapper.java` | El campo `age` tiene el mismo nombre en ambos lados; MapStruct lo mapea automáticamente. Se agrega `@Mapping` explícito solo si se quiere documentar. |

## Dependencias
- [ ] Requiere base de datos (MongoDB/Docker)
- [x] No requiere MCPs externos

## Tareas (ordenadas por dependencia)

### Tarea 1: Modelo — agregar campo `age`
**Archivo:** `src/main/java/com/mitocode/model/Client.java`
**Pasos:**
1. Agregar `private Integer age;` después del campo `birthDate`

### Tarea 2: DTO — agregar campo `age` con validaciones
**Archivo:** `src/main/java/com/mitocode/dto/ClientDTO.java`
**Pasos:**
1. Agregar `@NotNull @Min(1) @Max(120) private Integer age;` con los imports correspondientes

### Tarea 3: Mapper — confirmar mapeo automático
**Archivo:** `src/main/java/com/mitocode/mapper/ClientMapper.java`
**Pasos:**
1. Verificar que `age` (mismo nombre en modelo y DTO) es mapeado automáticamente por MapStruct
2. No se necesita `@Mapping` adicional

## Criterios de Verificacion
| # | Criterio | Cómo verificar | Herramienta |
|---|----------|----------------|-------------|
| 1 | Proyecto compila sin errores | `./mvnw clean compile` | Bash |
| 2 | Tests existentes pasan | `./mvnw test` | Bash |
| 3 | POST /clients acepta `age` y persiste en MongoDB | `curl -X POST` con payload JSON | Bash |
| 4 | GET /clients retorna `age` en la respuesta | `curl -X GET` | Bash |

## Riesgos
- Documentos existentes en MongoDB sin el campo `age` retornarán `null`. Mitigación: el campo es nullable en la entidad (no se usa `@NonNull` en el modelo); en producción se haría una migración, pero en dev se puede limpiar la colección.

---
**APROBACION REQUERIDA: Escribe "aprobado" para iniciar desarrollo.**
