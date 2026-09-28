# Book Catalog REST API

A Spring Boot REST API for creating and maintaining a searchable catalog of books. The project uses a layered controller-service-repository design and stores data in MySQL.

## Entity

Each book has an auto-generated ID, title, author, unique ISBN, genre, and publication year. The API uses separate request and response DTOs so persistence entities are not exposed to clients.

## Technologies

- Java 17 and Spring Boot 4
- Gradle (project generated with Spring Initializr; Gradle Wrapper included)
- Spring Web MVC, Spring Data JPA, Bean Validation
- MySQL 8.4
- Springdoc OpenAPI / Swagger UI
- H2 for local and automated test profiles
- Docker and Docker Compose

## API endpoints

All endpoints use `/api/books`.

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/books` | Create a book; returns `201 Created` and a `Location` header |
| `GET` | `/api/books` | Get a paginated, sorted list |
| `GET` | `/api/books/{id}` | Get a book by ID |
| `PUT` | `/api/books/{id}` | Replace a book's editable fields |
| `DELETE` | `/api/books/{id}` | Delete a book; returns `204 No Content` |
| `GET` | `/api/books/search?term=dune` | Search titles and authors |
| `GET` | `/api/books/search?genre=fantasy` | Filter by genre |

List and search endpoints accept `page` (default `0`), `size` (default `20`, maximum `100`), `sortBy`, and `direction` (`asc` or `desc`). Supported sort fields are `id`, `title`, `author`, `genre`, and `publishedYear`.

Example create request:

```json
{
  "title": "The Hobbit",
  "author": "J. R. R. Tolkien",
  "isbn": "9780547928227",
  "genre": "Fantasy",
  "publishedYear": 1937
}
```

Validation requires non-blank, length-limited title/author/genre fields, an ISBN-10 or ISBN-13 without separators, and a publication year between 1450 and 2100. Errors return a consistent JSON body with a timestamp, status, message, request path, and (for body validation) field-level errors.

## Additional features

- Derived JPA query for case-insensitive genre filtering.
- Custom JPQL `@Query` for matching title or author.
- Global exception handling for not-found, duplicate ISBN, request validation, and database-integrity errors.
- Pagination and sorting with bounded page sizes and a safe sort-field allowlist.
- OpenAPI documentation at `/swagger-ui.html` (JSON spec at `/v3/api-docs`).
- H2 `local` profile for running without a local MySQL installation; MySQL remains the default database.

## Run locally

Requires JDK 17 or newer. The Gradle Wrapper downloads the required Gradle distribution on first use.

### Quick start with the local H2 profile

```powershell
.\gradlew.bat bootRun --args='--spring.profiles.active=local'
```

The API is available at `http://localhost:8080`; Swagger UI is at `http://localhost:8080/swagger-ui.html`. The H2 console is available at `http://localhost:8080/h2-console` using JDBC URL `jdbc:h2:mem:bookcatalog`, username `sa`, and an empty password.

### Run against MySQL

Start a local MySQL 8.4 server and create the `book_catalog` database. Set connection values with environment variables, then run the default profile:

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'book_catalog'
$env:DB_USERNAME = 'your_mysql_user'
$env:DB_PASSWORD = 'your_mysql_password'
.\gradlew.bat bootRun
```

For tests, run:

```powershell
.\gradlew.bat test
```

Tests use an isolated in-memory H2 database and do not require MySQL.

## Docker

The root `Dockerfile` builds the executable Spring Boot JAR in a Gradle build stage and runs it on a JRE image. The image build is optional:

```powershell
docker build -t book-catalog-api .
```

To run the API and MySQL together with Docker Compose:

```powershell
docker compose up --build
```

Compose maps the API to port `8080` and MySQL to port `3306`. Its default credentials are for local development only; set `DB_PASSWORD` and `MYSQL_ROOT_PASSWORD` in the environment before using Compose outside a local development environment. Stop the services with `Ctrl+C`, then run `docker compose down` to remove the containers. The named MySQL data volume is retained unless explicitly removed.
