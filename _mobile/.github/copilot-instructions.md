# EcoTrack Mobile - Coding Standards

## Projektübersicht

EcoTrack Mobile ist die React Native App für Schüler.

## Technologie-Stack

- React Native
- TypeScript (strict mode)
- React Navigation v6
- TanStack Query v5 (Server State)
- React Hook Form + Zod (Forms)
- Jest + React Native Testing Library (Testing)

## Naming Conventions

- PascalCase: Komponenten, Interfaces, Types
- camelCase: Variablen, Funktionen, Hooks
- UPPER_SNAKE_CASE: Konstanten
- kebab-case: Dateinamen

## Projekt-Struktur

```
src/
├── api/           # API Client
├── components/
│   ├── ui/        # Basis-UI (Button, Input, etc.)
│   └── features/  # Feature-spezifische Components
├── hooks/         # Custom Hooks
├── screens/       # Screen-Komponenten
├── navigation/    # React Navigation Setup
├── services/      # Business Logic Services
├── stores/        # Global State
├── theme/         # Colors, Spacing, etc.
├── types/         # TypeScript Types
└── utils/         # Helper Functions
```

## Git Commit Messages

Format: `<type>(<scope>): <subject>`

Beispiel: `feat(home): add activity list screen`
