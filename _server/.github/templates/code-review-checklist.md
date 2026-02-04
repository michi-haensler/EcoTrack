# Code Review Checklist (Backend)

**PR Number**: #[Nummer]  
**Author**: [Name]  
**Reviewer**: [Name]  
**Date**: YYYY-MM-DD  

---

## Overview

### Beschreibung
[Was ändert dieser PR?]

### Bounded Context
- [ ] Scoring
- [ ] Challenge
- [ ] UserProfile
- [ ] Administration

---

## Review Checkliste

### 1. Architektur

- [ ] **Hexagonal Architecture** (Scoring, Challenge)
  - Domain Layer Framework-frei?
  - Ports & Adapters korrekt?
  - Dependency Rule: nach innen?

- [ ] **SOLID-Prinzipien**
  - Single Responsibility?
  - Dependency Inversion?

- [ ] **DDD-Patterns**
  - Rich Domain Model?
  - Aggregate Roots korrekt?

- [ ] **Modul-Grenzen**
  - Kommunikation nur über Fassaden/Events?
  - Keine direkten Dependencies?

### 2. Code-Qualität

- [ ] **Naming**
  - Sprechende Namen?
  - Conventions eingehalten?

- [ ] **Clean Code**
  - Methoden kurz?
  - Keine Magic Numbers?
  - DRY, KISS?

- [ ] **Documentation**
  - JavaDoc für Public APIs?
  - Kein auskommentierter Code?

### 3. Testing

- [ ] Unit Tests vorhanden?
- [ ] Integration Tests (wenn nötig)?
- [ ] Coverage > 80%?
- [ ] AAA Pattern?
- [ ] Edge/Error Cases?

### 4. Security

- [ ] Input Validation (@Valid, Jakarta)?
- [ ] Endpoints geschützt (@PreAuthorize)?
- [ ] Keine Secrets im Code?
- [ ] SQL Injection verhindert?

### 5. Performance

- [ ] N+1 Queries vermieden?
- [ ] Pagination bei Listen?
- [ ] Lazy Loading korrekt?

### 6. Error Handling

- [ ] Spezifische Exceptions?
- [ ] Logging mit Kontext?
- [ ] GlobalExceptionHandler nutzen?

---

## Entscheidung

- [ ] ✅ Approved
- [ ] 🔄 Approved with minor changes
- [ ] ❌ Changes Requested

**Kommentare**:
[Feedback]
