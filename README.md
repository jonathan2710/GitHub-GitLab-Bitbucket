**Project Summary**
- **Name**: Vbote (demo)
- **Purpose**: REST API para gestión de usuarios y sesiones (login, sesiones activas, bloqueo, etc.).

**Tecnologías**:
- **Java 17**, **Spring Boot 3.5.8** (Web, Data JPA)
- **Hibernate ORM 6.x**
- **H2** (desarrollo, file-based persistente por defecto en este repo)
- **PostgreSQL** (opción para producción; integración disponible en `docker-compose.yml`)
- **Maven** (build)
- **JUnit 5 + Mockito + MockMvc** (tests unitarios y controller tests)

**Decisiones de diseño**:
- Entidades JPA: `User` y `Session` (relación 1:N User -> Session). Campos audit tramite `@PrePersist/@PreUpdate`.
- Serialización JSON: `password` marcado como `WRITE_ONLY` y se ignoran propiedades de Hibernate (`hibernateLazyInitializer`, `handler`) para evitar errores de serialización.
- Seeding: `DataInitializer` (implementa `CommandLineRunner`) inserta usuarios iniciales si el repositorio está vacío. `data.sql` existe como referencia pero la ejecución de scripts SQL está por defecto deshabilitada (`spring.sql.init.mode=never`) para evitar duplicados en una BD persistente.
- Persistencia dev: H2 file-based (`jdbc:h2:file:~/vbote_data/testdb;AUTO_SERVER=TRUE...`) para que los datos persistan entre reinicios en entorno local sin necesidad de instalar Postgres. Para producción, se recomienda usar PostgreSQL.
- Tests: mezcla de tests unitarios (Mockito) y pruebas de integración ligera con MockMvc. Agregados tests para `UserService`, `SessionService` y controladores.

**Archivos relevantes**:
- `src/main/java/com/example/vbote` — código fuente (controller, service, entity, repository, config)
- `src/main/resources/application.properties` — configuración por defecto (H2 file-based)
- `src/main/resources/data.sql` — script de seed (no ejecutado por defecto)
- `src/main/java/com/example/vbote/config/DataInitializer.java` — inserta datos iniciales si la tabla users está vacía
- `Dockerfile` + `docker-compose.yml` — despliegue con PostgreSQL (opcional)

**Comandos útiles**

- Ejecutar en modo desarrollo (mvn):
```bash
./mvnw spring-boot:run
```

- Empaquetar JAR:
```bash
./mvnw -DskipTests package
# Ejecutar JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

- Ejecutar tests:
```bash
./mvnw test
```

- Levantar con Docker Compose (PostgreSQL + API):
```bash
docker compose up --build
```
API disponible en `http://localhost:8080`.

**Variables de entorno para Docker**
- Configuradas en `docker-compose.yml` para el servicio `api`:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/vbote`
  - `SPRING_DATASOURCE_USERNAME=vbote`
  - `SPRING_DATASOURCE_PASSWORD=vbote`
  - `SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver`
  - `SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect`
  - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`


---
Fecha: 3 de diciembre de 2025
