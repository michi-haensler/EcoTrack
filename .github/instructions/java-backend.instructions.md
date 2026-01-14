---
applyTo: "server/**/*.java"
description: "Java Backend Coding Standards für EcoTrack"
---

## Java Backend Standards

### Spring Boot Best Practices

#### Dependency Injection
- Constructor Injection bevorzugen (keine @Autowired auf Feldern)
- `@RequiredArgsConstructor` von Lombok nutzen
- Interfaces für Services nur wenn mehrere Implementierungen existieren

```java
@Service
@RequiredArgsConstructor
public class LogActivityService implements LogActivityUseCase {
    private final ActivityEntryRepository activityRepository;
    private final EventPublisher eventPublisher;
    
    // Constructor wird automatisch von Lombok generiert
}
```

#### REST Controller
- `@RestController` statt `@Controller` + `@ResponseBody`
- Request/Response DTOs verwenden (keine Entities exposen)
- HTTP Status Codes korrekt nutzen
- `ResponseEntity<T>` für explizite Kontrolle

```java
@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
public class ScoringController {
    
    @PostMapping("/activities")
    public ResponseEntity<ActivityEntryDto> logActivity(
            @Valid @RequestBody LogActivityCommand command
    ) {
        ActivityEntryDto result = logActivityUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
```

### Hexagonal Architecture (Scoring & Challenge Module)

#### Schichten-Trennung
```
domain/          - Entities, Value Objects, Events (keine Framework-Abhängigkeiten!)
├── model/       - Aggregate Roots, Entities
├── event/       - Domain Events
└── port/
    ├── in/      - Use Case Interfaces
    └── out/     - Repository Interfaces, Event Publisher

application/     - Use Case Implementierungen, DTOs
├── command/     - Command Objects
├── dto/         - Data Transfer Objects
├── mapper/      - Domain ↔ DTO Mapping
└── service/     - Use Case Services

adapter/
├── in/
│   └── rest/    - REST Controller
└── out/
    ├── persistence/  - JPA Repositories
    └── event/        - Event Publisher Implementierung

api/             - Module Facade (öffentliche API)
```

#### Domain Layer
- **Keine** Spring-Annotationen
- **Keine** JPA-Annotationen
- Pure Business Logic
- Rich Domain Model (Verhalten in Entities)

```java
// ✅ Richtig
public class ActivityEntry extends AggregateRoot {
    private EcoUserId ecoUserId;
    private ActionDefinitionId actionDefinitionId;
    private int quantity;
    
    public int calculatePoints(ActionDefinition action) {
        return action.getPoints() * this.quantity;
    }
}

// ❌ Falsch - Framework-Abhängigkeiten in Domain
@Entity
public class ActivityEntry {
    @Id
    private UUID id;
    // ...
}
```

#### Application Layer
- Use Cases als Services implementieren
- Transactions hier @Transactional
- DTOs für Input/Output

```java
@Service
@RequiredArgsConstructor
@Transactional
public class LogActivityService implements LogActivityUseCase {
    
    private final ActivityEntryRepository activityRepository;
    private final ActionDefinitionRepository actionRepository;
    private final EventPublisher eventPublisher;
    private final ScoringMapper mapper;
    
    @Override
    public ActivityEntryDto execute(LogActivityCommand command) {
        // 1. Load
        ActionDefinition action = actionRepository.findById(command.actionDefinitionId())
                .orElseThrow(() -> new EntityNotFoundException("Action not found"));
        
        // 2. Create Entity
        ActivityEntry entry = ActivityEntry.builder()
                .ecoUserId(command.ecoUserId())
                .actionDefinitionId(command.actionDefinitionId())
                .quantity(command.quantity())
                .build();
        
        int points = entry.calculatePoints(action);
        
        // 3. Save
        ActivityEntry saved = activityRepository.save(entry);
        
        // 4. Publish Event
        eventPublisher.publish(new ActivityLoggedEvent(
                saved.getEcoUserId(),
                points,
                saved.getLoggedAt()
        ));
        
        // 5. Return DTO
        return mapper.toDto(saved, action);
    }
}
```

#### Adapter Layer
- JPA Entities hier mit Annotationen
- Repository Adapter implementiert Port-Interface
- Mapper für Domain ↔ JPA Entity

