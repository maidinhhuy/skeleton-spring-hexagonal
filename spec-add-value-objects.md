---
title: 'Add Value Objects to Domain and Bounds'
type: 'refactor'
created: '2026-06-18'
status: 'done'
baseline_commit: 'f2b08bcd90e2e8f83f43d672b95ae6394597fa5c'
context: []
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Toàn bộ domain entity `User` và các port bound (`RegisterUserCommand`, `UserResponse`) đang dùng primitive types (`String`, `Long`) — không có type-safety, không validation, dễ truyền nhầm giá trị.

**Approach:** Áp dụng rule: **tất cả field trong domain entity và port bound đều dùng Value Objects**. Chỉ map ra primitive tại adapter ngoài cùng — web controller (String ↔ VO) và persistence adapter (VO ↔ DB column).

## Boundaries & Constraints

**Always:**
- VO sống trong `core/domain/value/` — pure Java, không Spring/Jackson
- `User` entity dùng: `UserId`, `Email`, `PasswordHash`, `Role`
- `RegisterUserCommand` dùng: `Email email`, `Password password`
- `UserResponse` dùng: `UserId`, `Email`, `Role`
- Controller nhận **DTO request** riêng (String fields) → map sang command/VO trước khi gọi use case
- Controller map `UserResponse` (VO) → **DTO response** riêng (primitive fields) trước khi trả JSON
- Persistence adapter gọi `.value()` / `.name()` để unwrap VO khi query/insert jOOQ; wrap lại VO khi map record → entity
- Email validate format + normalize lowercase tại construction
- `UserId` chỉ wrap `Long`, không validate thêm (DB-generated)

**Ask First:**
- Nếu muốn thêm validation rule cho `Password` (vd: min length, complexity)
- Nếu muốn thêm VO cho các entity/command khác ngoài `User`

**Never:**
- Không thêm Spring / Jackson annotation vào VO trong `core`
- Không thay đổi schema DB hoặc jOOQ generated code
- Không để `UserResponse` record được dùng trực tiếp làm HTTP response body (phải qua DTO)
- Không thay đổi REST endpoint path hay HTTP method

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Valid registration | `POST /register` `{email: "User@Ex.com", password: "pass"}` | `User` lưu với `Email("user@ex.com")`, `Role.USER`; HTTP 200 `{id, email, role}` primitives | N/A |
| Invalid email format | `{email: "not-an-email", password: "pass"}` | `IllegalArgumentException` khi controller tạo `Email` VO | 400 / exception propagates |
| Duplicate email | Email đã tồn tại trong DB | `IllegalArgumentException("Email already registered")` | N/A |

</frozen-after-approval>

## Code Map

- `modules/core/src/main/java/com/example/app/domain/value/Email.java` -- VO mới: record wrap String, validate + normalize
- `modules/core/src/main/java/com/example/app/domain/value/Role.java` -- VO mới: enum USER / ADMIN
- `modules/core/src/main/java/com/example/app/domain/value/UserId.java` -- VO mới: record wrap Long
- `modules/core/src/main/java/com/example/app/domain/value/Password.java` -- VO mới: record wrap String plain password (input credential)
- `modules/core/src/main/java/com/example/app/domain/value/PasswordHash.java` -- VO mới: record wrap String hash
- `modules/core/src/main/java/com/example/app/domain/entity/User.java` -- dùng 4 VO thay raw types
- `modules/core/src/main/java/com/example/app/port/bound/RegisterUserCommand.java` -- `Email email`, `String password`
- `modules/core/src/main/java/com/example/app/port/bound/UserResponse.java` -- `UserId id`, `Email email`, `Role role`
- `modules/core/src/main/java/com/example/app/port/out/UserRepository.java` -- `findByEmail(Email)`
- `modules/application/src/main/java/com/example/app/application/service/RegisterUserService.java` -- dùng VOs, `new Email(...)`, `Role.USER`
- `modules/infrastructure/src/main/java/com/example/app/adapter/persistence/UserRepositoryImpl.java` -- unwrap VO → primitive cho jOOQ; wrap primitive → VO khi map
- `modules/infrastructure/src/main/java/com/example/app/adapter/web/UserController.java` -- nhận `RegisterUserHttpRequest` DTO, map → command; map response → `UserHttpResponse` DTO
- `modules/infrastructure/src/main/java/com/example/app/adapter/web/dto/RegisterUserHttpRequest.java` -- DTO mới: `String email, password` (Jackson @RequestBody)
- `modules/infrastructure/src/main/java/com/example/app/adapter/web/dto/UserHttpResponse.java` -- DTO mới: `Long id, String email, String role` (Jackson response)

## Tasks & Acceptance

