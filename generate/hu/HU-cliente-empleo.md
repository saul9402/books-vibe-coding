# HU-001: Agregar Campo Empleo Opcional al Cliente

## Historia de Usuario

**Como** administrador del sistema de ventas de libros
**Quiero** poder registrar opcionalmente la información de empleo de los clientes
**Para** tener un perfil más completo que permita estrategias de marketing segmentadas y análisis demográfico de los compradores

---

## Criterios de Aceptación de Negocio

### Escenario 1: Crear cliente con empleo
**Dado** que soy un administrador del sistema
**Cuando** creo un nuevo cliente con datos válidos incluyendo el campo "employment"
**Entonces** el sistema debe guardar el cliente con toda la información incluyendo el empleo
**Y** debe retornar status HTTP 201 Created
**Y** debe incluir la ubicación del recurso creado en el header Location

### Escenario 2: Crear cliente sin empleo (opcional)
**Dado** que soy un administrador del sistema
**Cuando** creo un nuevo cliente con datos válidos pero sin incluir el campo "employment"
**Entonces** el sistema debe guardar el cliente correctamente con employment como null
**Y** debe retornar status HTTP 201 Created
**Y** no debe generar errores de validación

### Escenario 3: Actualizar cliente agregando empleo
**Dado** que existe un cliente sin información de empleo
**Cuando** actualizo el cliente agregando el campo "employment"
**Entonces** el sistema debe actualizar el cliente incluyendo el empleo
**Y** debe retornar status HTTP 200 OK
**Y** debe reflejar el cambio en consultas posteriores

### Escenario 4: Actualizar cliente removiendo empleo
**Dado** que existe un cliente con información de empleo
**Cuando** actualizo el cliente enviando employment como null o vacío
**Entonces** el sistema debe actualizar el cliente removiendo el empleo
**Y** debe retornar status HTTP 200 OK

### Escenario 5: Consultar cliente con empleo
**Dado** que existe un cliente con información de empleo
**Cuando** consulto el cliente por su ID
**Entonces** el sistema debe retornar el cliente con el campo employment poblado
**Y** debe mantener la estructura JSON consistente

### Escenario 6: Consultar cliente sin empleo
**Dado** que existe un cliente sin información de empleo
**Cuando** consulto el cliente por su ID
**Entonces** el sistema debe retornar el cliente con employment como null
**Y** no debe omitir el campo en la respuesta JSON

---

## Criterios de Aceptación Técnicos

### CT-001: Modelo de Datos (MongoDB Document)
- [ ] El campo `employment` debe agregarse a la clase `Client` (package `com.mitocode.model`)
- [ ] El tipo del campo debe ser `String`
- [ ] El campo NO debe tener anotación `@NotNull` (es opcional)
- [ ] El campo debe incluirse en los constructores generados por `@AllArgsConstructor`
- [ ] El campo debe persistirse en MongoDB en la colección "clients"

### CT-002: DTO de Transferencia
- [ ] El campo `employment` debe agregarse a la clase `ClientDTO` (package `com.mitocode.dto`)
- [ ] El tipo del campo debe ser `String`
- [ ] El campo NO debe tener validación `@NotNull`, `@NotEmpty` ni `@NotBlank`
- [ ] El campo puede opcionalmente tener `@Size(max = 100)` para limitar longitud
- [ ] El campo debe incluirse en los constructores generados por Lombok

### CT-003: Mapeo MapStruct
- [ ] El `ClientMapper` debe mapear automáticamente el campo `employment` entre `Client` y `ClientDTO`
- [ ] No se requiere anotación `@Mapping` explícita si los nombres coinciden en ambas clases
- [ ] El mapper debe compilarse sin errores con el procesador de anotaciones
- [ ] Los métodos `toDto()` y `toEntity()` deben incluir el campo employment en la transformación

### CT-004: Persistencia Reactiva
- [ ] El repositorio `IClientRepo` NO requiere cambios (hereda de `ReactiveMongoRepository`)
- [ ] MongoDB debe persistir el campo automáticamente sin configuración adicional
- [ ] Las operaciones `save()` y `findById()` deben ser no bloqueantes (reactive streams)
- [ ] El campo debe indexarse correctamente en MongoDB si se requiere búsqueda por empleo en el futuro

### CT-005: Servicio Reactivo
- [ ] `ClientServiceImpl` NO requiere cambios en la lógica de negocio
- [ ] Los métodos `save()` y `update()` deben manejar el campo employment transparentemente
- [ ] Las operaciones deben retornar `Mono<Client>` para mantener el flujo reactivo
- [ ] El campo null debe manejarse correctamente sin lanzar `NullPointerException`

### CT-006: Controlador REST
- [ ] `ClientController` NO requiere cambios (opera con DTOs mapeados)
- [ ] Los endpoints POST `/clients` y PUT `/clients/{id}` deben aceptar el campo employment
- [ ] El endpoint GET `/clients/{id}` debe retornar el campo en la respuesta
- [ ] El campo debe incluirse en `GenericResponse<ClientDTO>` correctamente

