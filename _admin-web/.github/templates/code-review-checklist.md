# Code Review Checklist (Frontend)

**PR Number**: #[Nummer]  
**Author**: [Name]  
**Reviewer**: [Name]  
**Date**: YYYY-MM-DD  

---

## Overview

### Beschreibung
[Was ändert dieser PR?]

### Komponenten-Typ
- [ ] UI Component (Button, Input, etc.)
- [ ] Feature Component (ActivityCard, etc.)
- [ ] Page Component
- [ ] Custom Hook
- [ ] Utility Function

---

## Review Checkliste

### 1. TypeScript

- [ ] **Kein `any`**
- [ ] **Explizite Types** für Props, Return Values
- [ ] **Interfaces** für Objektstrukturen
- [ ] **Type Guards** wo nötig

### 2. React

- [ ] **Funktionale Komponenten**
- [ ] **Props Interface** definiert
- [ ] **Hooks korrekt** (Dependencies, keine Conditionals)
- [ ] **Performance** (useMemo, useCallback, memo)

### 3. State Management

- [ ] **TanStack Query** für Server State
- [ ] **Query Keys** konsistent
- [ ] **Loading/Error States** behandelt
- [ ] **Optimistic Updates** wo sinnvoll

### 4. Forms

- [ ] **React Hook Form** verwendet
- [ ] **Zod Schema** für Validation
- [ ] **Error Messages** angezeigt
- [ ] **Accessible** (Labels, Hints)

### 5. Styling

- [ ] **Tailwind** Utility Classes
- [ ] **cva** für Varianten
- [ ] **cn()** für Conditional Classes
- [ ] **Design Tokens** verwendet

### 6. Testing

- [ ] Tests vorhanden?
- [ ] Coverage > 80%?
- [ ] Rendering Tests?
- [ ] Interaction Tests?
- [ ] Hook Tests?

### 7. Accessibility

- [ ] Semantic HTML?
- [ ] ARIA Labels/Roles?
- [ ] Keyboard Navigation?
- [ ] Focus Management?

### 8. Code Quality

- [ ] **Naming** (PascalCase, camelCase)?
- [ ] **Clean Code** (DRY, KISS)?
- [ ] **JSDoc** für komplexe Funktionen?
- [ ] **Keine console.log**?

---

## Entscheidung

- [ ] ✅ Approved
- [ ] 🔄 Approved with minor changes
- [ ] ❌ Changes Requested

**Kommentare**:
[Feedback]
