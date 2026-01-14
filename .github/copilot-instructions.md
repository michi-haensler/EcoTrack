# EcoTrack Projekt-Coding-Standards

## Projektübersicht

EcoTrack ist eine Nachhaltigkeits-App für Schulen mit:
- **Backend:** Modularer Monolith (Spring Boot, Java 21) mit DDD & Hexagonal Architecture
- **Admin-Web:** React + TypeScript + Vite
- **Mobile:** React Native

### Architektur-Prinzipien

Das Backend folgt Domain-Driven Design mit klarer Bounded Context-Trennung:

| Modul | Domain-Typ | Architektur |
|-------|-----------|-------------|
| `module-scoring` | Core Domain | Hexagonal Architecture |
| `module-challenge` | Core Domain | Hexagonal Architecture |
| `module-userprofile` | Supporting Domain | CRUD |
| `module-administration` | Generic Domain | ACL (Keycloak) |

**Wichtig:** Module kommunizieren nur über:
- **Modul-Fassaden** (öffentliche API)
- **Domain Events** (Spring ApplicationEventPublisher)

## Allgemeine Coding-Standards

### Naming Conventions
- PascalCase für Klassen, Interfaces, Komponenten
- camelCase für Variablen, Methoden, Funktionen
- UPPER_SNAKE_CASE für Konstanten
- Präfixe für Interfaces: nur bei notwendiger Unterscheidung (z.B. `IUserRepository` nur wenn nötig)
- Keine Abkürzungen außer gängige wie `Id`, `DTO`, `API`

### Kommentare & Dokumentation
- Selbsterklärender Code bevorzugen
- JavaDoc/JSDoc für öffentliche APIs
- Inline-Kommentare nur für komplexe Business-Logik
- TODOs mit Issue-Nummer: `// TODO(#123): ...`

### Error Handling
- Try/catch für async Operationen
- Spezifische Exception-Typen verwenden
- Fehler mit Kontext loggen
- User-freundliche Fehlermeldungen

## Git Workflow

### Branch-Strategie
- `main` – Produktionsreif
- `develop` – Integration Branch
- `feature/<ticket>-<kurzbeschreibung>` – Feature-Branches
- `bugfix/<ticket>-<kurzbeschreibung>` – Bugfix-Branches

### Commit Messages
Format: `<type>(<scope>): <subject>`

Typen:
- `feat` – Neues Feature
- `fix` – Bugfix
- `docs` – Dokumentation
- `refactor` – Code-Umstrukturierung
- `test` – Tests
- `chore` – Build, Dependencies

Beispiel: `feat(scoring): add CO2 calculation for activities`

## Testing

### Test-Pyramide
- Unit Tests: 70%
- Integration Tests: 20%
- E2E Tests: 10%

### Test-Konventionen
- AAA-Pattern (Arrange, Act, Assert)
- Sprechende Test-Namen: `should_calculatePoints_when_activityIsLogged`
- Mocking für externe Abhängigkeiten
- Ein Assert pro Test (idealerweise)
- Tests müssen deterministisch sein

## Code Review

### Checkliste
- [ ] Lesbarkeit: Ist der Code verständlich?
- [ ] SOLID-Prinzipien eingehalten?
- [ ] Error Handling vorhanden?
- [ ] Tests geschrieben?
- [ ] Dokumentation aktualisiert?
- [ ] Keine Secrets im Code?

## Performance

- Lazy Loading wo sinnvoll
- Datenbankabfragen optimieren (N+1 Problem vermeiden)
- Pagination für Listen
- Caching für häufige Abfragen
