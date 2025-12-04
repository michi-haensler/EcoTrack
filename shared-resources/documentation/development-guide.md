# EcoTrack - Development Guide

## Voraussetzungen

### Allgemein
- Git
- VS Code mit empfohlenen Extensions

### Mobile (React Native)
- Node.js 20+
- npm oder yarn
- Xcode (macOS, für iOS)
- Android Studio (für Android)
- React Native CLI

### Admin Web (React)
- Node.js 20+
- npm oder yarn

### Server (Spring Boot)
- JDK 21
- Maven 3.9+
- PostgreSQL 15+
- Docker (optional)

## Quick Start

### 1. Repository klonen

```bash
git clone https://github.com/michi-haensler/EcoTrack.git
cd EcoTrack
```

### 2. Workspace öffnen

```bash
# Full Workspace (alle Projekte)
code ecotrack-full.code-workspace

# Oder einzelne Workspaces
code ecotrack-mobile.code-workspace
code ecotrack-web.code-workspace
code ecotrack-server.code-workspace
```

### 3. Abhängigkeiten installieren

**Server:**
```bash
cd server
mvn clean install -DskipTests
```

**Admin Web:**
```bash
cd admin-web
npm install
```

**Mobile:**
```bash
cd mobile
npm install

# iOS Dependencies (macOS only)
cd ios && pod install && cd ..
```

### 4. Datenbank starten

```bash
# Mit Docker
cd infra
docker-compose up -d postgres

# Oder lokale PostgreSQL Installation verwenden
```

### 5. Anwendungen starten

**Server:**
```bash
cd server
mvn spring-boot:run
# API läuft auf http://localhost:8080
```

**Admin Web:**
```bash
cd admin-web
npm run dev
# Web App läuft auf http://localhost:5173
```

**Mobile:**
```bash
cd mobile
npm start

# In neuem Terminal:
npm run android  # Android Emulator
npm run ios      # iOS Simulator (macOS only)
```

## VS Code Tasks

Anstatt manuelle Befehle nutzen Sie die konfigurierten Tasks:

1. `Ctrl+Shift+P` → "Tasks: Run Task"
2. Task auswählen:
   - `server: mvn verify` - Server bauen und testen
   - `server: run` - Server starten
   - `web: install` - Web Dependencies installieren
   - `web: dev` - Web Dev Server starten
   - `mobile: install` - Mobile Dependencies installieren
   - `mobile: start` - Metro Bundler starten

## Debugging

### Full-Stack Debugging

1. Debug-Panel öffnen (Ctrl+Shift+D)
2. "Full Stack: Web + Server" auswählen
3. F5 drücken

### Einzelne Komponenten

- **Server:** "server: Spring Boot"
- **Web:** "web: Vite dev server"
- **Mobile:** "mobile: React Native" (mit Flipper)

## API-Client Generierung

Nach Änderungen an der OpenAPI-Spezifikation:

```bash
# Web-Client neu generieren
cd admin-web
npm run generate-api

# Mobile-Client neu generieren
cd mobile
npm run generate-api
```

## Code-Qualität

### Linting

```bash
# Web
cd admin-web
npm run lint
npm run lint:fix

# Mobile
cd mobile
npm run lint

# Server (via Checkstyle)
cd server
mvn checkstyle:check
```

### Tests

```bash
# Server
cd server
mvn test

# Web
cd admin-web
npm test

# Mobile
cd mobile
npm test
```

## Git-Workflow

### Branch-Namenskonvention

- `feature/beschreibung` - Neue Features
- `fix/beschreibung` - Bugfixes
- `refactor/beschreibung` - Code-Verbesserungen
- `docs/beschreibung` - Dokumentation

### Commit-Messages

```
<type>(<scope>): <description>

[optional body]
```

**Types:** feat, fix, docs, style, refactor, test, chore
**Scopes:** mobile, web, server, shared, infra

**Beispiele:**
```
feat(mobile): add activity scanning with camera
fix(server): correct points calculation for activities
docs(shared): update API documentation
```

### Pull Request Workflow

1. Feature-Branch erstellen
2. Änderungen committen
3. Pull Request erstellen
4. Code Review
5. Merge nach main

## Troubleshooting

### Server startet nicht
- Prüfen: Läuft PostgreSQL?
- Prüfen: Sind alle Environment-Variablen gesetzt?
- Log-Dateien überprüfen

### Mobile App verbindet nicht mit API
- Prüfen: Läuft der Server?
- Prüfen: Korrekte API-URL in der Konfiguration?
- Bei Android: `adb reverse tcp:8080 tcp:8080`

### Web-Build schlägt fehl
- `node_modules` löschen und `npm install` erneut ausführen
- Node.js Version prüfen (20+)
