# Code Review Checklist: [Feature/PR Name]

**PR Number**: #[Nummer]  
**Author**: [Name]  
**Reviewer**: [Name]  
**Date**: YYYY-MM-DD  
**Status**: [In Review | Approved | Changes Requested]

---

## Overview

### Beschreibung
[Was ändert dieser PR? Welches Feature/Bug wird implementiert/gefixt?]

### Related Issues
- Closes: #[Issue-Nummer]
- Related: #[Issue-Nummer]

### Bounded Context
- [ ] Scoring
- [ ] Challenge
- [ ] UserProfile
- [ ] Administration

### Geänderte Dateien
- Backend: [Anzahl] Files
- Frontend: [Anzahl] Files
- Tests: [Anzahl] Files
- Documentation: [Anzahl] Files

---

## Review Checkliste

### 1. Funktionalität

- [ ] **Code erfüllt Akzeptanzkriterien**
  - Alle ACs aus User Story getestet
  - Edge Cases berücksichtigt
  - Error Cases behandelt

- [ ] **Features funktionieren wie erwartet**
  - Happy Path funktioniert
  - Validierungen greifen
  - Error Handling korrekt

- [ ] **Keine Regression**
  - Bestehende Features funktionieren noch
  - Keine Breaking Changes (oder dokumentiert)
  - Backward Compatibility sichergestellt

- [ ] **Integration mit anderen Modulen**
  - Module Boundaries respektiert
  - Events korrekt published/consumed
  - Keine direkten Dependencies zwischen Modulen

---

### 2. Code-Qualität

#### Architecture & Design

- [ ] **SOLID-Prinzipien eingehalten**
  - Single Responsibility: Klassen haben klaren Fokus
  - Open/Closed: Erweiterbar ohne Änderung
  - Liskov Substitution: Subtypes austauschbar
  - Interface Segregation: Kleine, fokussierte Interfaces
  - Dependency Inversion: Abhängigkeiten zu Abstraktionen

- [ ] **DDD-Patterns korrekt angewendet** (Core Domains)
  - Domain Layer Framework-frei
  - Rich Domain Model (Business Logic in Entities)
  - Aggregate Roots korrekt verwendet
  - Value Objects immutable

- [ ] **Hexagonal Architecture** (Scoring, Challenge)
  - Domain/Application/Adapter Trennung
  - Ports & Adapters korrekt
  - Dependency Rule: nach innen

