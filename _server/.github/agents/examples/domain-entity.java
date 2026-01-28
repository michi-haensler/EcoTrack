// ============================================================
// Domain Entity Beispiel - ActivityEntry
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung einer
// Domain Entity nach Hexagonal Architecture & DDD Prinzipien.
// ============================================================

// -----------------------------
// Value Object für typsichere ID
// -----------------------------

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Value
public class ActivityEntryId {
    UUID value;

    public static ActivityEntryId newId() {
        return new ActivityEntryId(UUID.randomUUID());
    }

    public static ActivityEntryId of(UUID value) {
        return new ActivityEntryId(value);
    }
}

// -----------------------------
// Domain Entity (Aggregate Root)
// -----------------------------
// ✅ Pure Java - keine Framework-Abhängigkeiten!
public class ActivityEntry extends AggregateRoot {
    private ActivityEntryId id;
    private EcoUserId ecoUserId;
    private ActionDefinitionId actionDefinitionId;
    private int quantity;
    private String notes;
    private OffsetDateTime loggedAt;

    // Business Logic gehört in die Domain Entity
    public int calculatePoints(ActionDefinition action) {
        return action.getPoints() * this.quantity;
    }

    // Factory Method mit Validierung
    public static ActivityEntry create(EcoUserId userId,
            ActionDefinitionId actionId,
            int quantity) {
        // Validation in Factory Method
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        return new ActivityEntry(
                ActivityEntryId.newId(),
                userId,
                actionId,
                quantity);
    }

    // Private Constructor - Factory Method nutzen!
    private ActivityEntry(ActivityEntryId id,
            EcoUserId userId,
            ActionDefinitionId actionId,
            int quantity) {
        this.id = id;
        this.ecoUserId = userId;
        this.actionDefinitionId = actionId;
        this.quantity = quantity;
        this.loggedAt = OffsetDateTime.now();
    }

    // Getter (kein Setter - Immutability bevorzugen)
    public ActivityEntryId getId() {
        return id;
    }

    public EcoUserId getEcoUserId() {
        return ecoUserId;
    }

    public ActionDefinitionId getActionDefinitionId() {
        return actionDefinitionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getLoggedAt() {
        return loggedAt;
    }
}

// -----------------------------
// ❌ FALSCH: Framework-Abhängigkeiten in Domain
// -----------------------------
// @Entity // NO! Gehört in Adapter Layer
// public class ActivityEntry {
// @Id // NO!
// private UUID id;
// }

// -----------------------------
// Domain Port (Interface)
// -----------------------------
// Output Port - wird vom Adapter implementiert
public interface ActivityEntryRepository {
    ActivityEntry save(ActivityEntry entry);

    Optional<ActivityEntry> findById(ActivityEntryId id);

    List<ActivityEntry> findByEcoUserId(EcoUserId userId);
}

// Input Port - Use Case Interface
public interface LogActivityUseCase {
    ActivityEntryDto execute(LogActivityCommand command);
}

// -----------------------------
// Domain Event
// -----------------------------
public record ActivityLoggedEvent(
        EcoUserId ecoUserId,
        int points,
        OffsetDateTime timestamp) implements DomainEvent {
}
