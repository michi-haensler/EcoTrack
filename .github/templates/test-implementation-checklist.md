# Test Engineer Agent - Implementierungs-Checkliste

## 🎯 Zweck

Diese Checkliste hilft dem Test Engineer Agent, systematisch Tests für alle EcoTrack-Komponenten zu erstellen.

---

## 📋 Backend (Java/Spring Boot) Test-Checkliste

### Unit Tests (70%)

#### Domain Layer Tests
- [ ] `ActivityEntry` Entity Tests
  - [ ] `should_create_when_validData`
  - [ ] `should_throwException_when_negativeQuantity`
  - [ ] `should_calculatePoints_when_actionProvided`
  
- [ ] `Challenge` Entity Tests
  - [ ] `should_create_when_validDates`
  - [ ] `should_throwException_when_endBeforeStart`
  - [ ] `should_beActive_when_withinDateRange`

- [ ] Value Objects Tests
  - [ ] `EcoUserId`, `ActivityEntryId`, etc.
  - [ ] `should_equal_when_sameValue`
  - [ ] `should_notEqual_when_differentValue`

#### Application Layer Tests
- [ ] `LogActivityService` Tests
  - [ ] `should_saveActivity_when_validCommand`
  - [ ] `should_throwException_when_actionNotFound`
  - [ ] `should_publishEvent_when_activityLogged`
  
- [ ] `GetLeaderboardService` Tests
  - [ ] `should_returnSortedList_when_called`
  - [ ] `should_limitResults_when_paginated`

- [ ] Mapper Tests
  - [ ] `should_mapToDto_when_entityProvided`
  - [ ] `should_mapToEntity_when_commandProvided`

### Integration Tests (20%)

#### Controller Tests (MockMvc)
- [ ] `ScoringController` Tests
  - [ ] `should_return201_when_activityLogged`
  - [ ] `should_return400_when_invalidInput`
  - [ ] `should_return404_when_resourceNotFound`
  - [ ] `should_return401_when_unauthorized`

- [ ] `ChallengeController` Tests
  - [ ] `should_return200_when_challengesListed`
  - [ ] `should_return201_when_challengeCreated`

#### Repository Tests (@DataJpaTest)
- [ ] `ActivityEntryRepository` Tests
  - [ ] `should_save_when_validEntity`
  - [ ] `should_findByUserId_when_exists`
  - [ ] `should_returnEmpty_when_notExists`

- [ ] `ChallengeRepository` Tests
  - [ ] `should_findActive_when_withinDateRange`

### E2E Tests (10%)
- [ ] Critical User Journey: Aktivität loggen
- [ ] Critical User Journey: Challenge beitreten

---

## 📋 Admin-Web (React/TypeScript) Test-Checkliste

### Unit Tests (70%)

#### Component Tests
- [ ] `UserCard` Tests
  - [ ] `should_renderUserInfo_when_provided`
  - [ ] `should_callOnSelect_when_clicked`
  
- [ ] `ActivityCard` Tests
  - [ ] `should_displayActivity_when_provided`
  - [ ] `should_formatDate_when_rendered`

- [ ] `ChallengeForm` Tests
  - [ ] `should_validate_when_submitted`
  - [ ] `should_showErrors_when_invalid`

#### Hook Tests
- [ ] `useActivities` Tests
  - [ ] `should_fetchData_when_mounted`
  - [ ] `should_handleError_when_apiFails`
  
- [ ] `useCreateActivity` Tests
  - [ ] `should_mutate_when_called`
  - [ ] `should_invalidateQueries_when_success`

#### Utility Tests
- [ ] Date Formatters
- [ ] Validators
- [ ] API Client

### Integration Tests (20%)

#### Page Tests
- [ ] `DashboardPage` Tests
  - [ ] `should_loadData_when_mounted`
  - [ ] `should_displayStats_when_loaded`

- [ ] `UserManagementPage` Tests
  - [ ] `should_listUsers_when_loaded`
  - [ ] `should_filterUsers_when_searchTermProvided`

#### API Integration (MSW)
- [ ] API Client Tests mit Mock Service Worker
- [ ] Error Handling Tests

---

## 📋 Mobile (React Native) Test-Checkliste

### Unit Tests (70%)

#### Component Tests
- [ ] `ActivityListItem` Tests
  - [ ] `should_render_when_activityProvided`
  - [ ] `should_navigate_when_pressed`

- [ ] `PointsBadge` Tests
  - [ ] `should_displayPoints_when_provided`
  - [ ] `should_showLevel_when_thresholdReached`

#### Hook Tests
- [ ] `useAuth` Tests
- [ ] `useActivities` Tests
- [ ] `useLeaderboard` Tests

#### Navigation Tests
- [ ] Screen Navigation
- [ ] Deep Linking

### Integration Tests (20%)

#### Screen Tests
- [ ] `HomeScreen` Tests
- [ ] `ProfileScreen` Tests
- [ ] `LeaderboardScreen` Tests

---

## 🔧 Test-Dateien erstellen

### Backend Verzeichnisstruktur

```
server/
├── src/
│   └── test/
│       └── java/
│           └── com/ecotrack/
│               ├── scoring/
│               │   ├── domain/
│               │   │   └── ActivityEntryTest.java
│               │   ├── application/
│               │   │   └── LogActivityServiceTest.java
│               │   └── adapter/
│               │       ├── in/rest/
│               │       │   └── ScoringControllerTest.java
│               │       └── out/persistence/
│               │           └── ActivityEntryRepositoryTest.java
│               └── challenge/
│                   └── ...
└── pom.xml (mit Test-Dependencies)
```

### Frontend Test-Dateien

```
admin-web/
├── src/
│   ├── components/
│   │   └── __tests__/
│   │       └── UserCard.test.tsx
│   ├── hooks/
│   │   └── __tests__/
│   │       └── useActivities.test.ts
│   └── pages/
│       └── __tests__/
│           └── Dashboard.test.tsx
└── vitest.config.ts

mobile/
├── src/
│   ├── components/
│   │   └── __tests__/
│   │       └── ActivityListItem.test.tsx
│   └── screens/
│       └── __tests__/
│           └── HomeScreen.test.tsx
└── jest.config.js
```

---

## 🚀 Ausführung

### Backend Tests
```bash
cd server
./mvnw test                    # Alle Tests
./mvnw test -Dtest=*Service*   # Nur Service Tests
./mvnw jacoco:report           # Coverage Report
```

### Admin-Web Tests
```bash
cd admin-web
npm test                       # Alle Tests
npm test -- --coverage         # Mit Coverage
npm test -- UserCard           # Spezifischer Test
```

### Mobile Tests
```bash
cd mobile
npm test                       # Alle Tests
npm test -- --coverage         # Mit Coverage
```

---

## ✅ Abnahmekriterien

| Metrik | Zielwert |
|--------|----------|
| Statement Coverage | > 80% |
| Branch Coverage | > 75% |
| Function Coverage | > 80% |
| Unit Test Ratio | 70% |
| Integration Test Ratio | 20% |
| E2E Test Ratio | 10% |
| Alle Tests grün | ✅ |
| Keine flaky Tests | ✅ |
