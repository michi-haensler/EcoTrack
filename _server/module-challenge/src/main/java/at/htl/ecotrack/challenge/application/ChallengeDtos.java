package at.htl.ecotrack.challenge.application;

import at.htl.ecotrack.shared.model.ChallengeStatus;
import at.htl.ecotrack.shared.model.GoalUnit;
import at.htl.ecotrack.shared.model.PeriodType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ChallengeDtos {

    public record CreateChallengeRequest(
            @NotBlank @Size(min = 3, max = 100) String title,
            @Size(max = 500) String description,
            @Min(1) double goalValue,
            @NotNull GoalUnit goalUnit,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotNull UUID classId
    ) {
    }

    public record ChallengeGoalResponse(double targetValue, GoalUnit unit) {
    }

    public record ChallengeResponse(UUID challengeId, String title, String description, ChallengeStatus status,
                                    ChallengeGoalResponse goal, LocalDate startDate, LocalDate endDate,
                                    UUID classId, String className, UUID createdBy) {
    }

    public record ChallengeProgressResponse(UUID challengeId, double currentValue, double targetValue,
                                            @Min(0) @Max(100) double percentage, boolean completed) {
    }

    public record ChallengeDetailResponse(UUID challengeId, String title, String description, ChallengeStatus status,
                                          ChallengeGoalResponse goal, LocalDate startDate, LocalDate endDate,
                                          UUID classId, String className, UUID createdBy,
                                          ChallengeProgressResponse progress, int participants) {
    }

    public record ChallengeSummary(UUID challengeId, String title, ChallengeStatus status, double progressPercentage, int daysRemaining) {
    }

    public record ActionStats(UUID actionDefinitionId, String actionName, int count, int totalPoints) {
    }

    public record StudentSummary(UUID ecoUserId, String displayName, int points, int rank, at.htl.ecotrack.shared.model.Level level) {
    }

    public record ClassSummaryResponse(UUID classId, String className, PeriodType period, int totalPoints, int totalActivities,
                                       int activeStudents, int totalStudents, List<ChallengeSummary> activeChallenges,
                                       List<ActionStats> topActions, List<StudentSummary> topStudents) {
    }

    private ChallengeDtos() {
    }
}
