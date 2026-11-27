cat <<'EOF' > ~/Downloads/prova/README.md
# Project & Task Management API

Backend RESTful sviluppato con **Spring Boot 4** per la gestione di progetti e relativi task, completo di autenticazione tramite **JWT**, persistenza su **PostgreSQL**, migrazioni versionate con **Flyway**, documentazione **OpenAPI/Swagger** e suite di test automatizzati.

## Stack tecnologico

- **Java 21** & **Spring Boot 4.1.2**
- **Spring Web MVC** & **Bean Validation**
- **Spring Security** con filtro **JWT** (`JwtAuthenticationFilter`)
- **Spring Data JPA / Hibernate**
- **PostgreSQL 16** (produzione/locale) & **H2 Database** (test)
- **Flyway** (migrazioni SQL `V1`–`V4` in `src/main/resources/db/migration`)
- **SpringDoc OpenAPI 3 (Swagger UI)**
- **JUnit 5**, **Mockito** & **MockMvc**
- **Docker** & **Docker Compose**

## Sicurezza e autenticazione (Spring Security + JWT)

L'API utilizza **Spring Security** in modalita stateless con autenticazione basata su **JWT** (`JwtAuthenticationFilter` e `AuthController`):

- **Endpoint pubblici (`permitAll()`):** `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**` e `/error` (cosi gli errori applicativi o del database restituiscono il dettaglio JSON anziche un `403` sul forward interno).
- **Endpoint protetti:** tutte le altre rotte (es. `/api/projects/**`) richiedono l'header `Authorization: Bearer <token>`.

### Comandi di accesso, autenticazione e rotte protette

```bash
# 1. Registrazione utente
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "mario", "password": "password123"}'

# 2. Login e salvataggio del token JWT
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "mario", "password": "password123"}')
echo "TOKEN: $TOKEN"

# 3. Accesso a endpoint protetto con Bearer token
curl -i http://localhost:8080/api/projects \
  -H "Authorization: Bearer $TOKEN"

# 4. Verifica accesso negato (401/403) senza token
curl -i http://localhost:8080/api/projects
# Su PostgreSQL locale (localhost:5432)
psql -h localhost -p 5432 -U postgres -d taskdb <<'EOF'
DO $$ BEGIN IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'taskuser') THEN CREATE ROLE taskuser WITH LOGIN PASSWORD 'taskpassword'; END IF; END $$;

GRANT USAGE, CREATE ON SCHEMA public TO taskuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO taskuser;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO taskuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO taskuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO taskuser;
EOF

# In alternativa, sul container Docker task-manager-db
docker exec -i task-manager-db psql -U postgres -d taskdb <<'EOF'
GRANT USAGE, CREATE ON SCHEMA public TO taskuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO taskuser;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO taskuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO taskuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO taskuser;
EOF 
# Compilazione e packaging
./mvnw clean package

# Esecuzione suite di test (JUnit 5 / Mockito / MockMvc)
./mvnw test

# Avvio applicazione in locale
./mvnw spring-boot:run

# Avvio escludendo il supporto Docker Compose integrato
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.docker.compose.enabled=false"

# Liberare la porta 8080 se gia occupata
lsof -ti:8080 | xargs kill -9 2>/dev/null

# Avvio completo con Docker Compose
docker compose up --build

