# Spring Hexagonal Skeleton

## Project Structure

```
.
├── modules/
│   ├── core/           ← pure Java, no Spring, no Lombok beyond basics
│   ├── application/    ← @Service classes, @RequiredArgsConstructor, no Spring beans beyond @Service
│   └── infrastructure/ ← Spring Boot, jOOQ, Flyway, JWT, email, controllers. All framework code.
├── docker/
│   └── docker-compose.yml
├── Dockerfile
├── Makefile
├── build.gradle        ← root: Spotless + Java toolchain
└── settings.gradle     ← modules declared + projectDir mapping
```

## Module Dependency Chain

```
infrastructure → application → core
```

Never import upward (core must not know application/infrastructure).

## Hexagonal Pattern — `com.example.app`

| Layer | Package | Rule |
|---|---|---|
| Domain aggregates | `core/domain/aggregation/` | Aggregate roots (User, …) |
| Domain entities | `core/domain/entity/` | Sub-entities (auth, email, user) |
| Domain values | `core/domain/value/` | Value objects (Email, Password, UserId, …) |
| Domain exceptions | `core/domain/exception/` | Pure Java exceptions |
| Port in | `core/port/in/auth/`, `core/port/in/user/` | Use case interfaces |
| Port out | `core/port/out/auth/`, `core/port/out/email/`, `core/port/out/user/` | Repository & client interfaces |
| Port bound | `core/port/bound/command/` | Command objects (no query/ layer in skeleton) |
| Use case | `application/application/service/` | `@Service` + `@RequiredArgsConstructor` |
| Controller | `infrastructure/adapter/controller/` | Spring `@RestController` |
| Filter | `infrastructure/adapter/filter/` | `JwtAuthenticationFilter` |
| Request DTOs | `infrastructure/adapter/request/` | HTTP request records |
| Response DTOs | `infrastructure/adapter/response/` | HTTP response records |
| Web utilities | `infrastructure/adapter/web/` | `GlobalExceptionHandler`, `dto/` |
| Auth impl | `infrastructure/infrastructure/auth/` | `BCryptPasswordHasher`, `JwtTokenProvider`, `JwtProperties` |
| Email impl | `infrastructure/infrastructure/email/` | `ResendEmailClient`, `DevEmailClient` |
| Persistence | `infrastructure/infrastructure/repository/` | jOOQ repos implementing `port/out/` |
| jOOQ generated | `infrastructure/infrastructure/jooq/` | Git-ignored; run `make generate` |
| Spring config | `infrastructure/config/` | `SecurityConfig`, `EmailConfig`, `JooqConfiguration`, `OpenApiConfig`, `GlobalExceptionHandler`, `DataInitializer` |

## Key Conventions

- Use case services: `@Service` + `@RequiredArgsConstructor` (Lombok). Spring component scan handles wiring automatically — **no `UseCaseConfig.java`**.
- Auth/password/token implementations live in `infrastructure/auth/`, not `security/`.
- `infrastructure` must declare both `:core` and `:application` in its Gradle deps (transitive project deps are not exposed automatically).
- jOOQ generated code: `infrastructure/build/generated-src/jooq/` (git-ignored, regenerated via `make generate`).
- Config values (`@Value`): inject at the `@Bean` or `@ConfigurationProperties` level in `config/`, pass as constructor params.

## How to Start a New Project from This Skeleton

1. Clone / copy the skeleton
2. `grep -r "com.example.app" --include="*.java" -l` → rename package to your package
3. `grep -r "com.example.app" --include="*.gradle" -l` → update jOOQ target package
4. Rename `rootProject.name` in `settings.gradle`
5. Update DB creds in `docker/docker-compose.yml` and `application.yml`
6. Delete example `User` code and write your own domain
7. `make dev-setup`

## Common Commands

```bash
make dev-setup   # start postgres + migrate + jooq codegen + run
make generate    # flyway migrate + jooq codegen only
make compile     # fast compile (skip codegen)
make test        # run all tests
make dev         # run app (postgres must already be up)
make db-up       # start postgres only
make db-down     # stop postgres
```