- [ ] **Clean Code**
  - Sprechende Namen (Klassen, Methoden, Variablen)
  - Funktionen/Methoden kurz (< 20 Zeilen ideal)
  - Keine Magic Numbers (Konstanten verwenden)
  - DRY (Don't Repeat Yourself)
  - KISS (Keep It Simple, Stupid)

#### Code Style

- [ ] **Naming Conventions**
  - PascalCase: Klassen, Interfaces, Components
  - camelCase: Methoden, Variablen, Funktionen
  - UPPER_SNAKE_CASE: Konstanten
  - Keine unklaren Abkürzungen

- [ ] **Formatting**
  - Konsistente Einrückung
  - Keine Trailing Whitespace
  - Max Line Length eingehalten (120 Zeichen)
  - Imports sortiert

- [ ] **Comments & Documentation**
  - Keine auskommentierter Code
  - JavaDoc/JSDoc für öffentliche APIs
  - Inline-Kommentare nur für komplexe Logik
  - TODOs mit Issue-Nummer

#### Backend-Spezifisch (Java/Spring Boot)

- [ ] **Dependency Injection**
  - Constructor Injection (nicht Field)
  - `@RequiredArgsConstructor` von Lombok
  - Interfaces für Services (wenn sinnvoll)

- [ ] **Transaction Management**
  - `@Transactional` in Application Layer
  - Propagation korrekt (REQUIRES_NEW für Events)
  - Isolation Level angemessen

- [ ] **Error Handling**
  - Spezifische Exception Types
  - Global Exception Handler
  - Logging mit Kontext

- [ ] **JPA Best Practices**
  - Lazy Loading default
  - N+1 Queries vermieden
  - Indexes auf häufige Queries
  - Explizite Schema-Namen

#### Frontend-Spezifisch (React/TypeScript)

- [ ] **Type Safety**
  - Keine `any` (außer unavoidable)
  - Explizite Return Types
  - Type Guards für Runtime Checks

- [ ] **React Best Practices**
  - Funktionale Komponenten + Hooks
  - Props Interface definiert
  - Memoization wo sinnvoll (`useMemo`, `useCallback`, `React.memo`)
  - Key Props bei Listen

- [ ] **State Management**
  - TanStack Query für Server State
  - `useState` für lokalen State
  - Keine unnötigen Re-Renders

- [ ] **Performance**
  - Lazy Loading von Komponenten
  - Virtual Lists für lange Listen (FlatList in RN)
  - Optimized Re-Renders

---

### 3. Testing

- [ ] **Unit Tests vorhanden**
  - Use Cases getestet
  - Business Logic getestet
  - Components getestet
  - Coverage > 80%

- [ ] **Integration Tests vorhanden**
  - REST Endpoints getestet
  - Repositories getestet
  - Event Handlers getestet

- [ ] **Test-Qualität**
  - AAA Pattern (Arrange-Act-Assert)
  - Sprechende Namen (`should_X_when_Y`)
  - Ein Konzept pro Test
  - Kein Flaky Tests

- [ ] **Edge Cases getestet**
  - Null/Undefined Handling
  - Empty Lists
  - Boundary Values
  - Concurrent Access

- [ ] **Error Cases getestet**
  - Invalid Input
  - Not Found Cases
  - Authorization Failures

- [ ] **Test Coverage**
  - Statements > 80%
  - Branches > 75%
  - Functions > 80%
  - Coverage Report grün

---

### 4. Security

- [ ] **Input Validation**
  - Jakarta Validation Annotations (Backend)
  - Zod Schemas (Frontend)
  - SQL Injection verhindert (Parameterized Queries)
  - XSS verhindert (React escaped automatisch)

- [ ] **Authentication & Authorization**
  - Endpoints geschützt (`@PreAuthorize`)
  - Rollen korrekt geprüft (STUDENT, TEACHER, ADMIN)
  - Token Validation

- [ ] **Sensitive Data**
  - Keine Secrets im Code
  - Environment Variables verwendet
  - Keine Passwords/Tokens geloggt
  - PII (Personally Identifiable Information) geschützt

- [ ] **Dependencies**
  - Keine bekannten CVEs (Snyk, Dependabot)
  - Dependencies aktuell
  - Keine deprecated Libraries

---

### 5. Performance

- [ ] **Database**
  - Indexes auf häufige Queries
  - N+1 Queries vermieden
  - Pagination bei Listen (> 20 Items)
  - Connection Pooling konfiguriert

- [ ] **API**
  - Response Times akzeptabel (< 200ms für einfache Requests)
  - Caching wo sinnvoll
  - Keine synchronen Blocking Calls

- [ ] **Frontend**
  - Bundle Size akzeptabel
  - Lazy Loading verwendet
  - Images optimiert
  - Keine Memory Leaks

---

### 6. Documentation

- [ ] **Code Dokumentation**
  - Public APIs dokumentiert (JavaDoc/JSDoc)
  - Komplexe Business Logic erklärt
  - ADRs für Architekturentscheidungen

- [ ] **README**
  - Setup Instructions aktuell
  - Dependencies dokumentiert
  - Build/Run Commands

- [ ] **API Documentation**
  - Endpoints dokumentiert (OpenAPI/Swagger optional)
  - Request/Response Beispiele
  - Error Codes erklärt

- [ ] **Changelog**
  - CHANGELOG.md aktualisiert (bei größeren Changes)
  - Migration Guide (bei Breaking Changes)

---

### 7. Git & CI/CD

- [ ] **Commits**
  - Commit Messages sinnvoll (`<type>(<scope>): <subject>`)
  - Logische Commit-Struktur
  - Keine Merge-Konflikte

- [ ] **Branch**
  - Branch-Name korrekt (`feature/<ticket>-<description>`)
  - Von `develop` abgezweigt
  - Aktuell mit `develop` (rebase)

- [ ] **CI/CD Pipeline**
  - Alle Tests grün ✅
  - Build erfolgreich ✅
  - Linter/Formatter grün ✅
  - Coverage akzeptabel ✅

---

## Feedback

### Positive Aspekte
- ✅ [Was ist gut gelaufen?]
- ✅ [Besonders gute Lösung]

### Verbesserungsvorschläge

#### Critical (Muss geändert werden)
- ❌ [Problem 1]
  - **Location**: [File:Line]
  - **Issue**: [Beschreibung]
  - **Suggestion**: [Lösungsvorschlag]

#### Major (Sollte geändert werden)
- ⚠️ [Problem 2]
  - **Location**: [File:Line]
  - **Issue**: [Beschreibung]
  - **Suggestion**: [Lösungsvorschlag]

#### Minor (Nice-to-have)
- 💡 [Vorschlag 1]
  - **Location**: [File:Line]
  - **Suggestion**: [Lösungsvorschlag]

### Fragen
- ❓ [Frage 1]
- ❓ [Frage 2]

---

## Entscheidung

- [ ] ✅ **Approved**: Kann gemerged werden
- [ ] 🔄 **Approved with minor changes**: Kleine Änderungen in Follow-up
- [ ] ❌ **Changes Requested**: Muss überarbeitet werden

### Nächste Schritte
- [ ] [Aktion 1]
- [ ] [Aktion 2]

---

## Reviewer Notes

[Zusätzliche Notizen, Kontext, Diskussionen]

---

**Signature**: [Reviewer Name] - [Date]
