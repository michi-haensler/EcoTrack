// ============================================================
// Use Case Service Beispiel - LogActivityService
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// Use Case Services im Application Layer.
// ============================================================

// -----------------------------
// Command Object (Input DTO)
// -----------------------------

import java.time.OffsetDateTime;
import java.util.UUID;

public record LogActivityCommand(
        EcoUserId ecoUserId,
        ActionDefinitionId actionDefinitionId,
        int quantity,
        String notes) {
    // Validation im Constructor
    public LogActivityCommand {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}

// -----------------------------
// Response DTO
// -----------------------------
public record ActivityEntryDto(
        UUID id,
        UUID ecoUserId,
        ActionDefinitionDto action,
        int quantity,
        int points,
        String notes,
        OffsetDateTime loggedAt) {
}

// -----------------------------
// Use Case Service Implementation
// -----------------------------
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LogActivityService implements LogActivityUseCase {

    // Constructor Injection via Lombok @RequiredArgsConstructor
    private final ActivityEntryRepository activityRepository;
    private final ActionDefinitionRepository actionRepository;
    private final EventPublisher eventPublisher;
    private final ScoringMapper mapper;

    @Override
    public ActivityEntryDto execute(LogActivityCommand command) {
        log.debug("Logging activity: {}", command);

        // 1. Load Dependencies
        ActionDefinition action = actionRepository
                .findById(command.actionDefinitionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Action not found: " + command.actionDefinitionId()));

        // 2. Create Domain Object (Factory Method)
        ActivityEntry entry = ActivityEntry.create(
                command.ecoUserId(),
                command.actionDefinitionId(),
                command.quantity());

        // 3. Business Logic in Domain
        int points = entry.calculatePoints(action);

        // 4. Persist via Repository Port
        ActivityEntry saved = activityRepository.save(entry);

        // 5. Publish Domain Event
        eventPublisher.publish(new ActivityLoggedEvent(
                saved.getEcoUserId(),
                points,
                saved.getLoggedAt()));

        log.info("Activity logged: {} points for user {}",
                points, saved.getEcoUserId());

        // 6. Return DTO (nicht Domain Object!)
        return mapper.toDto(saved, action);
    }
}

// -----------------------------
// Mapper (MapStruct)
// -----------------------------
@Mapper(componentModel = "spring")
public interface ScoringMapper {

    ActivityEntryDto toDto(ActivityEntry entry, ActionDefinition action);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ActivityEntry toEntity(LogActivityCommand command);
}
