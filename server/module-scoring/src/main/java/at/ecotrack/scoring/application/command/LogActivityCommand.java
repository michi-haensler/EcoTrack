package at.ecotrack.scoring.application.command;

import at.ecotrack.scoring.domain.model.ActivitySource;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command: Neue Aktivität erfassen.
 */
public record LogActivityCommand(
        @NotNull EcoUserId ecoUserId,
        @NotNull UUID actionDefinitionId,
        @NotNull @Positive BigDecimal quantity,
        @NotNull LocalDate activityDate,
        ActivitySource source,
        ChallengeId challengeId) {
    public LogActivityCommand {
        if (source == null) {
            source = ActivitySource.APP;
        }
    }
}
