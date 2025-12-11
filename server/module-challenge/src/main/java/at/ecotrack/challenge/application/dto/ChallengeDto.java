package at.ecotrack.challenge.application.dto;

import at.ecotrack.challenge.domain.model.ChallengeStatus;
import at.ecotrack.challenge.domain.model.GoalUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO für Challenge.
 */
public record ChallengeDto(
        UUID id,
        String title,
        String description,
        BigDecimal goalValue,
        GoalUnit goalUnit,
        ChallengeStatus status,
        LocalDate startDate,
        LocalDate endDate,
        UUID classId,
        UUID createdBy,
        Integer bonusPoints,
        OffsetDateTime createdAt) {
}
