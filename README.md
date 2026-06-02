# Gateway Service

The single entry point for all client-facing API traffic. Every request from the mobile app goes through the gateway, which validates the JWT, routes the request to the correct microservice, and returns the response.

---

## Overview

| | |
|---|---|
| **Port** | `8080` |
| **Framework** | Spring Boot 4.x + Spring Cloud Gateway MVC |
| **Java** | 25 |

---

## Responsibilities

- **Single entry point** — clients only need to know one URL (`http://host:8080`)
- **JWT validation** — validates the Bearer token on every request before forwarding
- **Routing** — forwards requests to the correct downstream service via Eureka load balancing
- **Security** — blocks all `/internal/**` paths from reaching clients
- **CORS** — handles cross-origin requests centrally

---

## Routing Table

All routes use **Eureka load balancing** — the gateway discovers service instances by name, no hardcoded URLs.

| Client Request | Routed To | Service |
|---|---|---|
| `GET /auth/**` | `lb://auth` | Auth Service (8081) |
| `GET /users/**` | `lb://user` | User Service (8082) |
| `GET /chats/**` | `lb://chat` | Chat Service (8083) |
| `GET /messages/**` | `lb://message` | Message Service (8084) |
| `GET /notifications/**` | `lb://notification` | Notification Service (8085) |

> **Note:** WebSocket connections (`/ws/user`) connect **directly** to the Notification Service on port `8085`, bypassing the gateway. Spring Cloud Gateway MVC does not support WebSocket proxying.

---

## Security

### JWT Validation

Every request (except `/auth/**`) must include a valid Bearer token:

```
Authorization: Bearer <jwt_token>
```

The gateway decodes and validates the token using the shared HMAC-SHA256 secret. If invalid or missing, it returns `401 Unauthorized`. The token is **not** re-validated by downstream services — they trust the gateway.

### Blocked Paths

Internal service-to-service endpoints are explicitly blocked from external access:

```
/auth/internal/**      → 403 Forbidden
/chats/internal/**     → 403 Forbidden
/messages/internal/**  → 403 Forbidden
```

These paths exist only for Feign clients between services and must never be reachable by clients.

### Public Paths

```
/auth/login      → no JWT required
/auth/register   → no JWT required
/auth/refresh    → no JWT required
```

---

## Configuration

```properties
# application-dev.properties
server.port=8080

jwt.secret=<base64-encoded-256-bit-secret>   # must match auth service

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Generating a JWT Secret

```bash
openssl rand -base64 32
```

The same secret must be set in **all services** that validate JWTs (gateway, auth, user, chat, message, notification).

---

## Running Locally

Start the **registry service first**, then:

```bash
./gradlew bootRun
```

---

## Dependencies

| Dependency | Purpose |
|---|---|
| `spring-cloud-starter-gateway-server-webmvc` | MVC-based gateway routing |
| `spring-cloud-starter-netflix-eureka-client` | Service discovery for load balancing |
| `spring-boot-starter-security` | Security filter chain |
| `spring-boot-starter-security-oauth2-resource-server` | JWT validation |
| `spring-boot-starter-actuator` | Health checks and route inspection |

---

## API Response Format

All downstream services return a consistent envelope:

```json
{
  "status": true,
  "message": "Operation successful",
  "data": { ... }
}
```

Error responses:

```json
{
  "status": false,
  "message": "Unauthorized",
  "code": "UNAUTHORIZED",
  "data": null
}
```

---

## Architecture Notes

- Uses **Spring Cloud Gateway MVC** (servlet-based), not the reactive WebFlux variant. This means it runs on a standard Tomcat thread pool — simpler but not reactive.
- Load balancing uses **Spring Cloud LoadBalancer** backed by Eureka service discovery.
- CORS is configured to allow `http://localhost:3000` (web client). Update `allowedOrigins` for production.
