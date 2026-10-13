# Week 4 — Containerisation, CI/CD, Deployment and Monitoring

## Architecture
- Java 17 and Spring Boot REST API
- Docker multi-stage build: Maven build stage and a smaller JRE runtime stage
- Docker Compose for repeatable local container execution
- GitHub Actions runs the Maven test suite and packages the application on pushes and pull requests
- Spring Boot Actuator health endpoint for basic runtime health checks
- Swagger UI for API exploration

## Run locally without Docker
Install JDK 17 and Maven, then run:
```bash
mvn clean test
mvn spring-boot:run
```
Visit `http://localhost:8080/swagger-ui.html` and `http://localhost:8080/actuator/health`.

## Run with Docker
Create a long random secret in your terminal session (do not commit it):
PowerShell:
```powershell
$env:JWT_SECRET = [guid]::NewGuid().ToString() + [guid]::NewGuid().ToString()
docker compose up --build -d
docker compose logs -f blog-api
```
Linux/macOS:
```bash
export JWT_SECRET="$(openssl rand -hex 32)"
docker compose up --build -d
docker compose logs -f blog-api
```
Stop with `docker compose down`. The app listens on port 8080 by default.

## GitHub Actions CI/CD
The workflow at `.github/workflows/ci.yml` checks out the repository, installs Java 17, runs tests, and packages the application. This is continuous integration. To enable automatic cloud deployment, connect the repository to a hosting provider such as Render or Railway and configure deployment from the desired branch in that provider's dashboard. Store secrets in the provider's environment settings, not in source code.

## Cloud deployment checklist
1. Push this branch to GitHub and ensure the Actions workflow completes successfully.
2. Create a Docker-based web service on your chosen provider and select this repository and branch.
3. Configure `JWT_SECRET` as a strong random environment variable.
4. Configure the service to expose port 8080 (or use the provider's assigned port mapping).
5. After deployment, check `/actuator/health` and `/swagger-ui.html`.
6. Enable HTTPS, restrict secret access, and review service logs.

## Monitoring and security notes
The health endpoint provides a basic liveness signal. Use the hosting provider's logs and metrics for initial monitoring; production services should add structured logs, alerting, dashboards, persistent storage, and backups.

## Current project limitations
This branch is an educational deployment extension of the Week 3 API. The existing Week 3 implementation keeps users and posts in process memory; data is lost on restart and does not scale across instances. A production SaaS release still requires a persistent database, robust token/secret management, integration/security tests, and deployment verification. The CI workflow has been committed but must run in GitHub Actions to confirm its result. No cloud deployment is claimed until it is actually performed and verified.
