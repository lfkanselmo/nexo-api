# nexo-api

Backend para inmobiliarias que administra contratos de arrendamiento, calcula la morosidad y avisa
cuando un pago se atrasa. Arquitectura hexagonal sobre Spring Boot 4.1 y Java 25. El diseño
completo, con el porqué de cada decisión, está en
[`SAD_Nexo_Gestion_Arrendamientos.md`](../SAD_Nexo_Gestion_Arrendamientos.md).

## Desarrollo local

Levantar Postgres y MailHog (para ver los correos de recordatorio sin enviarlos de verdad):

```bash
docker compose -f docker/docker-compose.yml up -d
```

Copiar las variables de entorno y correr la app:

```bash
cp .env.example .env
./gradlew bootRun
```

La API queda en `http://localhost:8080`, con Swagger UI en `/swagger-ui/index.html`.

El compose publica Postgres en el puerto `5435` del host, no el `5432` de siempre, para no chocar
si ya tenés otro Postgres corriendo en la máquina (nativo o de otro proyecto del portafolio). El
`.env` lo lee la app directo, vía `spring.config.import`.

## Tests

```bash
./gradlew test
```

Las pruebas de integración usan Testcontainers — levantan su propio Postgres en Docker, no
dependen del `docker compose` de arriba. En Windows con Docker Desktop a veces no coopera (llegó a
fallar incluso con `DOCKER_HOST` fijado a mano); si pasa, revisar primero que Docker Desktop esté
realmente respondiendo (`docker info`) antes de perder tiempo con la configuración del pipe — en
esta máquina resultó ser que Docker Desktop se había colgado sin más.

## Arquitectura

- `domain/model`: el negocio puro — `LeaseContract`, `Property`, `Tenant`, `Payment` — sin una sola
  anotación de Spring ni de JPA.
- `domain/model/enums/LeaseStatus`: la máquina de estados. `canTransitionTo` es la única fuente de
  verdad sobre qué cambios de estado son válidos.
- `domain/port`: los repositorios como interfaces — el dominio no sabe que existe Postgres.
- `infrastructure/persistence`: las entidades JPA y los adaptadores que implementan los puertos,
  mapeando entidad ↔ modelo de dominio en los dos sentidos.
- `application/service/ContractLifecycleService`: crea contratos (validando que la propiedad y el
  inquilino existan) y orquesta las transiciones de estado, publicando el evento de dominio
  después de guardar.
- `infrastructure/rest`: los controllers y el `GlobalExceptionHandler` que traduce las excepciones
  de dominio a códigos HTTP (404 para "no existe", 409 para una transición inválida, 400 para
  validación de campos).

## Endpoints

| Método | Ruta | Qué hace |
| :--- | :--- | :--- |
| POST | `/properties` | Crea una propiedad |
| POST | `/tenants` | Crea un inquilino |
| POST | `/contracts` | Crea un contrato en `DRAFT` |
| GET | `/contracts/{id}` | Consulta un contrato |
| GET | `/contracts?status=ACTIVE` | Lista contratos por estado |
| POST | `/contracts/{id}/activate` | `DRAFT`/`RENEWAL`/`OVERDUE` → `ACTIVE` |
| POST | `/contracts/{id}/terminate` | `ACTIVE`/`OVERDUE` → `TERMINATED` |
| POST | `/contracts/{id}/renew` | `ACTIVE` → `RENEWAL` |

Probado a mano contra Postgres real (crear → activar → terminar → segundo terminate rechazado con
409 → 404 en un id inexistente → 400 en una validación de campo) mientras Testcontainers no
cooperaba en esta máquina — ver la nota sobre Docker Desktop más abajo.
