# Social Backend with Redis Guardrails

This is a Spring Boot-based backend application for a social platform that features strict Redis-based rate limiting, cooldowns, and guardrails for automated bot interactions.

## Features

- **User and Bot Management**: Separate entities and APIs for standard users and automated bots.
- **Post & Comment System**: Users and bots can interact with posts through comments.
- **Redis Guardrails**: 
  - Restricts the number of bot comments per post.
  - Enforces cooldown periods between bot interactions.
  - Limits comment depth to prevent infinite reply chains (maximum depth 20).
- **Virality Scoring**: Tracks and increments virality scores in Redis based on user and bot interactions (likes, comments).
- **Docker Integration**: Includes a `docker-compose.yml` to quickly spin up required PostgreSQL and Redis dependencies.

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.13** (Web, Data JPA, Data Redis)
- **PostgreSQL** (Primary Database)
- **Redis** (Caching, Guardrails, Rate Limiting)
- **Springdoc OpenAPI** (Swagger UI)

## Getting Started

### Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

### Starting the Infrastructure

The project relies on PostgreSQL and Redis. You can start these services using the provided `docker-compose.yml`:

```bash
docker-compose up -d
```
This will start:
- PostgreSQL on port `5435` (Username: `admin`, Password: `admin123`)
- Redis on port `6379`

### Running the Application

You can run the application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```
*(On Windows, use `mvnw.cmd spring-boot:run`)*

### API Documentation

Once the application is running, you can access the Swagger UI to explore and interact with the API:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

## API Endpoints Overview

- **Users**: `POST /api/users` - Create standard or premium users.
- **Bots**: `POST /api/bots` - Create and manage bots with specific personas.
- **Posts**: 
  - `POST /api/posts` - Create posts.
  - `POST /api/posts/{postId}/like` - Like a post and update virality.
- **Comments**: `POST /api/posts/{postId}/comments` - Add comments to a post. Interactions from bots are subject to validation by Redis guardrails.

## Architecture Highlights

The core business logic enforcing social rules can be found in the `CommentService` and `RedisGuardrailService` classes. They handle concurrent requests and enforce strict limitations without keeping local state, relying on Redis for robust rate limiting and locking.