### CT-007: Validación y Manejo de Errores
- [ ] Si el campo excede el límite de `@Size`, debe retornar HTTP 400 con `WebExchangeBindException`
- [ ] El `GlobalErrorHandler` debe mapear errores de validación correctamente
- [ ] No se permite validación de negocio adicional sobre el campo employment
- [ ] El campo null o vacío debe ser válido y no generar excepciones

### CT-008: Compatibilidad con Ventas (Sale)
- [ ] Verificar si `Sale` embebe un snapshot de `Client` (según CLAUDE.md)
- [ ] Si existe embedding, el campo `employment` debe incluirse en el snapshot
- [ ] `SaleMapper` debe actualizarse si mapea el cliente embebido
- [ ] Las ventas existentes deben seguir funcionando sin errores (retrocompatibilidad)

---

## Criterios de Aceptación de Testing

### CT-T001: Tests Unitarios del Servicio
- [ ] Actualizar `ClientServiceImplTest` para incluir employment en los datos de prueba
- [ ] El método `sampleClient()` debe retornar un cliente con employment poblado
- [ ] Agregar test: `save_withEmployment_returnsSavedEntity()`
- [ ] Agregar test: `save_withoutEmployment_returnsSavedEntityWithNullEmployment()`
- [ ] Todos los tests existentes deben pasar sin modificaciones mayores

### CT-T002: Tests de Integración del Controlador
- [ ] Actualizar `ClientControllerTest` para incluir employment en los DTOs de prueba
- [ ] El método `sampleDto()` debe retornar un DTO con employment
- [ ] Agregar test: `saveClient_withEmployment_returnsCreated()`
- [ ] Agregar test: `saveClient_withoutEmployment_returnsCreated()`
- [ ] Agregar test: `saveClient_employmentTooLong_returns400()` (si se agrega validación @Size)
- [ ] Todos los tests existentes deben ejecutarse exitosamente

### CT-T003: Tests de Mapeo
- [ ] Crear test unitario para `ClientMapper` si no existe
- [ ] Verificar que `toDto()` mapea employment correctamente
- [ ] Verificar que `toEntity()` mapea employment correctamente
- [ ] Verificar que valores null se manejan sin errores

---

## Reglas de Negocio

### RN-001: Opcionalidad del Campo
El campo `employment` es completamente opcional. Los clientes pueden registrarse sin especificar su empleo, y esto no debe impedir ninguna operación del sistema.

### RN-002: Longitud Máxima
Si se implementa validación `@Size`, el empleo no debe exceder 100 caracteres para mantener consistencia en la UI y base de datos.

### RN-003: Formato Libre
El campo acepta texto libre sin validaciones de formato específico (ej: no se valida si es una empresa real, profesión estándar, etc.).

### RN-004: Inmutabilidad Histórica
Las ventas ya registradas con snapshot del cliente deben mantener el employment tal como estaba al momento de la venta, aunque el cliente actualice su empleo posteriormente.

### RN-005: Retrocompatibilidad
Clientes existentes en la base de datos sin el campo `employment` deben seguir funcionando correctamente. MongoDB agregará el campo como null automáticamente en consultas.

---

## Contrato de API

### POST /clients (Crear Cliente con Empleo)

**Request Body:**
```json
{
  "firstName": "John",
  "surname": "Doe",
  "birthDateClient": "1990-01-15",
  "age": 34,
  "employment": "Software Engineer at TechCorp"
}
```

**Response:** HTTP 201 Created
```json
{
  "status": 201,
  "message": "Client created successfully",
  "data": [
    {
      "idClient": "cli-1",
      "firstName": "John",
      "surname": "Doe",
      "birthDateClient": "1990-01-15",
      "age": 34,
      "employment": "Software Engineer at TechCorp"
    }
  ]
}
```

**Headers:**
```
Location: /clients/cli-1
Content-Type: application/json
```

---

### POST /clients (Crear Cliente sin Empleo)

**Request Body:**
```json
{
  "firstName": "Jane",
  "surname": "Smith",
  "birthDateClient": "1992-05-20",
  "age": 32
}
```

**Response:** HTTP 201 Created
```json
{
  "status": 201,
  "message": "Client created successfully",
  "data": [
    {
      "idClient": "cli-2",
      "firstName": "Jane",
      "surname": "Smith",
      "birthDateClient": "1992-05-20",
      "age": 32,
      "employment": null
    }
  ]
}
```

---

### GET /clients/{id} (Consultar Cliente con Empleo)

**Response:** HTTP 200 OK
```json
{
  "status": 200,
  "message": "Client found",
  "data": [
    {
      "idClient": "cli-1",
      "firstName": "John",
      "surname": "Doe",
      "birthDateClient": "1990-01-15",
      "age": 34,
      "employment": "Software Engineer at TechCorp"
    }
  ]
}
```

---

### PUT /clients/{id} (Actualizar Empleo)

**Request Body:**
```json
{
  "idClient": "cli-1",
  "firstName": "John",
  "surname": "Doe",
  "birthDateClient": "1990-01-15",
  "age": 34,
  "employment": "Senior Software Engineer at NewCorp"
}
```