**Execution:**
- [x] `modules/core/src/main/java/com/example/app/domain/value/Email.java` -- CREATE: `record Email(String value)` compact constructor validate `^[^@\s]+@[^@\s]+\.[^@\s]+$`, normalize `value = value.toLowerCase()`
- [x] `modules/core/src/main/java/com/example/app/domain/value/Role.java` -- CREATE: `enum Role { USER, ADMIN }`
- [x] `modules/core/src/main/java/com/example/app/domain/value/UserId.java` -- CREATE: `record UserId(Long value)` không validate
- [x] `modules/core/src/main/java/com/example/app/domain/value/PasswordHash.java` -- CREATE: `record PasswordHash(String value)` không validate
- [x] `modules/core/src/main/java/com/example/app/domain/entity/User.java` -- MODIFY: fields → `UserId id`, `Email email`, `PasswordHash passwordHash`, `Role role`
- [x] `modules/core/src/main/java/com/example/app/domain/value/Password.java` -- CREATE: `record Password(String value)` compact constructor validate non-null/non-blank
- [x] `modules/core/src/main/java/com/example/app/port/bound/RegisterUserCommand.java` -- MODIFY: `Email email`, `Password password`
- [x] `modules/core/src/main/java/com/example/app/port/bound/UserResponse.java` -- MODIFY: `UserId id`, `Email email`, `Role role`
- [x] `modules/core/src/main/java/com/example/app/port/out/UserRepository.java` -- MODIFY: `findByEmail(Email email)`
- [x] `modules/application/src/main/java/com/example/app/application/service/RegisterUserService.java` -- MODIFY: `cmd.email()` đã là `Email`; `new PasswordHash(passwordHasher.hash(cmd.password().value()))`; dùng `Role.USER`
- [x] `modules/infrastructure/src/main/java/com/example/app/adapter/web/dto/RegisterUserHttpRequest.java` -- CREATE: `record RegisterUserHttpRequest(String email, String password)`
- [x] `modules/infrastructure/src/main/java/com/example/app/adapter/web/dto/UserHttpResponse.java` -- CREATE: `record UserHttpResponse(Long id, String email, String role)`
- [x] `modules/infrastructure/src/main/java/com/example/app/adapter/web/UserController.java` -- MODIFY: nhận `RegisterUserHttpRequest`, tạo `new RegisterUserCommand(new Email(req.email()), new Password(req.password()))`, map response `new UserHttpResponse(res.id().value(), res.email().value(), res.role().name())`
- [x] `modules/infrastructure/src/main/java/com/example/app/adapter/persistence/UserRepositoryImpl.java` -- MODIFY: `findByEmail` dùng `email.value()` trong where clause; map record → entity dùng `new Email(r.getEmail())`, `new UserId(r.getId())`, `new PasswordHash(r.getPasswordHash())`, `Role.valueOf(r.getRole())`; `save` dùng `user.getEmail().value()`, `user.getPasswordHash().value()`, `user.getRole().name()`

**Acceptance Criteria:**
- Given email hợp lệ `"User@Ex.com"`, when controller nhận request, then `Email` VO được tạo với value `"user@ex.com"` (normalized lowercase)
- Given email sai format, when `new Email(...)` được gọi, then `IllegalArgumentException` ném ngay — không có DB call
- Given `User` entity được build, when field được đọc, then return đúng VO type (`getEmail()` → `Email`, `getRole()` → `Role`, `getId()` → `UserId`)
- Given `UserResponse` với VOs, when controller map sang `UserHttpResponse`, then JSON response chứa primitive types (`id` là number, `email` là string, `role` là string)
- Given compilation, when `./gradlew :modules:core:compileJava`, then success (không có infra deps trong core)

## Design Notes

**Mapping layers rõ ràng:**

```
HTTP JSON (String) 
  → [UserController: new Email(), new RegisterUserCommand()] 
    → RegisterUserCommand(Email, String) 
      → [RegisterUserService] 
        → User(UserId, Email, PasswordHash, Role) 
          → [UserRepositoryImpl: .value(), .name()] 
            → DB row (String/Long)
```

**Email VO:**
```java
public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Invalid email: " + value);
        value = value.toLowerCase();
    }
}
```

**RegisterUserService sau khi refactor:** `cmd.email()` → `Email`, `cmd.password().value()` → raw String để hash, kết quả wrap vào `new PasswordHash(...)`. Service không biết gì về HTTP hay DB.

## Verification

**Commands:**
- `./gradlew :modules:core:compileJava` -- expected: BUILD SUCCESSFUL
- `./gradlew :modules:application:compileJava` -- expected: BUILD SUCCESSFUL
- `./gradlew compileJava` -- expected: BUILD SUCCESSFUL

## Spec Change Log

## Suggested Review Order

**Value Object definitions (core/domain/value)**

- Entry point: Email VO — validation logic + lowercase normalization
  [`Email.java:3`](modules/core/src/main/java/com/example/app/domain/value/Email.java#L3)

- Password VO — non-blank guard; sole validation at construction
  [`Password.java:3`](modules/core/src/main/java/com/example/app/domain/value/Password.java#L3)

- Role enum — replaces magic string `"USER"` with type-safe constant
  [`Role.java:3`](modules/core/src/main/java/com/example/app/domain/value/Role.java#L3)

- UserId + PasswordHash — identity/hash wrappers, no validation by design
  [`UserId.java:3`](modules/core/src/main/java/com/example/app/domain/value/UserId.java#L3)

**Mapping boundary — web adapter (String ↔ VO)**

- Controller: String→VO conversion at HTTP entry; VO→primitive at response
  [`UserController.java:24`](modules/infrastructure/src/main/java/com/example/app/adapter/web/UserController.java#L24)

- Exception handler: maps `IllegalArgumentException` (VO validation) → 400
  [`GlobalExceptionHandler.java:10`](modules/infrastructure/src/main/java/com/example/app/adapter/web/GlobalExceptionHandler.java#L10)

**Mapping boundary — persistence adapter (VO ↔ DB)**

- findByEmail: unwraps Email.value() for jOOQ; wraps DB row back to VOs
  [`UserRepositoryImpl.java:23`](modules/infrastructure/src/main/java/com/example/app/adapter/persistence/UserRepositoryImpl.java#L23)

**Application layer — use case**

- Service: password hashing via Password.value(); wraps result in PasswordHash VO
  [`RegisterUserService.java:31`](modules/application/src/main/java/com/example/app/application/service/RegisterUserService.java#L31)
