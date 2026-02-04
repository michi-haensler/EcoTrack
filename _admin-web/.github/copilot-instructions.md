# EcoTrack Admin-Web - Coding Standards

## Projektübersicht

EcoTrack Admin-Web ist das Admin-Frontend für Lehrer und Administratoren.

## Technologie-Stack

- React 18
- TypeScript (strict mode)
- Vite
- TanStack Query v5 (Server State)
- React Hook Form + Zod (Forms & Validation)
- React Router v6 (Routing)
- Tailwind CSS + cva (Styling)
- Vitest + React Testing Library (Testing)

## Naming Conventions

- PascalCase: Komponenten, Interfaces, Types
- camelCase: Variablen, Funktionen, Hooks
- UPPER_SNAKE_CASE: Konstanten
- kebab-case: Dateinamen

## Projekt-Struktur

```
src/
├── api/           # API Client, Generated Types
├── components/
│   ├── ui/        # Basis-UI (Button, Input, etc.)
│   └── features/  # Feature-spezifische Components
├── hooks/         # Custom Hooks
├── pages/         # Route-Komponenten
├── routes/        # Router-Konfiguration
├── services/      # Business Logic Services
├── stores/        # Global State (falls nötig)
├── types/         # TypeScript Types
└── utils/         # Helper Functions
```

## Git Commit Messages

Format: `<type>(<scope>): <subject>`

Beispiel: `feat(dashboard): add activity list component`
