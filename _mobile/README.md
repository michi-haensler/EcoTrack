# EcoTrack Mobile App

## Übersicht

Die Mobile App für EcoTrack - eine Nachhaltigkeits-App für Schulen.

- **Technologie:** React Native, TypeScript
- **Navigation:** React Navigation
- **State Management:** TanStack Query, Zustand
- **Authentifizierung:** Keycloak (OIDC)

## Features

- 🔐 Schüler-Login & Registrierung
- 📝 Aktivitäten erfassen
- 🏆 Challenges ansehen & teilnehmen
- 📊 Persönliche Statistiken
- 🏅 Achievements & Gamification
- 👤 Profilverwaltung

## Voraussetzungen

- Node.js 20+ (LTS)
- npm 10+
- Xcode (für iOS)
- Android Studio (für Android)
- CocoaPods (für iOS)

## Lokale Entwicklung

### Dependencies installieren

```bash
npm install
```

### iOS Pods installieren

```bash
cd ios && pod install && cd ..
```

### Metro Bundler starten

```bash
npm start
```

### iOS starten

```bash
npm run ios
```

### Android starten

```bash
npm run android
```

### Tests ausführen

```bash
npm test
```

### Linting

```bash
npm run lint
```

### API-Client generieren

```bash
npm run generate-api
```

## Projekt-Struktur

```
mobile/
├── src/
│   ├── api/                    # Generated API Client
│   ├── components/
│   │   ├── ui/                 # Basis UI-Komponenten
│   │   ├── common/             # Wiederverwendbare Komponenten
│   │   └── features/           # Feature-spezifische Komponenten
│   ├── hooks/                  # Custom Hooks
│   ├── navigation/             # Navigation Stack
│   ├── screens/                # Screen-Komponenten
│   ├── services/               # API-Services
│   ├── stores/                 # State Management (Zustand)
│   ├── theme/                  # Design System & Tokens
│   ├── types/                  # TypeScript Types
│   ├── utils/                  # Utility Functions
│   └── App.tsx
├── android/                    # Android Native Code
├── ios/                        # iOS Native Code
├── __tests__/                  # Tests
├── package.json
├── tsconfig.json
├── metro.config.js
├── babel.config.js
└── .env.example
```

## Umgebungsvariablen

Kopiere `.env.example` nach `.env` und passe die Werte an:

```env
API_BASE_URL=http://localhost:8080/api
KEYCLOAK_URL=http://localhost:8081
KEYCLOAK_REALM=ecotrack
KEYCLOAK_CLIENT_ID=ecotrack-mobile
```

## Coding Standards

Siehe [React Native Instructions](../.github/instructions/react-native.instructions.md)
