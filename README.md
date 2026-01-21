# EcoTrack 🌱

[![CI](https://github.com/[OWNER]/[REPO]/actions/workflows/ci.yml/badge.svg)](https://github.com/[OWNER]/[REPO]/actions/workflows/ci.yml)
[![CD](https://github.com/[OWNER]/[REPO]/actions/workflows/cd.yml/badge.svg)](https://github.com/[OWNER]/[REPO]/actions/workflows/cd.yml)
[![Coverage](https://github.com/[OWNER]/[REPO]/actions/workflows/coverage.yml/badge.svg)](https://github.com/[OWNER]/[REPO]/actions/workflows/coverage.yml)

> ⚠️ **Hinweis:** Ersetze `[OWNER]/[REPO]` mit dem tatsächlichen Repository-Pfad

## Team Aufteilung 

- Backend: Karner, Radlinger, Hänsler (Scrum Master)
 
- Fontend Web: Fellegger & Dalipovic
 
- Frontend App : Kovacs & Grigic 
 

**Gamification-Plattform für nachhaltige Aktionen in Schulen**

EcoTrack ist eine Mono-Repository-Anwendung, die Schüler:innen motiviert, nachhaltige Aktionen durchzuführen und dafür Punkte zu sammeln. Die Plattform bietet ein Leaderboard, Challenges und eine Admin-Oberfläche für Lehrkräfte.

## 📁 Projektstruktur

```
EcoTrack/
├── mobile/                    # React Native Mobile App
├── admin-web/                 # React Admin Dashboard
├── server/                    # Spring Boot Backend API
├── shared-resources/          # Gemeinsame Ressourcen
│   ├── api-contracts/         # OpenAPI Spezifikation
│   ├── design-tokens/         # Design System Tokens
│   └── docs/                  # Projekt-Dokumentation
├── .vscode/                   # VS Code Konfiguration
├── .github/                   # GitHub Actions Workflows
└── *.code-workspace           # Multi-Root Workspaces
```

## 🚀 Quick Start

### Voraussetzungen

- **Node.js** >= 20.x
- **Java** 21 (JDK)
- **Maven** >= 3.9.x
- **Docker** & Docker Compose
- **PostgreSQL** 16 (oder via Docker)

### Development Setup

1. **Repository klonen:**
   ```bash
   git clone <repository-url>
   cd EcoTrack
   ```

2. **Datenbank starten:**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Backend starten:**
   ```bash
   cd server
   ./mvnw spring-boot:run
   ```

4. **Admin-Web starten:**
   ```bash
   cd admin-web
   npm install
   npm run dev
   ```

5. **Mobile App starten:**
   ```bash
   cd mobile
   npm install
   npm start
   ```

## 🔧 VS Code Workspaces

Öffne den passenden Workspace für deine Entwicklungsaufgabe:

| Workspace | Beschreibung |
|-----------|--------------|
| `ecotrack.code-workspace` | Vollständiges Projekt |
| `ecotrack-mobile.code-workspace` | Mobile App Entwicklung |
| `ecotrack-web.code-workspace` | Admin Dashboard Entwicklung |
| `ecotrack-server.code-workspace` | Backend API Entwicklung |

## 🛠️ Technologie-Stack

### Mobile (React Native)
- React Native 0.73
- React Navigation 6.x
- TanStack Query
- AsyncStorage
- TypeScript

### Admin-Web (React)
- React 18.2
- Vite 5.x
- Tailwind CSS 3.4
- React Router 6.x
- TypeScript

### Server (Spring Boot)
- Spring Boot 3.2
- Java 21
- PostgreSQL
- Spring Security + JWT
- Flyway Migrations
- MapStruct

## 📚 Domain Übersicht

| Begriff | Beschreibung |
|---------|--------------|
| **EcoUser** | Fachlicher Endnutzer mit Punktestand |
| **ActionCatalog** | Sammlung vordefinierter Aktionstypen |
| **ActivityEntry** | Einzelne durchgeführte Aktion |
| **PointsLedger** | Protokoll aller Punktebewegungen |
| **Challenge** | Zeitlich begrenzter Wettbewerb |
| **Level** | Gamification-Stufe (Setzling → Altbaum) |

## 🧪 Tests ausführen

```bash
# Backend Tests
cd server && ./mvnw test

# Admin-Web Tests
cd admin-web && npm test

# Mobile Tests
cd mobile && npm test
```

## 📦 Production Build

```bash
# Mit Docker Compose
docker-compose up --build

# Oder einzeln:
cd server && ./mvnw package
cd admin-web && npm run build
```

## 📄 API Dokumentation

- **OpenAPI Spec:** `shared-resources/api-contracts/openapi.yaml`
- **Swagger UI:** http://localhost:8080/api/swagger-ui.html (wenn Server läuft)

## 👥 Team & Rollen

| Rolle | Verantwortung |
|-------|---------------|
| **Schüler** | Aktionen loggen, Punkte sammeln |
| **Lehrer** | Challenges erstellen, Klassen verwalten |
| **Admin** | Benutzerverwaltung, System-Config |

## 📜 Lizenz

Dieses Projekt wurde im Rahmen des SYP-Seminars an der HTL Leoben entwickelt.

---

**HTL Leoben | Schuljahr 2025-2026**
