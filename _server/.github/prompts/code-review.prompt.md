---
title: "Code Review (Backend)"
category: "Quality Assurance"
description: "Führt ein Code Review für Java/Spring Boot Code durch"
---

# Code Review Prompt (Backend)

Führe ein Code Review durch für die geänderten Java-Dateien.

## Review-Checkliste

### 1. Architektur & Design

- [ ] **SOLID-Prinzipien** eingehalten?
- [ ] **DDD-Patterns** korrekt? (für Core Domains)
  - Domain Layer Framework-frei?
  - Rich Domain Model?
- [ ] **Hexagonal Architecture** eingehalten? (Scoring, Challenge)
  - Domain/Application/Adapter Trennung?
  - Dependency Rule: nach innen?

### 2. Code-Qualität

- [ ] **Naming Conventions**
  - Sprechende Namen?
  - PascalCase, camelCase korrekt?
  
- [ ] **Clean Code**
  - Methoden kurz (< 20 Zeilen)?
  - Keine Magic Numbers?
  - DRY, KISS?

### 3. Testing

- [ ] Unit Tests vorhanden?
- [ ] Coverage > 80%?
- [ ] AAA Pattern?
- [ ] Edge Cases getestet?

### 4. Security

- [ ] Input Validation?
- [ ] Endpoints geschützt?
- [ ] Keine Secrets im Code?

### 5. Performance

- [ ] N+1 Queries vermieden?
- [ ] Pagination bei Listen?
- [ ] Caching wo sinnvoll?

## Review-Output

### ✅ Positive Aspekte
[Was ist gut?]

### ❌ Critical Issues
1. **Problem**: [Beschreibung]
   - **Location**: [File:Line]
   - **Suggestion**: [Lösung]

### ⚠️ Major Issues
[Sollte geändert werden]

### 💡 Minor Suggestions
[Nice-to-have]

### 🎯 Entscheidung
- [ ] ✅ Approved
- [ ] 🔄 Approved with minor changes
- [ ] ❌ Changes Requested
