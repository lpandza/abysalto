## Prerequisites

- **Java 17**
- **Docker** and **Docker Compose**
- **Maven** (or use the included `mvnw` wrapper)

## Getting Started

### Option A: Run with IntelliJ IDEA

Simply run the `Application` main class (`hr.abysalto.hiring.mid.Application`). The Spring Boot Docker Compose integration will automatically start the
PostgreSQL container.

### Option B: Run from command line

### 1. Clone and navigate to the project

```bash
cd java.mid
```

### 2. Start the database

```bash
docker-compose up -d
```

This starts a PostgreSQL database on port 5432.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api/v1`

### 4. Run tests

```bash
./mvnw test
```

## Notes

While this API does not include a frontend, I have built a similar frontend application using React and TypeScript,
available at: https://github.com/lpandza/react-app
