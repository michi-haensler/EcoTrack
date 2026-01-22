# EcoTrack Server (Backend)

## Übersicht

Das Backend für EcoTrack - eine Nachhaltigkeits-App für Schulen.

- **Technologie:** Spring Boot 3.x, Java 21
- **Architektur:** Modularer Monolith mit DDD & Hexagonal Architecture
- **Datenbank:** PostgreSQL
- **Authentifizierung:** Keycloak

## Modul-Struktur

Das Backend folgt Domain-Driven Design mit klarer Bounded Context-Trennung:

| Modul | Domain-Typ | Architektur |
|-------|-----------|-------------|
| `module-scoring` | Core Domain | Hexagonal Architecture |
| `module-challenge` | Core Domain | Hexagonal Architecture |
| `module-userprofile` | Supporting Domain | CRUD |
| `module-administration` | Generic Domain | ACL (Keycloak) |

## Voraussetzungen

- Java 21 (LTS)
- Maven 3.9+
- Docker (für lokale Entwicklung)

## Lokale Entwicklung

### Infrastruktur starten

```bash
cd ../infra
docker-compose up -d
```

### Server starten

```bash
mvn spring-boot:run
```

### Tests ausführen

```bash
mvn test
```

### Build erstellen

```bash
mvn clean install
```

## API-Dokumentation

Nach dem Start ist die API-Dokumentation verfügbar unter:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## Projekt-Struktur

```
server/
├── pom.xml                          # Parent POM
├── application/                     # Main Application Module
│   └── src/main/java/
│       └── at/htl/ecotrack/
│           └── EcoTrackApplication.java
├── module-scoring/                  # Core Domain - Scoring
│   ├── pom.xml
│   └── src/
│       ├── main/java/.../scoring/
│       │   ├── domain/              # Domain Layer
│       │   ├── application/         # Application Layer (Use Cases)
│       │   ├── adapter/             # Adapter Layer (In/Out)
│       │   └── ScoringModuleFacade.java
│       └── test/
├── module-challenge/                # Core Domain - Challenges
├── module-userprofile/              # Supporting Domain
├── module-administration/           # Generic Domain (Keycloak ACL)
└── shared-kernel/                   # Gemeinsame Domain-Objekte
```

## Coding Standards

Siehe [Java Backend Instructions](../.github/instructions/java-backend.instructions.md)
