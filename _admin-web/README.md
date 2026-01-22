# EcoTrack Admin-Web (Frontend)

## Übersicht

Das Admin-Web-Interface für EcoTrack - eine Nachhaltigkeits-App für Schulen.

- **Technologie:** React 18, TypeScript, Vite
- **UI-Framework:** TailwindCSS, shadcn/ui
- **State Management:** TanStack Query (React Query)
- **Authentifizierung:** Keycloak (OIDC)

## Features

- 👥 Benutzerverwaltung (Lehrer/Admins)
- 🏫 Schulverwaltung
- 🏆 Challenge-Management
- 📊 Statistiken & Auswertungen
- ⚙️ Systemkonfiguration

## Voraussetzungen

- Node.js 20+ (LTS)
- npm 10+

## Lokale Entwicklung

### Dependencies installieren

```bash
npm install
```

### Development Server starten

```bash
npm run dev
```

Die App ist dann unter http://localhost:5173 verfügbar.

### Build erstellen

```bash
npm run build
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
admin-web/
├── src/
│   ├── api/                    # Generated API Client
│   ├── components/
│   │   ├── ui/                 # Basis UI-Komponenten (shadcn)
│   │   ├── common/             # Wiederverwendbare Komponenten
│   │   └── features/           # Feature-spezifische Komponenten
│   ├── hooks/                  # Custom Hooks
│   ├── pages/                  # Seiten-Komponenten
│   ├── routes/                 # Routing-Konfiguration
│   ├── services/               # API-Services
│   ├── stores/                 # State Management
│   ├── types/                  # TypeScript Types
│   ├── utils/                  # Utility Functions
│   ├── App.tsx
│   └── main.tsx
├── public/                     # Static Assets
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
└── .env.example
```

## Umgebungsvariablen

Kopiere `.env.example` nach `.env.local` und passe die Werte an:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_KEYCLOAK_URL=http://localhost:8081
VITE_KEYCLOAK_REALM=ecotrack
VITE_KEYCLOAK_CLIENT_ID=ecotrack-admin-web
```

## Coding Standards

Siehe [TypeScript & React Instructions](../.github/instructions/typescript-react.instructions.md)
