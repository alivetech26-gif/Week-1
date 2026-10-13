# Skill Nexus Software Engineering Internship

This repository contains the weekly internship work. Each week is maintained on its own feature branch.

- Week 1: `main` — Java OOP exercises, SOLID calculator, and task manager.
- Week 2: [Library Management System](https://github.com/alivetech26-gif/Week-1/tree/feature/week-2-library-management)
- Week 3: [Blog Platform REST API](https://github.com/alivetech26-gif/Week-1/tree/feature/week-3-blog-api)

## Week 3 — Blog Platform API

Spring Boot REST API with registration/login and signed bearer tokens, post CRUD, comments, likes, pagination, search, input validation, rate limiting, and Swagger UI.

### Run Week 3
Requires Java 17 and Maven:
```bash
mvn spring-boot:run
```

Open Swagger UI at `http://localhost:8080/swagger-ui.html`. See `docs/openapi-examples.md` for endpoint examples and `docs/system-design.md` for the URL shortener architecture and social-media database schema.

### Limitations
The current learning implementation stores users and posts in memory, so data resets after restart. Use a strong `JWT_SECRET` environment variable. Before production, add persistent database storage, HTTPS, durable rate limiting, and comprehensive integration/security tests.
