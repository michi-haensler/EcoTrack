---
applyTo: "**/*Test.java,**/test/**/*.java"
description: "Java Testing Standards für EcoTrack Backend"
---

## Java Testing Standards

### Allgemeine Testprinzipien

#### AAA Pattern (Arrange-Act-Assert)
Alle Tests folgen diesem Muster:

```java
@Test
void should_calculateTotal_when_itemsProvided() {
    // Arrange - Setup
    List<Item> items = List.of(new Item(10), new Item(20));
    PriceCalculator calculator = new PriceCalculator();
    
    // Act - Ausführung
    int result = calculator.calculateTotal(items);
    
    // Assert - Überprüfung
    assertThat(result).isEqualTo(30);
}
```

#### Test-Benennung
Format: `should_<expected>_when_<condition>`

```
✅ should_returnUser_when_validIdProvided
✅ should_throwException_when_userNotFound
✅ should_calculatePoints_when_activityLogged
❌ testUser
❌ test1
```

#### Test-Isolation
- Jeder Test ist unabhängig
- Keine Reihenfolge-Abhängigkeiten
- Deterministisch (keine Random, keine Timestamps)
- Setup/Teardown mit @BeforeEach/@AfterEach

### Unit Tests (JUnit 5 + Mockito)

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
    void should_saveActivity_when_validCommand() {
        // Arrange
        LogActivityCommand command = LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();
        
        ActionDefinition action = ActionDefinition.builder()
                .id(command.actionDefinitionId())
                .points(10)
                .build();
        
        when(actionRepository.findById(command.actionDefinitionId()))
                .thenReturn(Optional.of(action));
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
}
```

### Integration Tests

#### Controller Tests (MockMvc)
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoringControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void should_return201_when_activityLogged() throws Exception {
        // Arrange
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                5,
                "Test"
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.points").value(50));
    }
    
    @Test
    void should_return404_when_actionNotFound() throws Exception {
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                5,
                null
        );
        
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
```

#### Repository Tests
```java
@DataJpaTest
@ActiveProfiles("test")
class ActivityEntryRepositoryTest {
    
    @Autowired
    private ActivityEntryJpaRepository repository;
    
    @Test
    void should_saveActivity_when_validEntity() {
        // Arrange
        ActivityEntryJpaEntity entity = new ActivityEntryJpaEntity();
        entity.setEcoUserId(UUID.randomUUID());
        entity.setQuantity(5);
        
        // Act
        ActivityEntryJpaEntity saved = repository.save(entity);
        
        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
```

### Test Doubles

#### Mocks
Für Abhängigkeiten mit komplexem Verhalten:
```java
@Mock
private UserRepository userRepository;

when(userRepository.findById(any())).thenReturn(Optional.of(user));
verify(userRepository).save(any());
```

#### Stubs
Für vorhersehbare Antworten:
```java
when(service.calculate()).thenReturn(100);
```

#### Spies
Für Verhaltensüberwachung:
```java
@Spy
private EventPublisher eventPublisher;

verify(eventPublisher, times(1)).publish(any());
```

### Test Data Factory

```java
public class TestDataFactory {
    
    public static LogActivityCommand createCommand() {
        return LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();
    }
    
    public static ActionDefinition createAction(int points) {
        return ActionDefinition.builder()
                .id(UUID.randomUUID())
                .name("Test Action")
                .points(points)
                .build();
    }
}
```

### Coverage-Ziele

- Unit Tests: 70-80%
- Integration Tests: 20-30%
- E2E Tests: 5-10%

### Was testen?

✅ Business Logic
✅ Edge Cases
✅ Error Handling
✅ Domain Events
✅ API Integration

❌ Triviale Getter/Setter
❌ Framework-Code
❌ Third-Party Libraries
