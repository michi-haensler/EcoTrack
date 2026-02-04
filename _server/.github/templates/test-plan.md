# Test Plan: [Feature/Modul Name]

**Erstellt von**: [Name]  
**Datum**: YYYY-MM-DD  
**Status**: [Draft | In Review | Approved | Executed]

---

## Übersicht

### Ziel
[Was wird getestet?]

### Scope
**In Scope**:
- [Feature 1]

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
**Fokus**: Business Logic, Use Cases

**Frameworks**: JUnit 5 + Mockito + AssertJ

**Coverage-Ziel**: > 80%

**Was testen**:
- Domain Entities
- Use Case Services
- Mappers

#### Integration Tests (20%)
**Fokus**: REST APIs, Repositories, Event Handlers

**Frameworks**: Spring Boot Test + MockMvc

**Coverage-Ziel**: > 70%

**Was testen**:
- REST Endpoints
- Repository Queries
- Event Handlers

#### E2E Tests (10%)
**Fokus**: Critical User Journeys

---

## Akzeptanzkriterien → Test Cases

### AC1: [Beschreibung]

**GIVEN** [Vorbedingung]  
**WHEN** [Aktion]  
**THEN** [Ergebnis]

**Test Case**:
```java
@Test
void should_<expected>_when_<condition>() {
    // Arrange
    
    // Act
    
    // Assert
}
```

---

## Testumgebung

### Datenbank
- [ ] Test-Datenbank (H2 / Testcontainers)
- [ ] Test-Daten vorbereitet

### Externe Services
- [ ] Mocks für externe APIs
- [ ] Keycloak Test-Realm

---

## Ausführung

```bash
# Unit Tests
mvn test

# Integration Tests
mvn verify -P integration-tests

# Coverage Report
mvn test jacoco:report
```

---

## Ergebnisse

| Test-Art | Anzahl | Passed | Failed | Coverage |
|----------|--------|--------|--------|----------|
| Unit     |        |        |        |          |
| Integration |     |        |        |          |
| E2E      |        |        |        |          |

## Bekannte Einschränkungen

- [Einschränkung 1]
