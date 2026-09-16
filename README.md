# GapFinder — Backend

API REST que ayuda a estudiantes de Uniandes a aprovechar los huecos entre
clases: calcula los GAPs libres de cada quien, propone con quién juntarse
(*match*) y qué hacer en ese rato (*Open Table*).

Spring Boot 4.1 · Java 21 · PostgreSQL 16

---

## Cómo correrlo

### Con Docker (recomendado)

No necesitas instalar Postgres ni configurar `JAVA_HOME`:

```bash
docker compose up --build
```

- API: http://localhost:8080
- Base: `localhost:5432` (`gapfinder` / `gapfinder`)
- Salud: http://localhost:8080/actuator/health

Si alguno de esos puertos está ocupado —Docker Desktop suele tomar el 8080—
se mueven sin editar nada:

```bash
APP_PORT=8081 DB_PORT=5433 docker compose up --build
```

Para bajarlo: `docker compose down` conserva los datos, `docker compose down -v`
también borra el volumen y la próxima vez arranca de cero.

### Sin Docker

Necesitas un Postgres corriendo en `localhost:5432` con base, usuario y
contraseña `gapfinder`:

```bash
./mvnw spring-boot:run
```

La conexión se puede apuntar a otro lado sin tocar código:

```bash
DB_URL=jdbc:postgresql://otro-host:5432/gapfinder \
DB_USERNAME=usuario DB_PASSWORD=secreto ./mvnw spring-boot:run
```

### Datos de prueba

`src/main/resources/data.sql` siembra el entorno solo: 30 usuarios con sus
intereses, 60 bloques de clase, 24 actividades, 56 amistades, 6 grupos y 8
Open Tables activas.

El script es **re-ejecutable**: corre en cada arranque y está escrito para no
duplicar ni fallar sobre una base ya poblada. Si agregas seed nuevo,
mantén esa propiedad (`ON CONFLICT` donde haya llave única, `NOT EXISTS`
correlacionado donde no la haya) o romperás el segundo arranque de todo el
equipo.

---

## Cómo está organizado

Monolito por capas, agrupado por funcionalidad:

```
Controller  ──>  Service  ──>  Repository  ──>  PostgreSQL
    │               │
   DTO          Entity + reglas de negocio
```

- **Controller** (`*Controller`) — expone HTTP, mapea entidad↔DTO, no decide nada
- **Service** (`*Service`) — la lógica y las transacciones (`@Transactional`)
- **Repository** (`*Repository`) — acceso a datos vía Spring Data JPA
- **DTO** — `*BasicDTO` (campos planos) y `*CompleteDTO` (agrega relaciones)

Los errores se traducen a HTTP en un solo lugar, `GlobalExceptionHandler`:

| Excepción | HTTP |
|---|---|
| `NotFoundException` | 404 |
| `IllegalArgumentException` | 400 |
| `IllegalStateException` | 409 |

Por eso los services lanzan excepciones de dominio y nunca devuelven
`ResponseEntity`.

### El dominio en una frase

```
ClassBlock  →  Gap        cuándo estás libre
Building    →  Location   dónde estás
Friendship / Group        a quién conoces
Gap + Gap   →  Match      con quién coincides
Match       →  Activity   qué hacer en ese rato
Activity    →  OpenTable  mesa abierta a la que otros se suman
```

`Gap` es el centro: casi todo cuelga de tener un hueco activo ahora mismo.
`GapService.calculateGapsFromSchedule` los deriva de los `ClassBlock`.

### Nivel de esfuerzo

`ActivityEffortEnum` (`QUIET` → `NORMAL` → `ACTIVE`) aparece en dos lados:
la preferencia del usuario (`activityEffortPreference`) y la exigencia de la
actividad (`activityEffortLevel`).

Al sugerir actividades **gana la preferencia más restrictiva** de los
participantes: si a alguien le sirve algo `QUIET`, no se proponen actividades
más exigentes. Las reglas viven en `ActivitySuggestion` —filtros componibles—
y tanto `MatchService` como `OpenTableService` las componen desde ahí. Están
centralizadas a propósito: cuando estaban duplicadas, se agregó el esfuerzo a
una sola y las sugerencias de Open Table quedaron ignorándolo.

---

## Hacia dónde debería ir

Lo de abajo no está hecho. Está ordenado por lo que cuesta arreglarlo si se
deja para después.

### 1. Flyway en lugar de `ddl-auto=update`

