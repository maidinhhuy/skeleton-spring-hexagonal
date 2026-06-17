dev-setup:
	docker compose -f docker/docker-compose.yml up -d postgres
	SPRING_PROFILES_ACTIVE=$${SPRING_PROFILES_ACTIVE:-dev} ./gradlew :infrastructure:flywayMigrate :infrastructure:jooqCodegen :infrastructure:bootRun

generate:
	./gradlew :infrastructure:flywayMigrate :infrastructure:jooqCodegen

compile:
	./gradlew :infrastructure:compileJava -x :infrastructure:jooqCodegen

test:
	./gradlew test

dev:
	SPRING_PROFILES_ACTIVE=$${SPRING_PROFILES_ACTIVE:-dev} ./gradlew :infrastructure:bootRun

db-up:
	docker compose -f docker/docker-compose.yml up -d postgres

db-down:
	docker compose -f docker/docker-compose.yml down
