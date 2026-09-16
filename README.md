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
esta máquina resultó ser que Docker Desktop se había colgado sin más, y con eso resuelto la suite
completa corre sin problema.

Ojo si vienen de Spring Boot 3: acá el `ObjectMapper` que Spring autoconfigura es el de
**Jackson 3** (`tools.jackson.databind.ObjectMapper`), no el clásico `com.fasterxml.jackson.databind`
— son tipos distintos, así que inyectar el paquete viejo falla con "no qualifying bean" aunque el
jar de Jackson 2 siga presente transitivamente. `asText()` en `JsonNode` también cambió de nombre,
a `asString()`.

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
  validación de campos, 500 genérico para cualquier otra cosa no prevista, siempre logueada).

## Endpoints

| Método | Ruta | Qué hace |
| :--- | :--- | :--- |
| POST | `/properties` | Crea una propiedad |
| GET | `/properties` | Lista todas las propiedades |
| POST | `/tenants` | Crea un inquilino |
| GET | `/tenants` | Lista todos los inquilinos |
| POST | `/contracts` | Crea un contrato en `DRAFT` |
| GET | `/contracts/{id}` | Consulta un contrato |
| GET | `/contracts` | Lista todos los contratos |
| GET | `/contracts?status=ACTIVE` | Lista contratos por estado |
| POST | `/contracts/{id}/activate` | `DRAFT`/`RENEWAL`/`OVERDUE` → `ACTIVE` |
| POST | `/contracts/{id}/terminate` | `ACTIVE`/`OVERDUE` → `TERMINATED` |
| POST | `/contracts/{id}/renew` | `ACTIVE` → `RENEWAL` |
| POST | `/contracts/{id}/payments` | Agenda un pago pendiente (fecha de vencimiento + monto) |
| GET | `/contracts/{id}/payments` | Lista los pagos de un contrato |
| GET | `/contracts/{id}/penalty?asOf=YYYY-MM-DD` | Calcula mora e interés a la fecha dada (por defecto, hoy) |

Probado a mano contra Postgres real (crear → activar → terminar → segundo terminate rechazado con
409 → 404 en un id inexistente → 400 en una validación de campo; agendar un pago vencido y calcular
su penalidad) mientras Testcontainers no cooperaba en esta máquina — ver la nota sobre Docker
Desktop más abajo.

## Motor de penalización

`PenaltyCalculator` busca los pagos `PENDING` de un contrato cuya fecha de vencimiento ya pasó,
y por cada uno le aplica la estrategia de interés que corresponda según `LeaseContract.interestType`
(`FIXED`: interés simple, `principal * tasa * días`; `VARIABLE`: interés compuesto día a día,
`principal * ((1 + tasa)^días − 1)`). El resultado suma el capital adeudado y el interés de todos
los pagos vencidos — si el inquilino debe dos meses, los dos entran en la cuenta, no solo el más
reciente. `overdueDays` reporta el peor caso (el pago más atrasado), no un promedio.

Las estrategias viven en `application/strategy` como `@Component` de Spring — `PenaltyCalculator`
las recibe todas inyectadas y arma un mapa por `InterestType`, así que agregar un tercer esquema de
interés el día de mañana no toca ni una línea de `PenaltyCalculator`.

## Cron de vencidos y recordatorios

`OverdueDetector` corre todos los días (`nexo.scheduler.overdue-cron`, `0 0 6 * * *` por defecto —
6am) y hace dos cosas: cruza los contratos `ACTIVE` con los pagos `PENDING` vencidos y pasa a
`OVERDUE` los que correspondan, y le pide a `PaymentReminderService` que avise a los inquilinos por
correo (vía MailHog en desarrollo). `PaymentReminderService` solo manda recordatorio si el contrato
está `ACTIVE` u `OVERDUE` — un contrato en `DRAFT` con un pago cargado no debería generar spam de
correos para siempre, y en un momento lo hacía (bug real, encontrado acelerando el cron a mano con
`OVERDUE_CRON="*/15 * * * * *"` y viendo Mail Hog llenarse de correos repetidos a un inquilino de
un contrato que nunca se activó).

Para probar esto sin esperar hasta las 6am:

```bash
OVERDUE_CRON="*/15 * * * * *" ./gradlew bootRun
```

y revisar `http://localhost:8025` (la UI de MailHog) mientras el contrato correspondiente tiene un
pago vencido y está `ACTIVE`.
