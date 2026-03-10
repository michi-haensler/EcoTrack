# EcoTrack Backend - Implementierungsdokumentation

## 1. Ziel und Scope

Diese Dokumentation beschreibt die vollständige Backend-Implementierung, die im Rahmen der Aufgabenstellung (`Angabe`) und der User Stories für das EcoTrack-Projekt umgesetzt wurde.

Stand: 25. Februar 2026

Umgesetzt wurde ein modularer Spring-Boot-Monolith in `_server` mit folgenden Schwerpunkten:

- modulare Architektur (DDD-orientiert)
- Authentifizierung/Autorisierung
- Benutzer-/Klassenverwaltung
- Scoring, Aktivitäten, Fortschritt und Ranglisten
- Challenges und Lehrer-Dashboard
- Datenhaltung via JPA + Flyway
- Testabdeckung über Context- und API-Integrationstests

## 2. Ausgangslage

Beim Start der Arbeiten war `_server` nur ein Skelett:

- Verzeichnisstruktur mit `.gitkeep`
- keine `pom.xml`
- keine Java-Implementierung
- keine Flyway-Migrationen

Es existierten jedoch:

- Architektur- und Coding-Vorgaben
- User Story Doku
- OpenAPI-Vertrag in `shared-resources/api-contracts/openapi.yaml`
- Aufgabenliste in `_server/MS-01-TASKS.md`

## 3. Architektur und Projektstruktur

Es wurde ein Maven Multi-Module-Projekt erstellt.

### 3.1 Module

- `shared-kernel`
- `module-userprofile`
- `module-administration`
- `module-scoring`
- `module-challenge`
- `application` (Spring-Boot-Startmodul)

### 3.2 Hauptdateien

- Parent Build: `_server/pom.xml`
- App Start: `_server/application/src/main/java/at/htl/ecotrack/EcoTrackApplication.java`
- App Config: `_server/application/src/main/resources/application.yml`
- Migration: `_server/application/src/main/resources/db/migration/V1__init.sql`

## 4. Implementierte Fachbereiche

### 4.1 Shared Kernel

In `shared-kernel` wurden zentrale Typen implementiert:

- Enums: `Role`, `UserStatus`, `Category`, `Unit`, `ActivitySource`, `Level`, `PeriodType`, `ChallengeStatus`, `GoalUnit`
- Fehlerbasis: `ApiException`, `ErrorResponse`
- Security-Prinzipal: `CurrentUser`

### 4.2 Administration / Auth

Implementiert in `module-administration`:

- Entities:
  - `AppUser`
  - `SchoolClass`
  - `RefreshToken`
  - `PasswordResetToken`
- Repositories:
  - `AppUserRepository`
  - `SchoolClassRepository`
  - `RefreshTokenRepository`
  - `PasswordResetTokenRepository`
- Services:
  - `AuthService`
- Security:
  - `JwtTokenService`
  - `JwtAuthenticationFilter`
- Controller:
  - `AuthController`
  - `AdminController`

Funktionen:

- Registrierung
- Login (allgemein + Legacy/MS-01 Endpunkte)
- Logout (einzelnes Refresh-Token oder alle Sessions des Users)
- Passwort-Reset-Request (Rate-Limit)
- Lockout bei Fehlversuchen (5 Versuche / 5 Minuten)
- Admin User-/Class APIs

### 4.3 User Profile

Implementiert in `module-userprofile`:

- `EcoUserProfile` Entity + Repository
- `EcoUserProfileService`

Funktionen:

- Profilanlage bei Registrierung
- Lookup per `userId` und `ecoUserId`
- Klassen-/Schul-Zuordnung als Basis für Leaderboards und Dashboard

### 4.4 Scoring

Implementiert in `module-scoring`:

- Entities:
  - `ActionDefinition`
  - `ActivityEntry`
  - `PointsLedger`
- Repositories:
  - `ActionDefinitionRepository`
  - `ActivityEntryRepository`
  - `PointsLedgerRepository`
- Service:
  - `ScoringService`
- Controller:
  - `ScoringController`

Funktionen:

- Aktionskatalog lesen
- Aktivität erfassen
- Duplikaterkennung (gleiche Aktion/Menge/Datum innerhalb 5 Minuten)
- Punkteberechnung (`Menge * basePoints`)
- Ledger-Update
- Fortschritt/Levelberechnung
- Klassen- und Schul-Ranglisten inkl. Tie-Handling

### 4.5 Challenge und Dashboard

Implementiert in `module-challenge`:

- Entity: `Challenge`
- Repository: `ChallengeRepository`
- Service: `ChallengeService`
- Controller:
  - `ChallengeController`
  - `DashboardController`

Funktionen:

