package at.ecotrack.scoring.domain.event;

import at.ecotrack.shared.domain.DomainEvent;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Event: Eine neue Aktivität wurde erfasst.
 * Wird vom UserProfile-Modul konsumiert, um Punkte zu aktualisieren.
 */
public record ActivityLoggedEvent(
        UUID activityId,
        EcoUserId ecoUserId,
        int points,
        LocalDate activityDate,
        ChallengeId challengeId) implements DomainEvent {
}
