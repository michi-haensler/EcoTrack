# EcoTrack Backend - Coding Standards

## Projektübersicht

EcoTrack Backend ist ein modularer Monolith mit Spring Boot und Java 21.

### Architektur-Prinzipien

Das Backend folgt Domain-Driven Design mit klarer Bounded Context-Trennung:

| Modul | Domain-Typ | Architektur |
|-------|-----------|-------------|
| `module-scoring` | Core Domain | Hexagonal Architecture |
| `module-challenge` | Core Domain | Hexagonal Architecture |
| `module-userprofile` | Supporting Domain | CRUD |
| `module-administration` | Generic Domain | ACL (Keycloak) |

**Wichtig:** Module kommunizieren nur über:
- **Modul-Fassaden** (öffentliche API)
- **Domain Events** (Spring ApplicationEventPublisher)

## Technologie-Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA / Hibernate
- PostgreSQL
- Keycloak für Authentication
- MapStruct für Mapping
- JUnit 5, Mockito, AssertJ für Testing

## Naming Conventions

- PascalCase für Klassen, Interfaces
- camelCase für Variablen, Methoden
- UPPER_SNAKE_CASE für Konstanten
- Sprechende Namen ohne Abkürzungen

## Git Commit Messages

Format: `<type>(<scope>): <subject>`

Beispiel: `feat(scoring): add CO2 calculation for activities`
