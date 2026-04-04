# Cloud File Storage

A cloud file storage application that allows users to upload, download, manage, and search files through a web interface. Built with Spring Boot and React.

## Tech Stack

### Backend
- **Java 25**, **Spring Boot 4.0.3**
- **Spring Security** — session-based authentication (sessions stored in Redis)
- **Spring Data JPA** + **Hibernate** — data access
- **PostgreSQL 18** — relational database
- **Flyway** — database migrations
- **MinIO** — S3-compatible object storage for files
- **Redis** — distributed HTTP session store
- **MapStruct** — DTO mapping
- **SpringDoc OpenAPI 3.0** — API documentation (Swagger UI)
- **Testcontainers** — integration testing

### Frontend
- **React 19** + **Vite**
- **Material UI (MUI)**
- **React Router**
- **Axios**

## Architecture

```
┌────────────┐      ┌──────────────┐      ┌────────────┐
│  Frontend   │─────►│   Backend    │─────►│ PostgreSQL │
│  (React)    │ API  │ (Spring Boot)│      └────────────┘
│  nginx:80   │      │    :8080     │─────►┌────────────┐
└────────────┘      │              │      │   MinIO     │
                     │              │      │ (S3 store)  │
                     │              │─────►└────────────┘
                     └──────────────┘      ┌────────────┐
                            │──────────────►│   Redis    │
                                           │ (sessions) │
                                           └────────────┘
```

Each user's files are isolated under a `user-{userId}-files/` prefix in MinIO.

## Features

- User registration and session-based authentication
- File upload (up to 50 MB per file, multi-file support)
- File and folder download (folders downloaded as ZIP)
- Directory creation and browsing
- Move / rename files and folders
- Search files by name
- Per-user file isolation
- API documentation via Swagger UI

## API Endpoints

| Method | Endpoint              | Description                  | Auth     |
|--------|-----------------------|------------------------------|----------|
| POST   | `/api/auth/sign-up`   | Register a new user          | No       |
| POST   | `/api/auth/sign-in`   | Sign in                      | No       |
| POST   | `/api/auth/sign-out`  | Sign out                     | Yes      |
| GET    | `/api/user/me`        | Get current user profile     | Yes      |
| GET    | `/api/directory`      | List directory contents      | Yes      |
| POST   | `/api/directory`      | Create a directory           | Yes      |
| POST   | `/api/resource`       | Upload files (multipart)     | Yes      |
| GET    | `/api/resource`       | Get object metadata          | Yes      |
| DELETE | `/api/resource`       | Delete an object             | Yes      |
| GET    | `/api/resource/download` | Download file / folder as ZIP | Yes  |
| PUT    | `/api/resource/move`  | Move / rename an object      | Yes      |
| GET    | `/api/resource/search`| Search objects by name       | Yes      |

Full interactive documentation is available at `/swagger-ui/index.html` when the application is running.

## Project Structure (Backend)

```
backend/src/main/java/metty1337/cloudfilestorage/
├── config/             # Security, OpenAPI, MinIO configuration
├── controller/         # REST controllers (interface + impl)
├── dto/                # Request/response DTOs
├── entity/             # JPA entities
├── exception/          # Custom exception classes
├── mapper/             # MapStruct mappers
├── repository/         # Spring Data JPA repositories
├── security/           # UserDetailsService, UserPrincipal
├── service/            # Business logic (interface + impl)
├── storage/            # MinIO storage client abstraction
└── constants/          # Enums (ObjectType)
```

## Building and Running

### Prerequisites

- **Docker** and **Docker Compose**
- **Java 25** (only if building outside Docker)
- **Gradle** (wrapper included, no separate install needed)

### Secrets Setup

Before running, create secret files in `backend/secrets/`:

```
backend/secrets/
├── db_password.txt        # PostgreSQL password
├── minio_password         # MinIO root password
├── redis_password         # Redis password
```

Each file should contain the corresponding password as plain text (no trailing newline).

### Full Build — `compose.yml`

Uses a multi-stage Dockerfile to build the backend from source inside Docker (no local Java/Gradle required). Starts all services (backend, frontend, PostgreSQL, MinIO, Redis):

```bash
docker compose up --build -d
```

The frontend will be available at `http://localhost:3000`. API requests are proxied to the backend by nginx.

### Development — `compose.dev.yml`

Expects a pre-built JAR and mounts it from `backend/build/libs/`. Useful for faster iteration — you build the JAR locally once and let Docker Compose run it.

1. **Build the backend JAR locally:**

```bash
cd backend
./gradlew bootJar
```

2. **Start all services:**

```bash
docker compose -f compose.dev.yml up --build -d
```

The backend runs with `spring.profiles.active=dev`.

### Backend Only (without Docker)

If you want to run just the backend locally against existing database/MinIO/Redis instances:

```bash
cd backend
./gradlew bootRun
```

Configure connection details via environment variables or `application.yml` profiles.

### Running Tests

```bash
cd backend
./gradlew test
```

Tests use Testcontainers, so Docker must be running.

## Environment Variables

Key environment variables used in Docker Compose:

| Variable                        | Description                          |
|---------------------------------|--------------------------------------|
| `SPRING_PROFILES_ACTIVE`        | Spring profile (`dev` or `prod`)     |
| `SPRING_DATASOURCE_URL`         | JDBC connection string               |
| `SPRING_DATASOURCE_USERNAME`    | Database username                    |
| `MINIO_URL`                     | MinIO endpoint URL                   |
| `MINIO_ACCESS_NAME`             | MinIO access key                     |
| `SPRING_DATA_REDIS_HOST`        | Redis host                           |
| `SPRING_DATA_REDIS_PORT`        | Redis port                           |
| `SPRING_CONFIG_IMPORT`          | Path to secrets (config tree)        |

## CI/CD

GitHub Actions workflow (`.github/workflows/deploy.yml`) builds Docker images for backend and frontend, pushes them to GitHub Container Registry (`ghcr.io`), and deploys to the production server via SSH.
