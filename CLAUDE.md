# Spring Hexagonal Skeleton

## Project Structure

```
.
├── modules/
│   ├── core/           ← domain entities, port interfaces. Pure Java + Lombok, no framework.
│   ├── application/    ← use case implementations. Pure Java + Lombok + SLF4J, no Spring.
│   └── infrastructure/ ← Spring Boot, jOOQ, Flyway, JWT, controllers. All framework code.
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

## Hexagonal Pattern

| Layer | Package | Rule |
|---|---|---|
| Port in | `core/port/in/` | Use case interfaces |
| Port out | `core/port/out/` | Repository & client interfaces (PasswordHasher, TokenProvider, …) |
| Port bound | `core/port/bound/` | Command / Response records |
| Domain | `core/domain/entity/`, `core/domain/value/` | Pure Java |
| Use case | `application/application/service/` | No `@Service`, no Spring |
| Wiring | `infrastructure/config/UseCaseConfig.java` | `@Bean` factory for all services |
| Adapter in | `infrastructure/adapter/web/` | Controllers |
| Adapter out | `infrastructure/adapter/persistence/` | jOOQ repos implementing `port/out/` |
| Security impl | `infrastructure/security/` | BCryptPasswordHasher, JwtTokenProvider |

## Key Conventions

- Use case services: **no Spring annotations** — plain `@RequiredArgsConstructor` (Lombok)
- Config values (`@Value`): inject at the `@Bean` method level in `UseCaseConfig`, pass as constructor param
- Port interfaces for framework concerns: `PasswordHasher`, `TokenProvider` live in `core/port/out/` to keep `application` free of Spring deps
- jOOQ generated code: `infrastructure/jooq/` (git-ignored, regenerated via `make generate`)
- `infrastructure` must declare both `:core` and `:application` in its deps (Gradle doesn't expose transitive project deps)

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
