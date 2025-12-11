package at.ecotrack.scoring.adapter.in.rest;

import at.ecotrack.scoring.application.command.LogActivityCommand;
import at.ecotrack.scoring.application.dto.ActionDefinitionDto;
import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.domain.model.ActivitySource;
import at.ecotrack.scoring.domain.model.Category;
import at.ecotrack.scoring.domain.port.in.GetActionCatalogUseCase;
import at.ecotrack.scoring.domain.port.in.GetUserActivitiesUseCase;
import at.ecotrack.scoring.domain.port.in.LogActivityUseCase;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller für Scoring-Modul.
 */
@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
public class ScoringController {

    private final LogActivityUseCase logActivityUseCase;
    private final GetActionCatalogUseCase getActionCatalogUseCase;
    private final GetUserActivitiesUseCase getUserActivitiesUseCase;

    // ==================== Action Catalog ====================

    @GetMapping("/actions")
    public ResponseEntity<List<ActionDefinitionDto>> getAllActions() {
        return ResponseEntity.ok(getActionCatalogUseCase.getAllActive());
    }

    @GetMapping("/actions/category/{category}")
    public ResponseEntity<List<ActionDefinitionDto>> getActionsByCategory(
            @PathVariable Category category) {
        return ResponseEntity.ok(getActionCatalogUseCase.getByCategory(category));
    }

    // ==================== Activities ====================

    @PostMapping("/activities")
    public ResponseEntity<ActivityEntryDto> logActivity(
            @Valid @RequestBody LogActivityRequest request) {
        LogActivityCommand command = new LogActivityCommand(
                EcoUserId.of(request.ecoUserId()),
                request.actionDefinitionId(),
                request.quantity(),
                request.activityDate() != null ? request.activityDate() : LocalDate.now(),
                request.source() != null ? request.source() : ActivitySource.APP,
                request.challengeId() != null ? ChallengeId.of(request.challengeId()) : null);

        ActivityEntryDto result = logActivityUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/activities/user/{ecoUserId}")
    public ResponseEntity<List<ActivityEntryDto>> getUserActivities(
            @PathVariable UUID ecoUserId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        if (from == null)
            from = LocalDate.now().minusDays(30);
        if (to == null)
            to = LocalDate.now();

        return ResponseEntity.ok(
                getUserActivitiesUseCase.getActivities(EcoUserId.of(ecoUserId), from, to));
    }

    @GetMapping("/activities/user/{ecoUserId}/recent")
    public ResponseEntity<List<ActivityEntryDto>> getRecentActivities(
            @PathVariable UUID ecoUserId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                getUserActivitiesUseCase.getRecentActivities(EcoUserId.of(ecoUserId), limit));
    }

    // ==================== Request DTOs ====================

    public record LogActivityRequest(
            UUID ecoUserId,
            UUID actionDefinitionId,
            BigDecimal quantity,
            LocalDate activityDate,
            ActivitySource source,
            UUID challengeId) {
    }
}
