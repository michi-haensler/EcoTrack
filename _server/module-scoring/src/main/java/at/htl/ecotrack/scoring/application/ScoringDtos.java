package at.htl.ecotrack.scoring.application;

import at.htl.ecotrack.shared.model.ActivitySource;
import at.htl.ecotrack.shared.model.Category;
import at.htl.ecotrack.shared.model.Level;
import at.htl.ecotrack.shared.model.PeriodType;
import at.htl.ecotrack.shared.model.Unit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class ScoringDtos {

    public record ActionDefinitionResponse(
            UUID actionDefinitionId,
            String name,
            String description,
            Category category,
            Unit unit,
            int basePoints,
            boolean active
    ) {
    }

    public record CreateActivityRequest(@NotNull UUID actionDefinitionId, @Min(1) double quantity, LocalDate date) {
    }

    public record ActivityEntryResponse(
            UUID activityEntryId,
            UUID actionDefinitionId,
            String actionName,
            Category category,
            double quantity,
            Unit unit,
            int points,
            OffsetDateTime timestamp,
            ActivitySource source
    ) {
    }

    public record ActivityPageResponse(
            List<ActivityEntryResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    public record PointsLedgerResponse(UUID ecoUserId, int totalPoints, OffsetDateTime lastUpdated) {
    }

    public record MilestoneResponse(UUID milestoneId, String name, int requiredPoints, boolean reached, OffsetDateTime reachedAt) {
    }

    public record TreeVisualizationResponse(Level level, String assetName, double growthPercentage) {
    }

    public record ProgressSnapshotResponse(
            UUID ecoUserId,
            int totalPoints,
            Level currentLevel,
            int pointsToNextLevel,
            double progressPercentage,
            List<MilestoneResponse> milestones,
            TreeVisualizationResponse treeVisualization
    ) {
    }

    public record RankingTableResponse(String scope, PeriodType period, OffsetDateTime generatedAt, List<RankingRowResponse> rows) {
    }

    public record RankingRowResponse(int rank, UUID ecoUserId, String displayName, int points, Level level, boolean isCurrentUser) {
    }

    private ScoringDtos() {
    }
}
