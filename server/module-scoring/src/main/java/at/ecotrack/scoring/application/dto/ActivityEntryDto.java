package at.ecotrack.scoring.application.dto;

import at.ecotrack.scoring.domain.model.ActivitySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO für ActivityEntry.
 */
public record ActivityEntryDto(
        UUID id,
        UUID ecoUserId,
        ActionDefinitionDto actionDefinition,
        BigDecimal quantity,
        Integer points,
        ActivitySource source,
        LocalDate activityDate,
        UUID challengeId,
        OffsetDateTime createdAt) {
}
