# EcoTrack Workspace Setup Guide

## 📁 Übersicht der Workspaces

EcoTrack ist in drei separate Entwicklungsbereiche aufgeteilt. Jedes Team hat seinen eigenen, vollständig konfigurierten VS Code Workspace:

| Workspace | Datei | Team | Technologien |
|-----------|-------|------|--------------|
| 🖥️ Backend | `ecotrack-server.code-workspace` | Backend-Entwickler | Java 21, Spring Boot, Maven |
| 🌐 Frontend Web | `ecotrack-web.code-workspace` | Web-Entwickler | React, TypeScript, Vite |
| 📱 Frontend App | `ecotrack-mobile.code-workspace` | Mobile-Entwickler | React Native, TypeScript |
| 🔧 Full Stack | `ecotrack-full.code-workspace` | Lead-Entwickler | Alle Komponenten |

---

## 🚀 Schnellstart

### 1. Workspace öffnen

```bash
# Backend-Entwickler
code ecotrack-server.code-workspace

# Web-Frontend-Entwickler  
code ecotrack-web.code-workspace

# Mobile-App-Entwickler
code ecotrack-mobile.code-workspace
```

### 2. Empfohlene Extensions installieren

Beim ersten Öffnen des Workspaces erscheint ein Popup:
> "This workspace has extension recommendations. Do you want to install them?"

**→ Klicke auf "Install All"**

Falls das Popup nicht erscheint:
1. `Cmd+Shift+P` (macOS) / `Ctrl+Shift+P` (Windows/Linux)
2. Eingabe: `Extensions: Show Recommended Extensions`
3. Alle Extensions installieren

---

## 🖥️ Backend Workspace (Java/Spring Boot)

