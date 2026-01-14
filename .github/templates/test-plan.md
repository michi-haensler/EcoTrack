# Test Plan: [Feature/Modul Name]

**Erstellt von**: [Name]  
**Datum**: YYYY-MM-DD  
**Version**: 1.0  
**Status**: [Draft | In Review | Approved | Executed]

---

## Übersicht

### Ziel
[Was wird getestet? Warum?]

### Scope
**In Scope**:
- [Feature 1]
- [Feature 2]

**Out of Scope**:
- [Was nicht getestet wird]

### Bezug zur User Story
- User Story: #[Nummer] - [Titel]
- Bounded Context: [Scoring/Challenge/UserProfile/Administration]

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
**Fokus**: Business Logic, Use Cases, Utils, Components

**Frameworks**:
- Backend: JUnit 5 + Mockito + AssertJ
- Frontend: Vitest + React Testing Library

**Coverage-Ziel**: > 80%

**Was testen**:
- [Business Logic Component 1]
- [Business Logic Component 2]
- [Use Case 1]
- [Use Case 2]

#### Integration Tests (20%)
**Fokus**: REST APIs, Repositories, Event Handlers, Component Trees

**Frameworks**:
- Backend: Spring Boot Test + MockMvc
- Frontend: Vitest + MSW (Mock Service Worker)

**Coverage-Ziel**: > 70%

**Was testen**:
- [REST Endpoint 1]
- [REST Endpoint 2]
- [Repository Query 1]
- [Event Handler 1]

#### E2E Tests (10%)
**Fokus**: Critical User Journeys

**Frameworks**:
- Playwright / Cypress / Detox (React Native)

**Was testen**:
- [User Journey 1: z.B. "Student loggt Aktivität"]
- [User Journey 2: z.B. "Lehrer erstellt Challenge"]

---

## Akzeptanzkriterien → Test Cases

Jedes Akzeptanzkriterium wird als Test Case formuliert.

### AC1: [Beschreibung]

**GIVEN** [Vorbedingung]  
**WHEN** [Aktion]  
**THEN** [Ergebnis]

**Test Case**:
```java
@Test
void should_[expected]_when_[condition]() {
    // Arrange
    
    // Act
    
    // Assert
}
```

**Status**: [ ] Not Started | [ ] In Progress | [ ] Passed | [ ] Failed

---

### AC2: [Beschreibung]

**GIVEN** [Vorbedingung]  
**WHEN** [Aktion]  
**THEN** [Ergebnis]

**Test Case**:
```typescript
it('should_[expected]_when_[condition]', () => {
  // Arrange
  
  // Act
  
  // Assert
});
```

**Status**: [ ] Not Started | [ ] In Progress | [ ] Passed | [ ] Failed

---

## Test Cases

### Backend Tests

#### Unit Tests

| ID | Test Name | Component | Beschreibung | Priority | Status |
|----|-----------|-----------|--------------|----------|--------|
| UT-001 | should_saveActivity_when_validCommand | LogActivityService | Validiert dass Aktivität gespeichert wird | High | ✅ |
| UT-002 | should_throwException_when_actionNotFound | LogActivityService | Error Handling für ungültige Action | High | ✅ |
| UT-003 | should_calculatePoints_when_activityLogged | ActivityEntry | Points Berechnung korrekt | High | 🔄 |
| UT-004 | should_publishEvent_when_activitySaved | LogActivityService | Event Publishing | Medium | ⏳ |

#### Integration Tests

| ID | Test Name | Endpoint | Beschreibung | Priority | Status |
|----|-----------|----------|--------------|----------|--------|
| IT-001 | should_return201_when_activityLogged | POST /api/scoring/activities | Happy Path | High | ✅ |
| IT-002 | should_return404_when_actionNotFound | POST /api/scoring/activities | Error Case | High | ✅ |
| IT-003 | should_return400_when_invalidQuantity | POST /api/scoring/activities | Validation Error | Medium | 🔄 |
| IT-004 | should_returnActivities_when_validUserId | GET /api/scoring/activities?userId={id} | Fetch Liste | Medium | ⏳ |

### Frontend Tests

#### Component Tests

| ID | Test Name | Component | Beschreibung | Priority | Status |
|----|-----------|-----------|--------------|----------|--------|
| CT-001 | should_renderActivityInfo_when_activityProvided | ActivityCard | Rendert Activity Daten | High | ✅ |
| CT-002 | should_callOnSelect_when_cardClicked | ActivityCard | Click Handler | High | ✅ |
| CT-003 | should_displayError_when_apiFails | ActivityList | Error Handling | High | 🔄 |
| CT-004 | should_submitForm_when_validData | CreateActivityForm | Form Submission | High | ⏳ |

#### Hook Tests

| ID | Test Name | Hook | Beschreibung | Priority | Status |
|----|-----------|------|--------------|----------|--------|
| HT-001 | should_loadActivities_when_hookCalled | useActivities | Fetch Activities | High | ✅ |
| HT-002 | should_handleError_when_apiFails | useActivities | Error Handling | High | 🔄 |
| HT-003 | should_createActivity_when_mutationCalled | useCreateActivity | Mutation | High | ⏳ |

### E2E Tests

