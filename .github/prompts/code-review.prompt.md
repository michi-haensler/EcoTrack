---
title: "Code Review"
category: "Quality Assurance"
description: "Führt ein umfassendes Code Review durch und prüft auf Code-Qualität, Architektur, Security, Performance und Testing"
intent: "Systematisches Review von Code-Änderungen vor dem Merge"
context: "Pull Requests, Feature-Branches, Code-Änderungen"
variables:
  - name: "file"
    description: "Datei oder Verzeichnis das reviewed werden soll"
    required: false
  - name: "focus"
    description: "Fokus-Bereich: architecture, security, performance, testing, all (default: all)"
    required: false
    default: "all"
---

# Code Review Prompt

Führe ein Code Review durch für: ${file ? `\`${file}\`` : "die geänderten Dateien im aktuellen Branch"}

## Review-Fokus

${focus === "all" ? "Vollständiges Review aller Aspekte" : `Fokussiert auf: **${focus}**`}

## Review-Checkliste

### 1. Code-Qualität & Clean Code

#### Architektur & Design
- [ ] **SOLID-Prinzipien** eingehalten?
  - Single Responsibility
  - Open/Closed
  - Liskov Substitution
  - Interface Segregation
  - Dependency Inversion

- [ ] **DDD-Patterns** korrekt? (für Core Domains)
  - Domain Layer Framework-frei?
  - Rich Domain Model (Business Logic in Entities)?
  - Aggregate Roots korrekt?

- [ ] **Hexagonal Architecture** eingehalten? (Scoring, Challenge)
  - Domain/Application/Adapter Trennung?
  - Dependency Rule: nach innen?
  - Ports & Adapters korrekt?

#### Code Style
- [ ] **Naming Conventions**
  - Sprechende Namen (Klassen, Methoden, Variablen)?
  - PascalCase, camelCase, UPPER_SNAKE_CASE korrekt?
  - Keine unklaren Abkürzungen?

- [ ] **Clean Code**
  - Funktionen/Methoden kurz (< 20 Zeilen)?
  - Keine Magic Numbers (Konstanten)?
  - DRY (Don't Repeat Yourself)?
  - KISS (Keep It Simple)?
  - Early Returns statt Nested Ifs?

- [ ] **Comments & Documentation**
  - Kein auskommentierter Code?
  - JavaDoc/JSDoc für Public APIs?
  - Inline-Kommentare nur für komplexe Logik?

### 2. Testing

- [ ] **Test Coverage**
  - Unit Tests vorhanden?
  - Integration Tests (wenn nötig)?
  - Coverage > 80%?

- [ ] **Test-Qualität**
  - AAA Pattern (Arrange-Act-Assert)?
  - Sprechende Namen (`should_X_when_Y`)?
  - Edge Cases getestet?
  - Error Cases getestet?

### 3. Security

- [ ] **Input Validation**
  - Jakarta Validation / Zod Schemas?
  - SQL Injection verhindert?
  - XSS verhindert?

- [ ] **Authentication & Authorization**
  - Endpoints geschützt?
  - Rollen korrekt geprüft?

- [ ] **Sensitive Data**
  - Keine Secrets im Code?
  - Environment Variables verwendet?
  - Keine Passwords/Tokens geloggt?

### 4. Performance

- [ ] **Database**
  - N+1 Queries vermieden?
  - Indexes auf häufige Queries?
  - Pagination bei Listen?

- [ ] **API**
  - Response Times akzeptabel?
  - Caching wo sinnvoll?

- [ ] **Frontend**
  - Bundle Size OK?
  - Lazy Loading verwendet?
  - Keine Memory Leaks?

### 5. Error Handling

- [ ] **Exception Handling**
  - Try/Catch für async Operationen?
  - Spezifische Exception Types?
  - Logging mit Kontext?

- [ ] **User-Friendly Errors**
  - Verständliche Fehlermeldungen?
  - Error Codes konsistent?

## Review-Output Format

Strukturiere dein Review wie folgt:

### ✅ Positive Aspekte
[Was ist gut gelaufen? Besonders gute Lösungen?]

### ❌ Critical Issues (Muss geändert werden)
1. **Problem**: [Beschreibung]
   - **Location**: [File:Line]
   - **Issue**: [Was ist falsch?]
   - **Impact**: [Warum ist das critical?]
   - **Suggestion**: [Wie fixen?]

### ⚠️ Major Issues (Sollte geändert werden)
1. **Problem**: [Beschreibung]
   - **Location**: [File:Line]
   - **Issue**: [Was könnte besser sein?]
   - **Suggestion**: [Verbesserungsvorschlag]

### 💡 Minor Suggestions (Nice-to-have)
1. **Suggestion**: [Vorschlag]
   - **Location**: [File:Line]
   - **Benefit**: [Warum wäre das besser?]

### ❓ Questions
1. [Frage zu Implementierung/Entscheidung]

### 📊 Metrics
- Lines Changed: [+X -Y]
- Test Coverage: [X%]
- Complexity: [Hoch/Mittel/Niedrig]

### 🎯 Entscheidung
- [ ] ✅ Approved: Kann gemerged werden
- [ ] 🔄 Approved with minor changes: Kleine Änderungen in Follow-up
- [ ] ❌ Changes Requested: Muss überarbeitet werden

## Beispiel-Review

### ✅ Positive Aspekte
- Klare Trennung von Domain und Application Layer
- Gute Test Coverage (85%)
- Sprechende Variablen-Namen

### ❌ Critical Issues
1. **Problem**: Business Logic im REST Controller
   - **Location**: `ScoringController.java:45-60`
   - **Issue**: Points-Berechnung direkt im Controller statt im Use Case
   - **Impact**: Verletzt Clean Architecture, nicht testbar ohne Spring
   - **Suggestion**: Logic in `LogActivityService` verschieben

### ⚠️ Major Issues
1. **Problem**: N+1 Query Problem
   - **Location**: `ActivityEntryRepository.java:25`
   - **Issue**: `findByUserId()` lädt Activities ohne Actions, dann N Queries für Actions
   - **Suggestion**: `@EntityGraph` oder JOIN FETCH verwenden

### 💡 Minor Suggestions
1. **Suggestion**: Magic Number extrahieren
   - **Location**: `ActivityEntry.java:30`
   - **Code**: `if (quantity > 100)`
   - **Benefit**: Konstante `MAX_QUANTITY = 100` ist selbsterklärender

### ❓ Questions
1. Warum `@Transactional(propagation = REQUIRES_NEW)` statt default?
2. Ist der Event-Handler idempotent? Was bei Duplicate Events?

### 📊 Metrics
- Lines Changed: +320 -45
- Test Coverage: 85%
- Complexity: Mittel

### 🎯 Entscheidung
- [ ] ✅ Approved
- [x] 🔄 Approved with minor changes
- [ ] ❌ Changes Requested

**Nächste Schritte**:
1. Business Logic aus Controller extrahieren (Critical)
2. N+1 Query fixen (Major)
3. Magic Numbers als Follow-up (Minor)

---

Starte jetzt das Code Review!
