# Week 1
Week 1 Software Engineering Internship Tasks


## Week 2 — Library Management System

The Week 2 implementation is available on the `feature/week-2-library-management` branch. It includes book CRUD operations, a Factory for creating books, an Observer-based notification service, a Singleton JSON file database, a command-line interface, and JUnit 5 tests.

### Run Week 2
```bash
mvn test
mvn compile exec:java
```

The CLI stores records in `books.json`. Use the menu to add, list, update, delete, borrow, and return books. The `main` branch retains the Week 1 work; Week 2 is kept in its own feature branch.