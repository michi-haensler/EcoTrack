package at.ecotrack.userprofile.dto;

import java.util.UUID;

/**
 * DTO für Leaderboard-Eintrag.
 */
public record LeaderboardEntryDto(
        int rank,
        UUID ecoUserId,
        UUID userId,
        Long totalPoints,
        String level) {
}
