---
title: "Code Review (Mobile)"
category: "Quality Assurance"
description: "Führt ein Code Review für React Native Code durch"
---

# Code Review Prompt (Mobile)

Führe ein Code Review durch für die geänderten React Native-Dateien.

## Review-Checkliste

### 1. React Native Basics

- [ ] **Platform-spezifischer Code**
  - Platform.select() wo nötig?
  - Platform.OS Checks?

- [ ] **Performance**
  - FlatList für Listen?
  - React.memo für List Items?
  - useCallback für Callbacks?

### 2. Navigation

- [ ] **Typed Navigation**
  - Navigation Props typisiert?
  - Route Params typisiert?

- [ ] **Navigation Flow**
  - Stack korrekt?
  - Deep Linking?

### 3. Styling

- [ ] **StyleSheet** statt Inline?
- [ ] **Design Tokens** (Colors, Spacing)?
- [ ] **Responsive** (Dimensions)?

### 4. State Management

- [ ] **TanStack Query** für Server State?
- [ ] **Query Keys** konsistent?
- [ ] **Loading/Error States**?
- [ ] **Pull-to-Refresh**?

### 5. Forms

- [ ] **React Hook Form**?
- [ ] **Zod Schema**?
- [ ] **Error Messages**?

### 6. Native Features

- [ ] **Permissions** korrekt angefragt?
- [ ] **AsyncStorage** für lokale Daten?
- [ ] **Platform APIs** korrekt?

### 7. Testing

- [ ] Tests vorhanden?
- [ ] Navigation gemockt?
- [ ] Coverage > 80%?

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
