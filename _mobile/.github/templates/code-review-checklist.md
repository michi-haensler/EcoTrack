# Code Review Checklist (Mobile)

**PR Number**: #[Nummer]  
**Author**: [Name]  
**Reviewer**: [Name]  
**Date**: YYYY-MM-DD  

---

## Overview

### Beschreibung
[Was ändert dieser PR?]

### Komponenten-Typ
- [ ] Screen
- [ ] Component
- [ ] Custom Hook
- [ ] Navigation
- [ ] Service/Utility

---

## Review Checkliste

### 1. React Native

- [ ] **Platform-spezifisch**
  - Platform.select() wo nötig?
  - Platform.OS Checks?

- [ ] **Performance**
  - FlatList für Listen?
  - React.memo für List Items?
  - useCallback/useMemo?
  - removeClippedSubviews?

### 2. Navigation

- [ ] **Typed** (RootStackParamList)?
- [ ] **Deep Linking** konfiguriert?
- [ ] **Stack Flow** korrekt?

### 3. Styling

- [ ] **StyleSheet** statt Inline?
- [ ] **Design Tokens** (Colors, Spacing)?
- [ ] **Responsive** Dimensions?

### 4. State Management

- [ ] **TanStack Query** für Server State?
- [ ] **Pull-to-Refresh**?
- [ ] **Loading States**?
- [ ] **Error Handling**?

### 5. Forms

- [ ] **React Hook Form**?
- [ ] **Zod Schema**?
- [ ] **Controller** für Inputs?
- [ ] **Error Messages**?

### 6. Native Features

- [ ] **Permissions** korrekt?
- [ ] **AsyncStorage** richtig?
- [ ] **Keyboard Handling**?

### 7. TypeScript

- [ ] **Kein `any`**?
- [ ] **Props Interfaces**?
- [ ] **Navigation Types**?

### 8. Testing

- [ ] Tests vorhanden?
- [ ] Navigation gemockt?
- [ ] Coverage > 80%?

---

## Entscheidung

- [ ] ✅ Approved
- [ ] 🔄 Approved with minor changes
- [ ] ❌ Changes Requested

**Kommentare**:
[Feedback]
