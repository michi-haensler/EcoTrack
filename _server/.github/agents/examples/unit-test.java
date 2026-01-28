// ============================================================
// Unit Test Beispiel - LogActivityServiceTest
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Unit Tests mit JUnit 5, Mockito und AssertJ.
// ============================================================

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LogActivityServiceTest {

    @Mock
    private ActivityEntryRepository activityRepository;

    @Mock
    private ActionDefinitionRepository actionRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ScoringMapper mapper;

    @InjectMocks
    private LogActivityService service;

    // -------------------------
    // Happy Path Tests
    // -------------------------

    @Test
    void should_saveActivityAndPublishEvent_when_validCommand() {
        // Arrange
        LogActivityCommand command = LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();

        ActionDefinition action = ActionDefinition.builder()
                .id(command.actionDefinitionId())
                .name("Radfahren")
                .points(10)
                .build();

        ActivityEntry savedEntry = ActivityEntry.builder()
                .id(UUID.randomUUID())
                .ecoUserId(command.ecoUserId())
                .actionDefinitionId(command.actionDefinitionId())
                .quantity(5)
                .build();

        ActivityEntryDto expectedDto = new ActivityEntryDto(
                savedEntry.getId(), savedEntry.getEcoUserId(),
                null, 5, 50, null, OffsetDateTime.now());

        when(actionRepository.findById(command.actionDefinitionId()))
                .thenReturn(Optional.of(action));
        when(activityRepository.save(any(ActivityEntry.class)))
                .thenReturn(savedEntry);
        when(mapper.toDto(any(), any()))
                .thenReturn(expectedDto);

        // Act
        ActivityEntryDto result = service.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.quantity()).isEqualTo(5);
        assertThat(result.points()).isEqualTo(50);

        verify(activityRepository).save(any(ActivityEntry.class));
        verify(eventPublisher).publish(argThat(event -> event instanceof ActivityLoggedEvent &&
                ((ActivityLoggedEvent) event).points() == 50));
    }

    // -------------------------
    // Error Case Tests
    // -------------------------

    @Test
    void should_throwException_when_actionNotFound() {
        // Arrange
        LogActivityCommand command = createCommand();
        when(actionRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Action not found");

        // Verify no side effects
        verify(activityRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void should_throwException_when_quantityIsNegative() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(-1)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be positive");
    }

    @Test
    void should_throwException_when_quantityIsZero() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(0)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -------------------------
    // Edge Case Tests
    // -------------------------

    @Test
    void should_calculateCorrectPoints_when_quantityIsLarge() {
        // Arrange
        LogActivityCommand command = createCommandWithQuantity(100);
        ActionDefinition action = createActionWithPoints(5);
        ActivityEntry savedEntry = createEntry(command);

        when(actionRepository.findById(any())).thenReturn(Optional.of(action));
        when(activityRepository.save(any())).thenReturn(savedEntry);
        when(mapper.toDto(any(), any())).thenReturn(createDto(500));

        // Act
        ActivityEntryDto result = service.execute(command);

        // Assert
        assertThat(result.points()).isEqualTo(500); // 100 * 5
    }

    // -------------------------
    // Helper Methods
    // -------------------------

    private LogActivityCommand createCommand() {
        return LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .build();
    }

    private LogActivityCommand createCommandWithQuantity(int quantity) {
        return LogActivityCommand.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(quantity)
                .build();
    }

    private ActionDefinition createActionWithPoints(int points) {
        return ActionDefinition.builder()
                .id(UUID.randomUUID())
                .name("Test Action")
                .points(points)
                .build();
    }

    private ActivityEntry createEntry(LogActivityCommand command) {
        return ActivityEntry.builder()
                .id(UUID.randomUUID())
                .ecoUserId(command.ecoUserId())
                .actionDefinitionId(command.actionDefinitionId())
                .quantity(command.quantity())
                .build();
    }

    private ActivityEntryDto createDto(int points) {
        return new ActivityEntryDto(
                UUID.randomUUID(), UUID.randomUUID(),
                null, 1, points, null, OffsetDateTime.now());
    }
}
