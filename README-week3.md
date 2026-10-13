# Skill Nexus Week 3 — Blog Platform API

This branch implements a blog REST API with JWT-style bearer authentication, post CRUD, comments, likes, pagination, search, request validation, rate limiting, and Swagger UI documentation.

## Run
- Java 17+
- Maven 3.8+
- Run `mvn spring-boot:run`
- Open `http://localhost:8080/swagger-ui.html`

Detailed endpoint examples are in [docs/openapi-examples.md](docs/openapi-examples.md).

**Note:** This is an educational in-memory implementation. Users and posts reset after restart. Set `JWT_SECRET` to a long random secret for local testing; production needs persistent storage, HTTPS, and further security review.
