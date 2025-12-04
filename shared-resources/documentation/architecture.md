# EcoTrack - System-Architektur

## Übersicht

EcoTrack ist eine Gamification-Plattform zur Förderung nachhaltiger Aktionen in Schulen. Das System besteht aus drei Hauptkomponenten:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           EcoTrack System                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────────────┐  │
│  │   Mobile     │    │  Admin Web   │    │      Backend API         │  │
│  │  (React      │    │   (React +   │    │   (Spring Boot +         │  │
│  │   Native)    │    │    Vite)     │    │    PostgreSQL)           │  │
│  │              │    │              │    │                          │  │
│  │  iOS/Android │    │   Browser    │    │   REST API + JWT Auth    │  │
│  └──────┬───────┘    └──────┬───────┘    └────────────┬─────────────┘  │
│         │                   │                         │                │
│         └───────────────────┼─────────────────────────┘                │
│                             │                                          │
│                    ┌────────▼────────┐                                 │
│                    │  OpenAPI Spec   │                                 │
│                    │ (Single Source  │                                 │
│                    │   of Truth)     │                                 │
│                    └─────────────────┘                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## Komponenten

### 1. Mobile App (React Native)

**Zweck:** Hauptanwendung für Schüler zur Erfassung nachhaltiger Aktionen

**Funktionen:**
- Aktionen erfassen (Barcode, manuell)
- Punktestand und Fortschritt anzeigen
- Baum-Visualisierung (Gamification)
- Klassenrangliste
- Challenge-Übersicht
- Push-Benachrichtigungen

**Technologie-Stack:**
- React Native (Cross-Platform)
- TypeScript
- React Navigation
- React Query / TanStack Query
- AsyncStorage / MMKV

### 2. Admin Web (React + Vite)

**Zweck:** Web-Dashboard für Lehrer und Administratoren

**Funktionen:**
- Lehrer-Dashboard (Klassenübersicht)
- Challenge-Management
- Nutzerverwaltung (Admin)
- Klasseneinstellungen
- Statistiken und Reports
- Export-Funktionen

**Technologie-Stack:**
- React 18
- TypeScript
- Vite (Build Tool)
- React Router
- TanStack Query
- Tailwind CSS / shadcn/ui

### 3. Backend API (Spring Boot)

**Zweck:** Zentrale Geschäftslogik und Datenhaltung

**Funktionen:**
- RESTful API
- JWT-basierte Authentifizierung
- Domain-Driven Design
- Event-basierte Architektur
- Scheduled Jobs (Rankings)

**Technologie-Stack:**
- Java 21
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway (Migrations)
- MapStruct (DTOs)

## Bounded Contexts (DDD)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        EcoTrack Domain Model                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐                      │
│  │  Identity & Access  │  │   Administration    │                      │
│  │     (Generic)       │  │     (Generic)       │                      │
│  │                     │  │                     │                      │
│  │  - User             │  │  - Class            │                      │
│  │  - Session          │  │  - School           │                      │
│  │  - Role             │  │  - AdminService     │                      │
│  │  - AuthService      │  │                     │                      │
│  └─────────────────────┘  └─────────────────────┘                      │
│                                                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐                      │
│  │   User Profile      │  │  Scoring & Activity │                      │
│  │     (Core)          │  │      (Core)         │                      │
│  │                     │  │                     │                      │
│  │  - EcoUser          │  │  - ActionCatalog    │                      │
│  │  - Name             │  │  - ActivityEntry    │                      │
│  │                     │  │  - PointsLedger     │                      │
│  └─────────────────────┘  └─────────────────────┘                      │
│                                                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐                      │
│  │ Progress/Gamific.   │  │     Leaderboard     │                      │
│  │     (Core)          │  │      (Core)         │                      │
│  │                     │  │                     │                      │
│  │  - Level            │  │  - RankingTable     │                      │
│  │  - Milestone        │  │  - RankingRow       │                      │
│  │  - ProgressSnapshot │  │  - PeriodType       │                      │
│  └─────────────────────┘  └─────────────────────┘                      │
│                                                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐                      │
│  │     Challenges      │  │ Reporting/Dashboard │                      │
│  │     (Core)          │  │   (Supporting)      │                      │
│  │                     │  │                     │                      │
│  │  - Challenge        │  │  - ClassSummary     │                      │
│  │  - ChallengeGoal    │  │  - StudentSummary   │                      │
│  │  - ChallengeProgress│  │  - DashboardService │                      │
│  └─────────────────────┘  └─────────────────────┘                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## Datenfluss

### Aktion erfassen (Mobile App)

```
┌──────────┐    ┌──────────┐    ┌──────────────┐    ┌──────────────┐
│  Mobile  │───▶│   API    │───▶│ ActivityEntry│───▶│ PointsLedger │
│   App    │    │ Endpoint │    │   erstellen  │    │  aktualisieren│
└──────────┘    └──────────┘    └──────────────┘    └──────────────┘
                                        │
                                        ▼
                               ┌──────────────┐
                               │  Domain      │
                               │  Events      │
                               └──────────────┘
                                        │
                      ┌─────────────────┼─────────────────┐
                      ▼                 ▼                 ▼
              ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
              │ Progress     │ │ Challenge    │ │ Ranking      │
              │ aktualisieren│ │ aktualisieren│ │ aktualisieren│
              └──────────────┘ └──────────────┘ └──────────────┘
```

## Deployment-Architektur

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Production Environment                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐                                                       │
│  │  App Store  │  ◄──── Mobile App (React Native)                      │
│  │  Play Store │                                                       │
│  └─────────────┘                                                       │
│                                                                         │
│  ┌─────────────┐                                                       │
│  │   CDN /     │  ◄──── Admin Web (Static Build)                       │
│  │  Vercel     │                                                       │
│  └─────────────┘                                                       │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │   Docker    │───▶│  Spring     │───▶│ PostgreSQL  │                │
│  │  Container  │    │   Boot      │    │  Database   │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## API-First Development

1. OpenAPI-Spezifikation als Single Source of Truth
2. Code-Generierung für alle Clients
3. Konsistente Datenmodelle über alle Plattformen

```bash
# Server-Stubs generieren
cd server && mvn compile

# Web-Client generieren
cd admin-web && npm run generate-api

# Mobile-Client generieren
cd mobile && npm run generate-api
```
