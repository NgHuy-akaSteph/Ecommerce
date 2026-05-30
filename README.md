# E-Commerce API (Spring Boot 3 + PostgreSQL)

A robust RESTful API for an e-commerce platform built with Spring Boot 3, providing features like product management, user authentication, shopping carts, and order processing.

## 🚀 Key Features

* **Authentication & Authorization**: JWT-based stateless authentication with Role-Based Access Control (RBAC).
* **Product Management**: Create, read, update, and delete products, categories, and tags.
* **Shopping Cart**: Add products to cart, update quantities, and calculate totals.
* **Order Processing**: Place orders and track order status.
* **File Storage**: Cloudflare R2 integration for fast, S3-compatible asset storage (images, thumbnails).
* **API Documentation**: Interactive Swagger UI via SpringDoc OpenAPI.
* **Database Migrations**: Automated schema and data seeding using Flyway.

## 🛠️ Technology Stack

* **Java 21**
* **Spring Boot 3.3.0**
  * Spring Web
  * Spring Data JPA
  * Spring Security
  * Spring Validation
* **PostgreSQL 16** (Database)
* **Flyway** (Database Migrations)
* **MapStruct & Lombok** (Boilerplate reduction and object mapping)
* **Cloudflare R2** via AWS SDK v2 (S3-compatible Object Storage)
* **SpringDoc OpenAPI** (Swagger UI)
* **Docker & Docker Compose**

## 📦 Running the Application Locally

### Prerequisites

* Java 21+
* Maven 3.9+
* Docker & Docker Compose (optional but recommended for DB)

### 1. Environment Setup

1. Copy the example `.env` file:
   ```bash
   cp .env.example .env
   ```
2. Update the `.env` file with your actual Cloudflare R2 credentials and a strong JWT signer key.

### 2. Using Docker Compose (Easiest Way)

You can spin up both the PostgreSQL database and the Spring Boot API using Docker Compose:

```bash
docker-compose up -d --build
```

The API will be accessible at `http://localhost:8888/api/v1`.

### 3. Manual Build & Run

If you only want to run the database via Docker and run the app manually:

1. Start PostgreSQL:
   ```bash
   docker-compose up -d db
   ```
2. Build and run the app:
   ```bash
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```

## 📖 API Documentation

Once the application is running, you can access the Swagger UI to interactively explore and test the endpoints:

* **Swagger UI**: [http://localhost:8888/api/v1/swagger-ui.html](http://localhost:8888/api/v1/swagger-ui.html)
* **OpenAPI Specs**: [http://localhost:8888/api/v1/api-docs](http://localhost:8888/api/v1/api-docs)

### Authentication in Swagger

1. Use the `/users/login` endpoint to authenticate (Admin credentials seed: `admin` / `123456`).
2. Copy the `access_token` from the response.
3. Click the **"Authorize"** button at the top of the Swagger UI.
4. Paste the token in the Bearer input field and click "Authorize".

## 🗄️ Database Schema & Migrations

The database schema and initial seed data are managed automatically by **Flyway**.
When the application starts, it checks the `src/main/resources/db/migration/` directory and executes any pending SQL scripts.

* `V1__init-schema.sql`: Creates all tables, indexes, and relationships.
* `V2__seed-data.sql`: Seeds default permissions, roles (ADMIN/USER), and the default admin user.

## 📁 File Uploads (Cloudflare R2)

This project uses Cloudflare R2 for storing product images and other assets. It's fully S3-compatible. The configuration disables chunked encoding (`chunkedEncodingEnabled(false)`), which is required by Cloudflare R2.
