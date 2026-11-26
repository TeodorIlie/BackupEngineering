# Project & Task Management API

Backend RESTful sviluppato con **Spring Boot 4** per la gestione di progetti e relativi task, completo di persistenza su **PostgreSQL**, migrazioni versionate con **Flyway**, documentazione **OpenAPI/Swagger** e suite di test automatizzati.

## Stack Tecnologico
- **Java 21** & **Spring Boot 4.1.2**
- **Spring Web MVC** & **Bean Validation**
- **Spring Data JPA / Hibernate**
- **PostgreSQL 16** (produzione/locale) & **H2 Database** (test)
- **Flyway** (migrazioni SQL `V1`, `V2`, `V3`)
- **SpringDoc OpenAPI 3 (Swagger UI)**
- **JUnit 5**, **Mockito** & **MockMvc**
- **Docker** & **Docker Compose**

## Avvio Rapido

### Con Docker Compose (consigliato)
```bash
docker compose up --build
