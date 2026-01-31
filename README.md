# FinTrack – Proyecto Integrador Backend

API REST desarrollada en **Kotlin + Spring Boot** para el control de gastos e ingresos personales.  
Permite gestionar usuarios, categorías financieras y transacciones, además de generar reportes de balance.

---

## 🚀 Tecnologías utilizadas

- Kotlin
- Spring Boot
- Spring Data JPA
- PostgreSQL (Docker Compose)
- Gradle
- JUnit 5 + Mockito
- JaCoCo (Coverage)

---

## 🧱 Arquitectura

El proyecto sigue estrictamente la arquitectura:

Controller → Service → Repository

Además, se utilizan:
- DTOs de **Request**
- DTOs de **Response**
- **Mappers** para conversión Entity ↔ DTO
- **GlobalExceptionHandler** para manejo centralizado de errores

---

## 🗄️ Modelo de datos

Entidades relacionadas:

- **User**
    - 1..N → Categories
    - 1..N → Transactions

- **Category**
    - N..1 → User
    - 1..N → Transactions

- **Transaction**
    - N..1 → User
    - N..1 → Category

---

## 🐘 Base de datos (PostgreSQL con Docker)

El proyecto usa una base de datos externa levantada con Docker Compose.

### 📄 docker-compose.yml

```yaml
services:
  fintrack-db:
    image: postgres:17
    container_name: fintrack-db
    environment:
      POSTGRES_USER: fintrack
      POSTGRES_PASSWORD: fintrack
      POSTGRES_DB: fintrack_db
    ports:
      - "5433:5432"
    volumes:
      - fintrack_pgdata:/var/lib/postgresql/data
                
volumes:
  fintrack_pgdata:
```
## ▶️ Levantar la base de datos

    docker compose up -d

## ⚙️ Configuración (application.yml)

    spring:
        datasource:
            url: jdbc:postgresql://localhost:5433/fintrack_db
            username: fintrack
            password: fintrack
        jpa:
            hibernate:
            ddl-auto: update
            show-sql: true

## ▶️ Ejecutar la aplicación

Desde la raíz del proyecto:

    ./gradlew bootRun

La API se levanta en:

    http://localhost:8080

## 🧪 Ejecutar tests

    ./gradlew clean test

## 📊 Coverage (JaCoCo)

Para generar el reporte de coverage:

    ./gradlew clean test jacocoTestReport

Abrir el reporte en:

    build/reports/jacoco/test/html/index.html

✔️ La capa Service tiene 100% de coverage, requisito obligatorio del proyecto.

## 📬 Colección de endpoints (Postman)

El proyecto incluye una colección de Postman con todos los endpoints disponibles.

    FinTrack API.postman_collection.json

📁 Ruta: fintrack/postman/FinTrack API.postman_collection.json

### Cómo usarla
1. Abrir Postman
2. Import → File
3. Seleccionar `fintrack_postman_collection.json`
4. Configurar la variable `baseUrl` (ej: http://localhost:8080)

## 📌 Endpoints principales

## Users

* POST /users

* GET /users/{id}

* *GET /users

## Categories

* POST /categories
* GET /categories/{id}
* GET /categories?userId={userId}

## Transactions

* POST /transactions
* GET /transactions/{id}
* GET /transactions?userId={userId}
* GET /transactions?categoryId={categoryId}
* PUT /transactions/{id}
* DELETE /transactions/{id}
* GET /transactions/report?userId={userId}&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
