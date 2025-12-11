# EcoTrack Server

Modularer Monolith Backend für die EcoTrack Nachhaltigkeits-App.

## 🏗️ Architektur

Das Backend folgt den Prinzipien aus "Von Bounded Contexts zu Architekturmustern":

### Bounded Contexts & Architekturmuster

| Modul | Domain-Typ | Architektur | Beschreibung |
|-------|-----------|-------------|--------------|
| `module-scoring` | Core Domain | Hexagonal | Aktivitäten loggen, Punkte berechnen |
| `module-challenge` | Core Domain | Hexagonal | Challenges verwalten, Teilnahme tracken |
| `module-userprofile` | Supporting Domain | CRUD | Benutzerprofile, Levels, Milestones |
| `module-administration` | Generic Domain | ACL | Schulen/Klassen verwalten, Keycloak-Integration |

### Modulstruktur

```
server/
├── shared-kernel/           # Gemeinsame Value Objects & Interfaces
├── module-scoring/          # Core Domain - Hexagonal Architecture
│   ├── domain/             # Entities, Value Objects, Events
│   ├── application/        # Use Cases, DTOs, Services
│   ├── adapter/            # In/Out Adapters (REST, JPA)
│   └── api/                # Module Facade (öffentliche API)
├── module-challenge/        # Core Domain - Hexagonal Architecture
├── module-userprofile/      # Supporting Domain - CRUD
├── module-administration/   # Generic Domain - ACL zu Keycloak
└── ecotrack-app/           # Host Application
    ├── config/             # Security, OpenAPI, Flyway
    └── resources/
        └── db/migration/   # Flyway Migrations pro Schema
```

## 🚀 Quick Start

### Voraussetzungen

- Java 21
- Maven 3.9+
- Docker & Docker Compose

### 1. Infrastruktur starten

```bash
cd server
docker-compose up -d
```

Dies startet:
- PostgreSQL (Port 5432)
- Keycloak (Port 8180)

### 2. Keycloak konfigurieren

1. Öffne http://localhost:8180
2. Login mit `admin` / `admin`
3. Erstelle Realm "ecotrack"
4. Erstelle Client "ecotrack-app" (Public Client, Authorization Code Flow)
5. Erstelle Rollen: `student`, `teacher`, `admin`
6. Erstelle Test-Benutzer und weise Rollen zu

### 3. Backend starten

```bash
mvn clean install
cd ecotrack-app
mvn spring-boot:run
```

API verfügbar unter: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

## 📊 Datenbank-Schemas

Jedes Modul hat sein eigenes PostgreSQL-Schema:

| Schema | Modul | Tabellen |
|--------|-------|----------|
| `scoring` | module-scoring | action_definitions, activity_entries, points_ledger_entries |
| `challenge` | module-challenge | challenges, challenge_participations |
| `userprofile` | module-userprofile | eco_users, milestones, eco_user_milestones |
| `admin` | module-administration | schools, school_classes, class_teachers |

## 🔒 Security

- OAuth2/OIDC mit Keycloak
- JWT Token Validierung
- Rollen-basierte Autorisierung (STUDENT, TEACHER, ADMIN)

## 🧪 Tests

```bash
# Unit & Integration Tests
mvn test

# ArchUnit Tests (Modul-Grenzen prüfen)
mvn test -Dtest=ModuleArchitectureTest
```

## 📦 Module APIs

Module kommunizieren nur über ihre öffentlichen Fassaden:

```java
// Scoring Module API
ScoringModuleFacade scoring;
scoring.logActivity(command);

// Challenge Module API
ChallengeModuleFacade challenges;
challenges.getActiveClassChallenges(classId);

// UserProfile Module API
UserProfileModuleFacade userProfile;
userProfile.getEcoUser(ecoUserId);

// Administration Module API
AdministrationModuleFacade admin;
admin.getSchoolClass(classId);
```

## 📡 Event-Kommunikation

Module kommunizieren asynchron über Spring Application Events:

```
[Scoring] --ActivityLoggedEvent--> [UserProfile]
          (Punkte aktualisieren)

[Challenge] --ChallengeGoalReachedEvent--> [Scoring]
            (Bonus-Punkte vergeben)
```
