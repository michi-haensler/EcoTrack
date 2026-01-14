---
agent: true
title: "Generate Unit Tests"
category: "Testing"
description: "Generiert Unit Tests für Java Services oder TypeScript Components nach AAA-Pattern mit hoher Coverage"
intent: "Automatisiere die Erstellung von Unit Tests basierend auf bestehender Implementierung"
context: "Use Cases, Services, Components die noch keine oder unvollständige Tests haben"
variables:
  - name: "file"
    description: "Der Pfad zur Datei, für die Tests generiert werden sollen"
    required: true
  - name: "coverage"
    description: "Gewünschte Test Coverage in Prozent (default: 80)"
    required: false
    default: "80"
---

# Generate Unit Tests Prompt

Generiere vollständige Unit Tests für die folgende Datei: `${file}`

## Anforderungen

### Test-Framework
- **Java**: JUnit 5 + Mockito + AssertJ
- **TypeScript**: Vitest + React Testing Library (für Components)

### Test-Pattern
- **AAA Pattern**: Arrange - Act - Assert
- **Benennung**: `should_<expected>_when_<condition>`
- **Coverage-Ziel**: ${coverage}%

### Test-Arten

#### Für Java Services (Use Cases):
1. **Happy Path Tests**
   - Validiere Hauptfunktionalität
   - Alle Pfade durchlaufen
   - Return Values korrekt

2. **Edge Cases**
   - Null/Empty Values
   - Boundary Values
   - Concurrent Access (wenn relevant)

3. **Error Cases**
   - Exceptions bei Invalid Input
   - Not Found Cases
   - Authorization Failures

4. **Interaction Tests**
   - Repositories aufgerufen
   - Events gepublished
   - Mapper verwendet

#### Für TypeScript Components:
1. **Rendering Tests**
   - Component rendert ohne Fehler
   - Props werden angezeigt
   - Conditional Rendering

2. **Interaction Tests**
   - Click Handlers
   - Form Submission
   - State Updates

3. **Integration Tests**
   - API Calls (mit MSW mocken)
   - TanStack Query Hooks
   - Error Handling

### Code-Qualität
- Test Data Factories für wiederverwendbare Daten
- Mock Setup in `beforeEach` (wenn shared)
- Cleanup in `afterEach` (wenn nötig)
- Keine Magic Numbers/Strings
- Sprechende Variable-Namen

## Schritte

1. **Datei analysieren**
   - Welche Klasse/Component?
   - Welche Dependencies?
   - Welche Public Methods/Props?
   - Welche Business Logic?

2. **Test-Datei erstellen**
   - Naming: `<OriginalName>Test.java` oder `<OriginalName>.test.ts`
   - Location: Gleiche Package-Struktur wie Source

3. **Test Setup**
   - Mocks für Dependencies
   - Test Data Factory (optional)
   - `beforeEach` für gemeinsame Setups

4. **Tests schreiben**
   - Happy Path zuerst
   - Edge Cases
   - Error Cases
   - Coverage > ${coverage}%

5. **Verifizieren**
   - Tests laufen durch (grün ✅)
   - Coverage-Report prüfen
   - Keine Flaky Tests

## Beispiel-Output

### Java Service Test

```java
@ExtendWith(MockitoExtension.class)
class LogActivityServiceTest {
    
    @Mock
    private ActivityEntryRepository activityRepository;
    
    @Mock
    private ActionDefinitionRepository actionRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private LogActivityService service;
    
    @Test
    void should_saveActivityAndPublishEvent_when_validCommand() {
        // Arrange
        LogActivityCommand command = createCommand();
        ActionDefinition action = createAction(10);
        ActivityEntry savedEntry = createEntry(command);
        
        when(actionRepository.findById(any())).thenReturn(Optional.of(action));
        when(activityRepository.save(any())).thenReturn(savedEntry);
        
        // Act
        ActivityEntryDto result = service.execute(command);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.points()).isEqualTo(50);
        verify(eventPublisher).publish(any(ActivityLoggedEvent.class));
    }
    
    @Test
    void should_throwException_when_actionNotFound() {
        // Arrange
        LogActivityCommand command = createCommand();
        when(actionRepository.findById(any())).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Action not found");
    }
    
    // Helper Methods
    private LogActivityCommand createCommand() {
        return LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();
    }
    
    private ActionDefinition createAction(int points) {
        return ActionDefinition.builder()
                .id(UUID.randomUUID())
                .points(points)
                .build();
    }
}
```

### TypeScript Component Test

```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { ActivityCard } from './activity-card';
import { createTestActivity } from '@/test/factories';

describe('ActivityCard', () => {
  it('should_renderActivityInfo_when_activityProvided', () => {
    // Arrange
    const activity = createTestActivity();
    const onSelect = vi.fn();
    
    // Act
    render(<ActivityCard activity={activity} onSelect={onSelect} />);
    
    // Assert
    expect(screen.getByText(activity.action.name)).toBeInTheDocument();
    expect(screen.getByText(`${activity.points} Punkte`)).toBeInTheDocument();
  });
  
  it('should_callOnSelect_when_clicked', () => {
    // Arrange
    const activity = createTestActivity();
    const onSelect = vi.fn();
    
    render(<ActivityCard activity={activity} onSelect={onSelect} />);
    
    // Act
    fireEvent.click(screen.getByRole('button'));
    
    // Assert
    expect(onSelect).toHaveBeenCalledWith(activity.id);
  });
});
```

## Nach der Generierung

1. **Tests ausführen**
   ```bash
   # Java
   ./mvnw test -Dtest=<TestClassName>
   
   # TypeScript
   npm run test -- <test-file>
   ```

2. **Coverage prüfen**
   ```bash
   # Java
   ./mvnw test jacoco:report
   
   # TypeScript
   npm run test:coverage
   ```

3. **Review**
   - Sind alle wichtigen Szenarien abgedeckt?
   - Sind die Tests verständlich?
   - Keine Flaky Tests?

Starte jetzt die Test-Generierung für: `${file}`
