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
dependen del `docker compose` de arriba. En Windows con Docker Desktop a veces hace falta fijar
`DOCKER_HOST` a la pipe activa (`docker context ls` te dice cuál) para que Testcontainers la
encuentre.

## Arquitectura

- `domain/model`: el negocio puro — `LeaseContract`, `Property`, `Tenant`, `Payment` — sin una sola
  anotación de Spring ni de JPA.
- `domain/model/enums/LeaseStatus`: la máquina de estados. `canTransitionTo` es la única fuente de
  verdad sobre qué cambios de estado son válidos.
- `domain/port`: los repositorios como interfaces — el dominio no sabe que existe Postgres.
- `infrastructure/persistence`: las entidades JPA y los adaptadores que implementan los puertos,
  mapeando entidad ↔ modelo de dominio en los dos sentidos.
