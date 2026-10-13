# Week 3 Blog Platform REST API

Base URL: `http://localhost:8080/api`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Authentication
- `POST /auth/register` — JSON: `{"username":"naman","password":"change-me-123"}`
- `POST /auth/login` — returns a signed JWT token. Send it on protected requests as `Authorization: Bearer <token>`.

## Posts
- `POST /posts` — create a post (authenticated)
- `GET /posts?page=0&size=10` — list posts with pagination
- `GET /posts?search=java&page=0&size=10` — search title and content
- `GET /posts/{id}` — get a post
- `PUT /posts/{id}` — update own post (authenticated)
- `DELETE /posts/{id}` — delete own post (authenticated)

Create/update JSON: `{"title":"My first post","content":"Hello from the blog API."}`

## Comments and likes
- `POST /posts/{id}/comments` — JSON `{"content":"Great post!"}` (authenticated)
- `POST /posts/{id}/likes` — like/unlike toggle (authenticated)

## Error and security behaviour
Invalid requests return 400, missing posts return 404, invalid/missing tokens return 401, and non-owner edits are rejected. The in-memory rate limiter allows 120 requests per client IP per minute and returns 429 after the limit. User and post data are in memory for this learning project, so they reset when the server restarts.

## Run locally
Requires JDK 17 and Maven:
```bash
mvn spring-boot:run
```

Set a strong `JWT_SECRET` environment variable outside local development. For production deployment, replace in-memory maps with a database, use HTTPS, configure persistent secrets, and add integration tests.
