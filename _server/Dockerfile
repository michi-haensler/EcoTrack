# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Abhängigkeiten vorab cachen (Layer-Optimierung)
COPY pom.xml .
COPY shared-kernel/pom.xml        shared-kernel/pom.xml
COPY module-userprofile/pom.xml   module-userprofile/pom.xml
COPY module-administration/pom.xml module-administration/pom.xml
COPY module-scoring/pom.xml       module-scoring/pom.xml
COPY module-challenge/pom.xml     module-challenge/pom.xml
COPY application/pom.xml          application/pom.xml

RUN mvn -B dependency:go-offline -q

# Quellcode kopieren und bauen (Tests überspringen — laufen in CI)
COPY . .
RUN mvn -B clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Non-root User für Sicherheit
RUN addgroup -S ecotrack && adduser -S ecotrack -G ecotrack
USER ecotrack

# JAR aus Build-Stage kopieren
COPY --from=build /build/application/target/application-*.jar app.jar

# Port auf dem der Server läuft
EXPOSE 8080

# Spring-Profile auf "prod" setzen
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
