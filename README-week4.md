# Skill Nexus Week 4 — SaaS Deployment Project

This branch packages the Week 3 Blog Platform REST API for a repeatable software delivery workflow.

## Included
- Docker multi-stage build and Docker Compose configuration
- GitHub Actions CI pipeline for Java 17, Maven tests, and packaging
- Deployment guide covering local execution, container startup, cloud-host setup, environment secrets, and monitoring
- Swagger API documentation inherited from Week 3
- Basic health-check endpoint through Spring Boot Actuator

## Quick start
```bash
mvn clean test
mvn spring-boot:run
```
Health: `http://localhost:8080/actuator/health`
Swagger: `http://localhost:8080/swagger-ui.html`

For Docker, set `JWT_SECRET` in the environment and run `docker compose up --build -d`. See [Week 4 deployment guide](docs/week4-deployment.md).

**Honest scope:** CI configuration and container/deployment files are included, but the pipeline, container, and cloud deployment have not been run or verified in this session. The inherited API still uses in-memory storage, so this is not yet production-ready. Persistent database integration and comprehensive automated tests remain necessary.
