package at.ecotrack.userprofile.controller;

import at.ecotrack.userprofile.dto.EcoUserDto;
import at.ecotrack.userprofile.dto.LeaderboardEntryDto;
import at.ecotrack.userprofile.dto.MilestoneDto;
import at.ecotrack.userprofile.entity.EcoUser;
import at.ecotrack.userprofile.entity.Milestone;
import at.ecotrack.userprofile.repository.EcoUserRepository;
import at.ecotrack.userprofile.repository.MilestoneRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * REST Controller für UserProfile-Modul.
 * Supporting Domain: Einfache CRUD-Operationen.
 */
@RestController
@RequestMapping("/api/userprofile")
@RequiredArgsConstructor
public class UserProfileController {

    private final EcoUserRepository ecoUserRepository;
    private final MilestoneRepository milestoneRepository;

    // ==================== EcoUser ====================

    @PostMapping("/users")
    public ResponseEntity<EcoUserDto> createEcoUser(@Valid @RequestBody CreateEcoUserRequest request) {
        EcoUser ecoUser = EcoUser.builder()
                .userId(request.userId())
                .classId(request.classId())
                .build();

        EcoUser saved = ecoUserRepository.save(ecoUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<EcoUserDto> getEcoUser(@PathVariable UUID id) {
        return ecoUserRepository.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/by-user/{userId}")
    public ResponseEntity<EcoUserDto> getEcoUserByUserId(@PathVariable UUID userId) {
        return ecoUserRepository.findByUserId(userId)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/class/{classId}")
    public ResponseEntity<List<EcoUserDto>> getEcoUsersByClass(@PathVariable UUID classId) {
        List<EcoUserDto> users = ecoUserRepository.findByClassId(classId)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    // ==================== Leaderboard ====================

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntryDto> leaderboard = ecoUserRepository.findAllOrderByPointsDesc()
                .stream()
                .limit(limit)
                .map(user -> new LeaderboardEntryDto(
                        rank.getAndIncrement(),
                        user.getId(),
                        user.getUserId(),
                        user.getTotalPoints(),
                        user.getLevel().getDisplayName()))
                .toList();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/leaderboard/class/{classId}")
    public ResponseEntity<List<LeaderboardEntryDto>> getClassLeaderboard(
            @PathVariable UUID classId,
            @RequestParam(defaultValue = "50") int limit) {
        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardEntryDto> leaderboard = ecoUserRepository.findByClassIdOrderByPointsDesc(classId)
                .stream()
                .limit(limit)
                .map(user -> new LeaderboardEntryDto(
                        rank.getAndIncrement(),
                        user.getId(),
                        user.getUserId(),
                        user.getTotalPoints(),
                        user.getLevel().getDisplayName()))
                .toList();
        return ResponseEntity.ok(leaderboard);
    }

    // ==================== Milestones ====================

    @GetMapping("/milestones")
    public ResponseEntity<List<MilestoneDto>> getAllMilestones() {
        List<MilestoneDto> milestones = milestoneRepository.findAllByOrderByRequiredPointsAsc()
                .stream()
                .map(this::toMilestoneDto)
                .toList();
        return ResponseEntity.ok(milestones);
    }

    // ==================== Mapping ====================

    private EcoUserDto toDto(EcoUser user) {
        List<MilestoneDto> milestoneDtos = user.getMilestones().stream()
                .map(this::toMilestoneDto)
                .toList();

        return new EcoUserDto(
                user.getId(),
                user.getUserId(),
                user.getClassId(),
                user.getTotalPoints(),
                user.getLevel(),
                milestoneDtos,
                user.getCreatedAt());
    }

    private MilestoneDto toMilestoneDto(Milestone milestone) {
        return new MilestoneDto(
                milestone.getId(),
                milestone.getName(),
                milestone.getRequiredPoints(),
                milestone.getBadgeAsset(),
                milestone.getDescription());
    }

    // ==================== Request DTOs ====================

    public record CreateEcoUserRequest(
            UUID userId,
            UUID classId) {
    }
}