Hoy Hibernate ajusta el esquema solo. **Nunca borra ni renombra**: cuando
`mobilityPreference` pasó a `activityEffortPreference`, agregó la columna
nueva y habría dejado la vieja con datos muertos; solo se salvó porque la
base se recreó desde cero. En producción eso no es una opción.

Con Flyway cada cambio es un `V3__rename_mobility.sql` versionado, revisable
y reproducible, y el seed pasa a ser una migración solo de desarrollo.

### 2. Autenticación

No hay ninguna. El `userId` viaja como parámetro, así que cualquier cliente
puede actuar como cualquier usuario:

```
POST /users/5/interests/3    ← nadie verifica que seas el usuario 5
```

Spring Security + JWT, y que el `userId` salga del token y nunca de la URL.

### 3. Tests

Solo existe `contextLoads`, y ni siquiera corre sin una base levantada —por
eso el Dockerfile compila con `-DskipTests`—.

Las reglas de `MatchService`, `GapService` y `ActivitySuggestion` son lógica
pura: se prueban sin base y es donde más barato sale. Para los repositorios,
`@DataJpaTest` con Testcontainers, que además arregla el arranque del build.

### 4. Validación en el borde

`spring-boot-starter-validation` ya es dependencia y no se usa en ningún
lado: toda la validación es artesanal, con `IllegalArgumentException` dentro
de los services. Con `@Valid` + `@NotBlank`/`@Email` en los DTO de entrada se
rechaza antes de tocar la base.

Relacionado: `UserBasicDTO` se usa para entrada **y** salida, así que el
cliente puede mandar el `id` al crear. Conviene separar `CreateUserRequest`
de `UserResponse`.

### 5. Mapeo explícito en vez de ModelMapper

ModelMapper empareja campos por reflexión sobre el nombre. Si una entidad y
su DTO se desincronizan, **no falla: mapea `null` en silencio**. Con MapStruct
o un `from()` escrito a mano, eso es un error de compilación.

### 6. Detalles de JPA

- `@Data` está en las 15 entidades: genera `equals`/`hashCode` sobre todos los
  campos, colecciones `LAZY` incluidas, lo que fuerza cargas y se lleva mal
  con los proxies de Hibernate. Mejor `@Getter`/`@Setter` y `equals` por `id`.
- `open-in-view` está activo (es el default) y por eso mapear colecciones
  perezosas en el controller funciona. Apagarlo es lo correcto, pero hay que
  hacerlo junto con `@EntityGraph`/`JOIN FETCH` explícitos o los endpoints
  `*CompleteDTO` empiezan a tronar.
- Las sugerencias hacen `activityService.getAll()` y filtran en memoria. Con
  24 actividades da lo mismo; es el filtro que debería bajar a la consulta.

### 7. Agrupar por contexto

`entities/` tiene 17 carpetas hermanas y planas: no se ve qué depende de qué.
Una estructura que lo haga evidente:

```
identity/   User, VisibilitySettings, Interest
schedule/   ClassBlock, Gap
campus/     Building, UserLocationLog
social/     Friendship, Group
matching/   Match, Activity
gathering/  OpenTable, Participant, Message, Rating, Notification
shared/     BaseEntity, exceptions, config
```

Con la regla que la hace útil: **`domain/` no importa nada de `api/` ni de
Spring Web**. Eso solo ya impide que la lógica se derrame al controller, que
es lo que empieza a pasar en `UserLocationLogController`.

Es un cambio que toca todos los archivos, así que conviene hacerlo cuando no
haya ramas largas abiertas.

---

## Notas para quien desarrolle

- **`tools/*.ps1`** generaron entidades y repositorios al inicio del proyecto.
  Están fuera de `src/` a propósito: dentro, se empaquetaban en el jar. Ojo
  con `llenar_entities.ps1`, que escribía UTF-8 con BOM y rompía la
  compilación (`illegal character: '﻿'`); ya escribe sin BOM, pero si
  agregas scripts, respétalo.
- **Los `@Scheduled` requieren `@EnableScheduling`**, que está en
  `GapfinderApplication`. Sin esa anotación no corren y no avisan: es lo que
  dejaba las Open Tables vencidas en `ACTIVE` para siempre.
- **Nada de credenciales versionadas.** `application.properties` trae valores
  por defecto para local; todo lo demás entra por entorno (`DB_URL`,
  `DB_USERNAME`, `DB_PASSWORD`).