| ID | Test Name | User Journey | Beschreibung | Priority | Status |
|----|-----------|--------------|--------------|----------|--------|
| E2E-001 | should_logActivity_when_studentCompletes | Student loggt Aktivität | Full Flow: Login → Select Action → Log → Verify | High | ⏳ |
| E2E-002 | should_createChallenge_when_teacherCompletes | Lehrer erstellt Challenge | Full Flow: Login → Create → Activate → Verify | High | ⏳ |

**Legende**:
- ✅ Passed
- 🔄 In Progress
- ⏳ Not Started
- ❌ Failed
- ⚠️ Blocked

---

## Test-Daten

### Test User

| Role | Username | Password | UUID |
|------|----------|----------|------|
| Student | test-student | test123 | `00000000-0000-0000-0000-000000000001` |
| Teacher | test-teacher | test123 | `00000000-0000-0000-0000-000000000002` |
| Admin | test-admin | test123 | `00000000-0000-0000-0000-000000000003` |

### Test Action Definitions

| ID | Name | Points | Category |
|----|------|--------|----------|
| `action-001` | Radfahren 10km | 10 | Transport |
| `action-002` | Öffis nutzen | 5 | Transport |
| `action-003` | Vegetarisch essen | 3 | Ernährung |

### Test Factories

```typescript
// TypeScript
export function createTestActivity(overrides?: Partial<Activity>): Activity {
  return {
    id: '1',
    ecoUserId: 'user-1',
    action: createTestActionDefinition(),
    quantity: 5,
    points: 50,
    loggedAt: new Date('2024-01-15T10:00:00Z'),
    ...overrides,
  };
}
```

```java
// Java
public class TestDataFactory {
    public static LogActivityCommand createValidCommand() {
        return LogActivityCommand.builder()
            .ecoUserId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .actionDefinitionId(UUID.fromString("action-001"))
            .quantity(5)
            .build();
    }
}
```

---

## Test-Umgebung

### Backend
- **Java**: 21
- **Spring Boot**: 3.x
- **Database**: H2 (in-memory) für Tests
- **Profile**: `test`

### Frontend
- **Node**: 20.x
- **Package Manager**: npm
- **Mock API**: MSW (Mock Service Worker)

### CI/CD
- **GitHub Actions**: `.github/workflows/test.yml`
- **Test Execution**: On every PR
- **Coverage Report**: Codecov

---

## Test-Ausführung

### Lokal

```bash
# Backend Unit Tests
cd server
./mvnw test

# Backend Integration Tests
./mvnw verify

# Backend Coverage
./mvnw test jacoco:report
# Report: target/site/jacoco/index.html

# Frontend Tests
cd admin-web
npm run test

# Frontend Coverage
npm run test:coverage
# Report: coverage/index.html

# Frontend E2E
npm run test:e2e
```

### CI/CD

Tests werden automatisch ausgeführt bei:
- Pull Requests
- Push auf `develop` Branch
- Push auf `main` Branch

Pipeline Steps:
1. Checkout Code
2. Setup Environment (Java, Node)
3. Run Unit Tests
4. Run Integration Tests
5. Generate Coverage Report
6. Upload to Codecov
7. (Optional) Run E2E Tests

---

## Coverage-Ziele

### Backend

| Modul | Statements | Branches | Functions | Lines |
|-------|------------|----------|-----------|-------|
| module-scoring | > 80% | > 75% | > 80% | > 80% |
| module-challenge | > 80% | > 75% | > 80% | > 80% |
| module-userprofile | > 70% | > 65% | > 70% | > 70% |
| module-administration | > 60% | > 60% | > 60% | > 60% |

### Frontend

| Projekt | Statements | Branches | Functions | Lines |
|---------|------------|----------|-----------|-------|
| admin-web | > 80% | > 75% | > 80% | > 80% |
| mobile | > 70% | > 65% | > 70% | > 70% |

**Aktuelle Coverage**: [Link zu Codecov Report]

---

## Risiken & Probleme

### Identifizierte Risiken

| ID | Risiko | Wahrscheinlichkeit | Impact | Mitigation |
|----|--------|-------------------|--------|------------|
| R-001 | Flaky Tests durch Timing Issues | Medium | High | Verwendung von `waitFor()`, feste Test-Daten |
| R-002 | Test-Daten Cleanup fehlt | Low | Medium | `@BeforeEach` / `beforeEach()` verwenden |
| R-003 | Coverage-Ziel nicht erreicht | Medium | Medium | Regelmäßige Reviews, Pair Programming |

### Bekannte Probleme

| ID | Problem | Status | Lösung |
|----|---------|--------|--------|
| P-001 | E2E Tests zu langsam | Open | Parallele Ausführung evaluieren |
| P-002 | Mock-Daten inkonsistent | Closed | Test Factories eingeführt |

---

## Deliverables

- [ ] Alle Unit Tests implementiert
- [ ] Alle Integration Tests implementiert
- [ ] Coverage > 80% erreicht
- [ ] Alle Akzeptanzkriterien getestet
- [ ] Test Report generiert
- [ ] Bugs dokumentiert & gefixt
- [ ] Code Review durchgeführt

---

## Approval

| Rolle | Name | Datum | Unterschrift |
|-------|------|-------|--------------|
| Test Lead | [Name] | YYYY-MM-DD | ✅ |
| Developer | [Name] | YYYY-MM-DD | ✅ |
| Product Owner | [Name] | YYYY-MM-DD | ⏳ |

---

## Appendix

### Nützliche Links
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Vitest Documentation](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Testing Instructions](.github/instructions/testing.instructions.md)
