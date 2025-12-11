package at.ecotrack.challenge.domain.event;

import at.ecotrack.shared.domain.DomainEvent;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;

/**
 * Event: Challenge-Ziel wurde erreicht.
 */
public record ChallengeGoalReachedEvent(
        ChallengeId challengeId,
        EcoUserId ecoUserId,
        int bonusPoints) implements DomainEvent {
}
