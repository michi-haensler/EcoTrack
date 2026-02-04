# Test Plan: [Screen/Feature]

**Erstellt von**: [Name]  
**Datum**: YYYY-MM-DD  
**Status**: [Draft | In Review | Approved]

---

## Übersicht

### Ziel
[Was wird getestet?]

### Scope
**In Scope**:
- [Feature 1]

**Out of Scope**:
- [Was nicht getestet wird]

---

## Test-Strategie

### Test-Arten

#### Unit Tests (70%)
**Fokus**: Screens, Components, Hooks

**Frameworks**: Jest + React Native Testing Library

**Coverage-Ziel**: > 80%

#### Integration Tests (20%)
**Fokus**: Navigation, API Integration

**Frameworks**: Jest + MSW

#### E2E Tests (10%)
**Fokus**: Critical User Journeys

**Frameworks**: Detox

---

## Test Cases

### Screen: [Name]

#### Rendering
- [ ] `should_render_when_mounted`
- [ ] `should_displayLoading_when_fetching`
- [ ] `should_displayError_when_apiFails`
- [ ] `should_displayEmpty_when_noData`

#### Interactions
- [ ] `should_refresh_when_pullDown`
- [ ] `should_navigate_when_itemPressed`
- [ ] `should_submit_when_formValid`

### Component: [Name]

- [ ] `should_displayProps_when_rendered`
- [ ] `should_callCallback_when_pressed`

### Hook: [Name]

- [ ] `should_fetchData_when_mounted`
- [ ] `should_handleError_when_apiFails`

---

## Mock Setup

### Navigation
```typescript
const mockNavigation = {
  navigate: jest.fn(),
  goBack: jest.fn(),
};
```

### AsyncStorage
```typescript
jest.mock('@react-native-async-storage/async-storage');
```

---

## Ausführung

```bash
# Unit Tests
npm test

# Watch Mode
npm run test:watch

# Coverage
npm run test:coverage
```

---

## Ergebnisse

| Test-Art | Anzahl | Passed | Failed | Coverage |
|----------|--------|--------|--------|----------|
| Unit     |        |        |        |          |
| Integration |     |        |        |          |