```java
// JPA Entity (adapter/out/persistence)
@Entity
@Table(name = "activity_entries", schema = "scoring")
@Getter
@Setter
@NoArgsConstructor
public class ActivityEntryJpaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "eco_user_id")
    private UUID ecoUserId;
    
    // ... JPA Mappings
}

// Repository Adapter
@Component
@RequiredArgsConstructor
public class ActivityEntryRepositoryAdapter implements ActivityEntryRepository {
    
    private final ActivityEntryJpaRepository jpaRepository;
    private final ActivityEntryJpaMapper mapper;
    
    @Override
    public ActivityEntry save(ActivityEntry entry) {
        ActivityEntryJpaEntity entity = mapper.toJpaEntity(entry);
        ActivityEntryJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### JPA & Hibernate

#### Entity Mapping
- `@Entity` + `@Table` mit explizitem Schema
- UUID für IDs (GenerationType.UUID)
- Timestamps mit `@CreatedDate` / `@LastModifiedDate`
- Lazy Loading default (nur eager wenn wirklich nötig)

```java
@Entity
@Table(name = "challenges", schema = "challenge")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challenge extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;
}
```

#### Repository Pattern
- Extend `JpaRepository<T, ID>`
- Custom Queries mit `@Query`
- Sprechende Methodennamen

```java
@Repository
public interface ChallengeJpaRepository extends JpaRepository<Challenge, UUID> {
    
    List<Challenge> findByClassIdAndStatus(UUID classId, ChallengeStatus status);
    
    @Query("SELECT c FROM Challenge c WHERE c.startDate <= :date AND c.endDate >= :date")
    List<Challenge> findActiveChallenges(@Param("date") LocalDate date);
}
```

### MapStruct Mapping

```java
@Mapper(componentModel = "spring")
public interface ScoringMapper {
    
    ActivityEntryDto toDto(ActivityEntry entry, ActionDefinition action);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ActivityEntry toEntity(LogActivityCommand command);
}
```

### Domain Events

#### Event Definition
```java
public record ActivityLoggedEvent(
        EcoUserId ecoUserId,
        int points,
        OffsetDateTime timestamp
) implements DomainEvent {
}
```

#### Event Publishing
```java
@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
}
```

#### Event Handling
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLoggedEventHandler {
    
    private final EcoUserRepository ecoUserRepository;
    
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(ActivityLoggedEvent event) {
        log.info("Processing ActivityLoggedEvent: {}", event);
        
        EcoUser user = ecoUserRepository.findById(event.ecoUserId().value())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        user.addPoints(event.points());
        ecoUserRepository.save(user);
    }
}
```

### Validation

- Jakarta Validation Annotations
- Custom Validators für Business Rules
- Validierung in Controller (Request) und Domain (Business Logic)

```java
public record LogActivityCommand(
        @NotNull UUID ecoUserId,
        @NotNull UUID actionDefinitionId,
        @Min(1) @Max(100) int quantity,
        String notes
) {
}
```

### Exception Handling

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }
}
```

### Security

- OAuth2/OIDC mit Keycloak
- Rollen: STUDENT, TEACHER, ADMIN
- Method Security: `@PreAuthorize("hasRole('ADMIN')")`
- Niemals Secrets im Code (Environment Variables nutzen)

### Logging

```java
@Slf4j
public class MyService {
    
    public void doSomething() {
        log.debug("Starting operation with param: {}", param);
        
        try {
            // ...
        } catch (Exception e) {
            log.error("Operation failed: {}", e.getMessage(), e);
            throw new ServiceException("Failed to process", e);
        }
        
        log.info("Operation completed successfully");
    }
}
```

### Testing

#### Unit Tests
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
        LogActivityCommand command = new LogActivityCommand(/* ... */);
        when(activityRepository.save(any())).thenReturn(savedEntry);
        
        // Act
        ActivityEntryDto result = service.execute(command);
        
        // Assert
        assertThat(result).isNotNull();
        verify(eventPublisher).publish(any(ActivityLoggedEvent.class));
    }
}
```

#### Integration Tests
```java
@SpringBootTest
@ActiveProfiles("test")
class ScoringControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void should_return201_when_activityLogged() throws Exception {
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(/* JSON */))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
```

## Modul-spezifische Regeln

### Shared Kernel
- Nur Value Objects und Interfaces
- **Keine** Abhängigkeiten zu anderen Modulen
- Immutable Value Objects

### Core Domains (Scoring, Challenge)
- Hexagonal Architecture strikt einhalten
- Domain Layer Framework-frei
- Use Cases als Interfaces im domain/port/in

### Supporting Domain (UserProfile)
- Vereinfachte CRUD-Architektur
- Entity → Repository → Controller
- Services für Business Logic

### Generic Domain (Administration)
- ACL (Anti-Corruption Layer) für Keycloak
- Keycloak-Adapter übersetzt externe Modelle