**Response:** HTTP 200 OK
```json
{
  "status": 200,
  "message": "Client updated successfully",
  "data": [
    {
      "idClient": "cli-1",
      "firstName": "John",
      "surname": "Doe",
      "birthDateClient": "1990-01-15",
      "age": 34,
      "employment": "Senior Software Engineer at NewCorp"
    }
  ]
}
```

---

### Validación de Errores

**Request con empleo demasiado largo (si se implementa @Size):**
```json
{
  "firstName": "John",
  "surname": "Doe",
  "birthDateClient": "1990-01-15",
  "age": 34,
  "employment": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
}
```

**Response:** HTTP 400 Bad Request
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "employment",
      "message": "size must be between 0 and 100"
    }
  ]
}
```

---

## Definición de Hecho (Definition of Done)

- [ ] **Modelo actualizado:** Campo `employment` agregado a `Client.java` con tipo `String`
- [ ] **DTO actualizado:** Campo `employment` agregado a `ClientDTO.java` sin validación `@NotNull`
- [ ] **Mapper funcional:** `ClientMapper` compila y mapea el campo correctamente
- [ ] **Base de datos funcional:** MongoDB persiste y recupera el campo sin errores
- [ ] **Tests unitarios actualizados:** `ClientServiceImplTest` incluye casos con y sin employment
- [ ] **Tests de integración actualizados:** `ClientControllerTest` valida endpoints con employment
- [ ] **Todos los tests pasan:** `./mvnw test` ejecuta sin fallos
- [ ] **Build exitoso:** `./mvnw clean install` completa sin errores
- [ ] **Aplicación arranca:** `./mvnw spring-boot:run` inicia correctamente
- [ ] **API funcional:** Endpoints POST, GET, PUT manejan employment correctamente
- [ ] **Retrocompatibilidad verificada:** Clientes existentes sin employment funcionan
- [ ] **Documentación actualizada:** Contrato API documentado en este archivo
- [ ] **Code review aprobado:** Cambios revisados por al menos un desarrollador senior
- [ ] **No regresiones:** Funcionalidad existente (Book, Sale, Category, Author) no afectada

---

## Notas Técnicas

### Stack Tecnológico Aplicado
- **Java 21** con Records y pattern matching para validaciones futuras
- **Spring Boot 3.5.6** con Spring WebFlux (reactive programming)
- **Spring Data MongoDB Reactive** - `ReactiveMongoRepository` para persistencia no bloqueante
- **MapStruct 1.5+** - Mapping compile-time entre `Client` ↔ `ClientDTO`
- **Lombok** - `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` para reducir boilerplate
- **Jakarta Validation** - Bean Validation 3.0 para validaciones declarativas

### Consideraciones de Arquitectura Reactiva
1. **No Blocking I/O:** Todos los métodos deben retornar `Mono<T>` o `Flux<T>`
2. **Error Handling:** El `GlobalErrorHandler` captura excepciones y retorna respuestas HTTP apropiadas
3. **Validación:** Las validaciones de Bean Validation se ejecutan automáticamente en `@Valid` del controlador
4. **MapStruct + Lombok:** El orden de procesadores de anotaciones en `pom.xml` debe ser: Lombok → MapStruct

### Estrategia de Testing
- **Unitarios:** Mockito para `IClientRepo`, StepVerifier para validar flujos reactivos
- **Integración:** `@WebFluxTest` con `WebTestClient` para endpoints REST
- **Cobertura:** Mínimo 80% de cobertura en servicios y controladores

### Migración de Datos
Si existen clientes en producción, MongoDB agregará automáticamente `employment: null` en documentos existentes al consultarlos. No se requiere script de migración explícito.

### Extensiones Futuras (Fuera del Scope)
- Búsqueda de clientes por empleo (requiere índice en MongoDB)
- Validación de empleo contra una lista de empresas registradas
- Campo compuesto `Employment` con nombre de empresa, cargo, fecha de inicio
- Integración con APIs de verificación de empleo (LinkedIn, etc.)

---

## Dependencias y Bloqueos

**Depende de:**
- Ninguna otra HU (feature independiente)

**Bloquea a:**
- HU de reportes avanzados de clientes por segmento laboral
- HU de análisis demográfico de compradores

**Riesgos:**
- Baja complejidad, riesgo mínimo de bloqueo en desarrollo
- Posible impacto en `SaleMapper` si se requiere actualizar snapshots de clientes

---

## Estimación

**Story Points:** 3
**Tiempo Estimado:** 4-6 horas

**Desglose:**
- Actualización de modelo y DTO: 1 hora
- Actualización de tests: 2 horas
- Validación manual y documentación: 1 hora
- Code review y ajustes: 1-2 horas

**Desarrollador Asignado:** [Por definir]
**Revisor:** [Por definir]

---

## Referencias

- [Spring Data MongoDB - Reactive Repositories](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive.repositories)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [Bean Validation Specification](https://beanvalidation.org/3.0/)
- [Project Reactor - Mono/Flux](https://projectreactor.io/docs/core/release/reference/)