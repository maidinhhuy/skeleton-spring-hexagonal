# Deferred Work

## From: Add Value Objects to Domain and Bounds

- **Role.valueOf() brittle on unknown DB values** — if DB contains lowercase or legacy role strings, `Role.valueOf(r.getRole())` throws. Consider a safe parse method (e.g. `Role.fromString()`).
- **Email re-validation on DB read** — `new Email(r.getEmail())` re-runs validation when mapping from persistence. A bypass constructor for trusted DB data would be safer.
- **fetchOne() NPE in save()** — `dsl.insertInto(...).returning().fetchOne()` returns null on INSERT failure. Use `fetchSingle()` for explicit failure.
- **Email regex — no max length** — extremely long strings pass validation and hit DB constraint instead of a clean 400.
- **Password minimum length/complexity** — `Password` VO only validates non-blank. Add min-length rule when password policy is defined.
