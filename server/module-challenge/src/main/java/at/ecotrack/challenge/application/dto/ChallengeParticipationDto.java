package at.ecotrack.challenge.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO für ChallengeParticipation mit Fortschritt.
 */
public record ChallengeParticipationDto(
        UUID id,
        UUID challengeId,
        UUID ecoUserId,
        BigDecimal currentValue,
        BigDecimal goalValue,
        int progressPercentage,
        Boolean goalReached,
        Boolean bonusAwarded) {
}
