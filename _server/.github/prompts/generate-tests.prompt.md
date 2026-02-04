---
title: "Generate Unit Tests (Java)"
category: "Testing"
description: "Generiert Unit Tests für Java Services nach AAA-Pattern"
---

# Generate Unit Tests Prompt (Java)

Generiere Unit Tests für die angegebene Java-Datei.

## Anforderungen

### Test-Framework
- JUnit 5 + Mockito + AssertJ

### Test-Pattern
- **AAA Pattern**: Arrange - Act - Assert
- **Benennung**: `should_<expected>_when_<condition>`
- **Coverage-Ziel**: 80%

### Test-Arten

1. **Happy Path Tests**
   - Validiere Hauptfunktionalität
   - Return Values korrekt

2. **Edge Cases**
   - Null/Empty Values
   - Boundary Values

3. **Error Cases**
   - Exceptions bei Invalid Input
   - Not Found Cases

4. **Interaction Tests**
   - Repositories aufgerufen
   - Events gepublished

## Beispiel-Output

```java
@ExtendWith(MockitoExtension.class)
class LogActivityServiceTest {
    
    @Mock
    private ActivityEntryRepository activityRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private LogActivityService service;
    
    @Test
    void should_saveActivityAndPublishEvent_when_validCommand() {
        // Arrange
        LogActivityCommand command = createCommand();
        when(activityRepository.save(any())).thenReturn(savedEntry);
        
        // Act
        ActivityEntryDto result = service.execute(command);
        
        // Assert
        assertThat(result).isNotNull();
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
}
```
