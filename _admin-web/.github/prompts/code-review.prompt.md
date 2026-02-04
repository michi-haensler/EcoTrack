---
title: "Code Review (Frontend)"
category: "Quality Assurance"
description: "Führt ein Code Review für React/TypeScript Code durch"
---

# Code Review Prompt (Frontend)

Führe ein Code Review durch für die geänderten TypeScript/React-Dateien.

## Review-Checkliste

### 1. TypeScript

- [ ] **Type Safety**
  - Kein `any`?
  - Explizite Return Types?
  - Correct Interfaces/Types?

- [ ] **Nullability**
  - Optional Chaining?
  - Null-Checks?

### 2. React

- [ ] **Component Design**
  - Single Responsibility?
  - Props Interface definiert?
  - Funktionale Komponenten?

- [ ] **Hooks**
  - Dependencies korrekt?
  - Keine Conditional Hooks?
  - Custom Hooks für Wiederverwendung?

- [ ] **Performance**
  - useMemo/useCallback wo nötig?
  - React.memo für reine Components?
  - Keine unnötigen Re-Renders?

### 3. State Management

- [ ] **Server State (TanStack Query)**
  - Query Keys konsistent?
  - Caching richtig konfiguriert?
  - Error/Loading States?

- [ ] **Local State**
  - useState für lokalen State?
  - Kein Props Drilling?

### 4. Forms

- [ ] **Validation**
  - Zod Schema vorhanden?
  - Error Messages angezeigt?
  - React Hook Form korrekt?

### 5. Styling

- [ ] Tailwind Utilities?
- [ ] cva für Varianten?
- [ ] cn() für Conditional Classes?

### 6. Testing

- [ ] Tests vorhanden?
- [ ] Coverage > 80%?
- [ ] User Interactions getestet?

### 7. Accessibility

- [ ] Semantic HTML?
- [ ] ARIA Labels?
- [ ] Keyboard Navigation?

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