### Voraussetzungen
- **Java 21** (empfohlen: [SDKMAN](https://sdkman.io/) oder [Temurin](https://adoptium.net/))
- **Docker Desktop** (für PostgreSQL & Keycloak)
- **Maven** (wird über Maven Wrapper bereitgestellt)

### Erste Schritte

```bash
# 1. Docker-Services starten (PostgreSQL, Keycloak, MailHog)
cd infra && docker-compose up -d

# 2. Dependencies installieren
cd server && ./mvnw clean install -DskipTests

# 3. Anwendung starten
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Wichtige Tasks (Cmd+Shift+B)
| Task | Beschreibung |
|------|-------------|
| 🔨 Build (clean install) | Projekt bauen ohne Tests |
| ✅ Verify (full build) | Vollständiger Build mit Tests |
| 🚀 Run Spring Boot | Anwendung im Dev-Modus starten |
| 🐳 Docker: Start Services | PostgreSQL, Keycloak starten |
| 🐳 Docker: Stop Services | Docker-Container stoppen |

### Enthaltene Extensions
- **Java Development**: Red Hat Java, Java Extension Pack
- **Spring Boot**: Spring Boot Tools, Spring Initializr, Boot Dashboard
- **Testing**: Java Test Runner
- **Database**: SQLTools mit PostgreSQL-Treiber
- **API**: OpenAPI (Swagger), REST Client
- **Code Quality**: SonarLint

### Debug-Konfigurationen
| Konfiguration | Beschreibung |
|---------------|-------------|
| 🚀 EcoTrack Application | Hauptanwendung debuggen |
| 🧪 Debug Current Test | Aktuellen Test debuggen |
| 🔗 Attach to Remote JVM | An laufende JVM (Port 5005) attachen |

---

## 🌐 Frontend Web Workspace (React/TypeScript)

### Voraussetzungen
- **Node.js 20+** (empfohlen: [nvm](https://github.com/nvm-sh/nvm))
- **npm** (wird mit Node.js installiert)

### Erste Schritte

```bash
# 1. Dependencies installieren
cd admin-web && npm install

# 2. Entwicklungsserver starten
npm run dev

# 3. Browser öffnen: http://localhost:5173
```

### Wichtige Tasks (Cmd+Shift+B)
| Task | Beschreibung |
|------|-------------|
| 📦 Install Dependencies | npm install ausführen |
| 🚀 Start Dev Server | Vite Dev-Server starten |
| 🔨 Build Production | Produktions-Build erstellen |
| 🧪 Run Tests | Unit-Tests ausführen |
| 🧪 Run Tests (Watch) | Tests im Watch-Modus |
| 🔍 Lint | ESLint ausführen |
| 🔧 Lint & Fix | ESLint mit Auto-Fix |
| 🔄 Generate API Client | OpenAPI Client generieren |

### Enthaltene Extensions
- **Core**: Prettier, ESLint
- **React**: ES7 React Snippets, Simple React Snippets
- **Styling**: Tailwind CSS IntelliSense
- **Testing**: Vitest Explorer
- **Utilities**: Auto Rename Tag, Path IntelliSense, Color Highlight

### Debug-Konfigurationen
| Konfiguration | Beschreibung |
|---------------|-------------|
| 🌐 Launch Chrome | Chrome mit Dev-Server debuggen |
| 🦊 Launch Firefox | Firefox mit Dev-Server debuggen |
| 🧪 Debug Vitest Tests | Einzelne Tests debuggen |
| 🚀 Dev Server + Chrome | Server starten + Chrome öffnen |

---

## 📱 Frontend App Workspace (React Native)

### Voraussetzungen
- **Node.js 20+**
- **Xcode** (für iOS, nur macOS)
- **Android Studio** (für Android)
- **CocoaPods** (für iOS: `sudo gem install cocoapods`)

### Erste Schritte

```bash
# 1. Dependencies installieren
cd mobile && npm install

# 2. iOS: Pods installieren (nur macOS)
cd ios && pod install && cd ..

# 3. Metro Bundler starten
npm start

# 4. In neuem Terminal: App starten
npm run ios    # oder
npm run android
```

### Wichtige Tasks (Cmd+Shift+B)
| Task | Beschreibung |
|------|-------------|
| 📦 Install Dependencies | npm install ausführen |
| 🚀 Start Metro Bundler | Metro starten |
| 🤖 Run Android | Android-App starten |
| 🍎 Run iOS | iOS-App starten |
| 🧪 Run Tests | Jest-Tests ausführen |
| 🧪 Run Tests (Watch) | Tests im Watch-Modus |
| 🍎 iOS: Install Pods | CocoaPods installieren |
| 🤖 Android: Clean Build | Gradle clean |
| 🔄 Reset Metro Cache | Metro Cache leeren |

### Enthaltene Extensions
- **React Native**: React Native Tools
- **Core**: Prettier, ESLint
- **React**: ES7 React Snippets
- **Testing**: Jest Runner
- **Native**: Kotlin (für Android-Code)

### Debug-Konfigurationen
| Konfiguration | Beschreibung |
|---------------|-------------|
| 🤖 Debug Android | Android-App debuggen |
| 🍎 Debug iOS | iOS-App debuggen |
| 🔗 Attach to Packager | An laufenden Packager attachen |
| 🧪 Debug Jest Tests | Jest-Tests debuggen |
| 🚀 Metro + Android Debug | Metro starten + Android debuggen |
| 🚀 Metro + iOS Debug | Metro starten + iOS debuggen |

---

## 🔧 Gemeinsame Ressourcen

Alle Workspaces haben Zugriff auf:

### 📦 Shared Resources (`shared-resources/`)
- `api-contracts/openapi.yaml` - API-Spezifikation
- `design-tokens/tokens.json` - Design-Tokens
- `keycloak/` - Keycloak-Konfiguration

### 📚 Documentation (`docs/`)
- Architektur-Dokumentation
- ADRs (Architecture Decision Records)
- UI-Mockups

---

## 🎨 VS Code Theme & Icons

Empfohlene Einstellungen für einheitliches Look & Feel:

```json
{
  "workbench.iconTheme": "material-icon-theme",
  "workbench.colorTheme": "One Dark Pro"
}
```

---

## ❓ Häufige Probleme

### Extensions werden nicht installiert
```bash
# Manuell installieren (Beispiel für Java Pack)
code --install-extension vscjava.vscode-java-pack
```

### Java: "JDK not found"
1. JAVA_HOME setzen:
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)
   ```
2. VS Code neu starten

### Node.js: "Node version mismatch"
```bash
# Mit nvm richtige Version aktivieren
nvm use 20
```

### React Native: Metro startet nicht
```bash
# Cache leeren
npm start -- --reset-cache

# Watchman Cache leeren (falls installiert)
watchman watch-del-all
```

---

## 📞 Support

Bei Problemen:
1. Prüfe die [Development Guide](development-guide.md)
2. Erstelle ein Issue im Repository
3. Frage im Team-Chat
