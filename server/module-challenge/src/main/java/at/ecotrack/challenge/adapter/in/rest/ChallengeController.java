package at.ecotrack.challenge.adapter.in.rest;

import at.ecotrack.challenge.application.dto.ChallengeDto;
import at.ecotrack.challenge.application.service.ChallengeService;
import at.ecotrack.challenge.domain.model.GoalUnit;
import at.ecotrack.shared.valueobject.ClassId;
import at.ecotrack.shared.valueobject.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller für Challenge-Modul.
 */
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ChallengeDto> createChallenge(
            @Valid @RequestBody CreateChallengeRequest request) {
        ChallengeDto result = challengeService.createChallenge(
                request.title(),
                request.description(),
                request.goalValue(),
                request.goalUnit(),
                request.startDate(),
                request.endDate(),
                ClassId.of(request.classId()),
                UserId.of(request.createdBy()),
                request.bonusPoints());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDto> getChallenge(@PathVariable UUID challengeId) {
        return ResponseEntity.ok(challengeService.getChallenge(challengeId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ChallengeDto>> getChallengesForClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(challengeService.getChallengesForClass(ClassId.of(classId)));
    }

    @GetMapping("/class/{classId}/active")
    public ResponseEntity<List<ChallengeDto>> getActiveChallengesForClass(@PathVariable UUID classId) {
        return ResponseEntity.ok(challengeService.getActiveChallengesForClass(ClassId.of(classId)));
    }

    @PostMapping("/{challengeId}/activate")
    public ResponseEntity<ChallengeDto> activateChallenge(@PathVariable UUID challengeId) {
        return ResponseEntity.ok(challengeService.activateChallenge(challengeId));
    }

    @PostMapping("/{challengeId}/close")
    public ResponseEntity<ChallengeDto> closeChallenge(@PathVariable UUID challengeId) {
        return ResponseEntity.ok(challengeService.closeChallenge(challengeId));
    }

    // ==================== Request DTOs ====================

    public record CreateChallengeRequest(
            @NotBlank String title,
            String description,
            @NotNull @Positive BigDecimal goalValue,
            @NotNull GoalUnit goalUnit,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotNull UUID classId,
            @NotNull UUID createdBy,
            Integer bonusPoints) {
    }
}
