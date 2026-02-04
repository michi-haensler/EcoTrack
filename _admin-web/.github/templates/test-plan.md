# Test Plan: [Feature/Komponente]

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

### Test-Pyramide

```
     /\
    /E2E\ 10%
   /____\
  /      \
 /Integr.\ 20%
/__________\
/    Unit   \ 70%
/____________\
```

### Test-Arten

#### Unit Tests (70%)
**Fokus**: Components, Hooks, Utils

**Frameworks**: Vitest + React Testing Library

**Coverage-Ziel**: > 80%

#### Integration Tests (20%)
**Fokus**: API Integration, Component Trees

**Frameworks**: Vitest + MSW

#### E2E Tests (10%)
**Fokus**: Critical User Journeys

**Frameworks**: Playwright / Cypress

---

## Test Cases

### Component: [Name]

#### Rendering
- [ ] `should_render_when_propsProvided`
- [ ] `should_displayLoading_when_isLoading`
- [ ] `should_displayError_when_hasError`

#### Interactions
- [ ] `should_callOnClick_when_buttonClicked`
- [ ] `should_submitForm_when_valid`
- [ ] `should_showError_when_invalid`

### Hook: [Name]

- [ ] `should_fetchData_when_mounted`
- [ ] `should_handleError_when_apiFails`
- [ ] `should_refetch_when_keyChanges`

---

## Ausführung

```bash
# Unit Tests
npm test

# Watch Mode
npm run test:watch

# Coverage Report
npm run test:coverage
```

---

## Ergebnisse

| Test-Art | Anzahl | Passed | Failed | Coverage |
|----------|--------|--------|--------|----------|
| Unit     |        |        |        |          |
| Integration |     |        |        |          |
