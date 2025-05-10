# Book Catalog API

A secure backend service for managing books with admin login and two-factor authentication. You can log in as an admin, get a temporary access code using an authenticator app, and then add, view, or manage books.

---

## Features

* Admin login with password
* Two-step verification using Google Authenticator
* Add, update, delete, and view books
* Protected APIs that require login
* Swagger documentation (built-in testing UI)

---

## How it works (in simple terms)

1. You log in with your username and password.
2. If correct, you'll get a temporary token.
3. You then open your Google Authenticator app and enter the 6-digit code.
4. If the code is correct, you receive a final token.
5. You use this final token to access all other APIs securely.

---

## Try It Out

You can try the API using Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### Sample Admin Login

```
username: admin
password: password
```

Make sure to set up Google Authenticator using the secret from your environment variable (`TOTP_SECRET`).

---

## Sample Book Entry

Here's an example book JSON you can POST after logging in:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "genre": "Programming",
  "publishedDate": "2008-08-01",
  "summary": "A handbook of agile software craftsmanship."
}
```

---

## Technical Setup (for developers)

### Stack

* Java 21 + Spring Boot 3
* MySQL (prod), H2 (test/dev)
* Spring Security with TOTP (2FA)
* Swagger (Springdoc) for API docs
* JUnit 5 + Mockito for testing

### How to run locally

1. Clone the repo
2. Set environment variables:

    * `ADMIN_USERNAME`, `ADMIN_PASSWORD`
    * `TOTP_SECRET` (base32 string)
    * `SPRING_DATASOURCE_*` if using MySQL
    * `JWT_SECRET`, `jwt.authExpirationMinutes`, `jwt.otpExpirationMinutes`
3. Run the app:

```
./mvnw spring-boot:run
```

### Profiles

* `dev` → H2 in-memory DB, Swagger open
* `prod` → Use MySQL, JWT from env, secure settings

### Testing

```
./mvnw test
```

Includes:

* Unit tests for service logic and tokens
* Integration tests for login + book APIs
---

## Docker Setup

This project supports Docker for local development.

### Files:

* `Dockerfile`: builds the app into a secure image
* `docker-compose.yml`: spins up the app + MySQL together
* `.env.example`: shows the required environment variables
* `.env`: your local config (never committed)

### Prerequisites:

* Docker Desktop (Windows/Mac) or Docker CLI + Compose (Linux)

### Steps to run with Docker:

1. Copy the example env:

   ```bash
   cp .env.example .env
   # Edit .env with your actual local secrets (or leave defaults for dev)
   ```
2. Start containers:

   ```bash
   docker-compose up --build
   ```
3. Access:

   * Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   * MySQL: running internally at `db:3306`

### Stopping & resetting:

```bash
docker-compose down       # stop

docker-compose down -v    # stop & delete DB volume (wipe data)
```
