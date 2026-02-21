# Vora Backend (Admin + User)

Initial Spring Boot backend starter for a shared e-commerce API used by both admin and user React apps.

## Tech

- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- H2 (default local DB)

## Run

```bash
mvn spring-boot:run
```

## Default users (seeded)

- Admin: `admin@vora.com` / `admin123`
- Customer: `user@vora.com` / `user123`

## Key APIs

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### User side

- `GET /api/user/me` (requires token)

### Admin side

- `GET /api/admin/dashboard` (ADMIN)
- `GET /api/admin/products` (ADMIN)
- `POST /api/admin/products` (ADMIN)
- `PUT /api/admin/products/{id}` (ADMIN)
- `DELETE /api/admin/products/{id}` (ADMIN)

Use JWT token as `Authorization: Bearer <token>`.