- Challenge anlegen
- Challenge-Liste/Detail/Fortschritt
- Statusberechnung (DRAFT/ACTIVE/CLOSED) aus Zeitraum
- Klassen-Dashboard (Aggregationen, Top-Aktionen, Top-Schüler)

### 4.6 Application Layer

Implementiert in `application`:

- `SecurityConfig`
- `ApiExceptionHandler`
- `EcoUserController`

## 5. API-Endpunkte

### 5.1 Auth / Legacy (MS-01)

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/password/reset-request`
- `GET /api/users/me`
- `POST /api/v1/auth/mobile/login`
- `POST /api/v1/auth/admin/login`
- `POST /api/v1/registration`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/password/reset-request`
- `GET /api/v1/users/me`

### 5.2 EcoUsers

- `GET /api/eco-users/me`
- `GET /api/eco-users/{ecoUserId}`

### 5.3 Scoring / Progress / Leaderboard

- `GET /api/activities/catalog`
- `POST /api/activities`
- `GET /api/activities`
- `GET /api/progress`
- `GET /api/progress/points`
- `GET /api/leaderboard/class`
- `GET /api/leaderboard/school`

### 5.4 Challenge / Dashboard

- `GET /api/challenges`
- `POST /api/challenges`
- `GET /api/challenges/{challengeId}`
- `GET /api/challenges/{challengeId}/progress`
- `GET /api/dashboard/class/{classId}`

### 5.5 Administration

- `GET /api/admin/users`
- `GET /api/admin/classes`
- `POST /api/admin/classes`

## 6. Datenmodell (Flyway)

Migration: `V1__init.sql`

Erstellte Tabellen:

- `app_users`
- `school_classes`
- `eco_user_profiles`
- `refresh_tokens`
- `password_reset_tokens`
- `action_definitions`
- `activity_entries`
- `points_ledgers`
- `challenges`

Zusätzlich:

- Seed-Daten für `action_definitions` wurden angelegt.

## 7. Security-Konzept

- Stateless JWT via `Authorization: Bearer <token>`
- Filter-basierte Authentication (`JwtAuthenticationFilter`)
- Rollen über `ROLE_<role>` Authorities
- Method Security (u. a. für Admin-Endpunkte)
- Public Endpunkte nur für Login/Registration/Reset + Doku/Health

## 8. Teststrategie und Ergebnis

### 8.1 Implementierte Tests

- `EcoTrackApplicationTests` (Spring Context)
- `ApiIntegrationTests` (MockMvc, echte API-Flows)

### 8.2 Getestete Flows

- Registrierung + Class-Anlage + Aktivität + Progress
- Lehrer-Login mit Password-Change-Policy
- Challenge-Erstellung via Lehrer

### 8.3 Letzter Testlauf

Befehl:

```bash
mvn -Dmaven.repo.local=/tmp/ecotrack-m2 test -q
```

Ergebnis:

- Exit Code `0`
- Test-Suite erfolgreich

## 9. Relevante Bugfixes während Finalisierung

- `shared-kernel` Dependency ergänzt (`spring-web`) für `HttpStatus`
- Modulgrenzen korrigiert (`CurrentUser` in `shared-kernel` verschoben)
- Fehlende Modulabhängigkeit in `module-challenge` ergänzt
- JWT Principal-Mismatch im Filter korrigiert (NPE in Integrationstests behoben)

## 10. Abgleich mit Anforderungen

### 10.1 Abgedeckt

- Kern-Backend für Must-Haves ist implementiert (Auth, Aktionen, Punkte, Ranglisten, Challenges, Dashboard, Admin-Grundfunktionen)
- OpenAPI-nahe Endpunkte sind im Projekt vorhanden
- Persistenz, Security, Fehlerbehandlung und Tests sind vorhanden

### 10.2 Noch nicht voll produktionsreif (fachlich/extern)

- Keycloak ist aktuell nicht als echte externe Laufzeitintegration umgesetzt (derzeit interne JWT-Auth im Backend)
- Erweiterte Non-Functional Anforderungen (z. B. komplexes Caching, hochskalige Performance-Optimierung, vollständige Audit-/Event-Infrastruktur) sind nur teilweise abgedeckt
- Should-Have Stories wurden nicht vollständig in Tiefe umgesetzt

## 11. Build- und Run-Anleitung

### 11.1 Tests

```bash
cd _server
mvn -Dmaven.repo.local=/tmp/ecotrack-m2 test
```

### 11.2 Starten

```bash
cd _server/application
mvn spring-boot:run
```

Default-DB:

- H2 In-Memory (via `application.yml`)

Für PostgreSQL:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER` setzen

## 12. Änderungsumfang

Es wurden Kernartefakte neu erstellt:

- Maven-Struktur (Parent + Modul-POMs)
- 40+ Java-Implementierungsdateien
- Flyway-Migration
- Integrationstests
