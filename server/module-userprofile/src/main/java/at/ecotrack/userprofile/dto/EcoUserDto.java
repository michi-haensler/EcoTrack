package at.ecotrack.userprofile.dto;

import at.ecotrack.userprofile.entity.Level;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO für EcoUser.
 */
public record EcoUserDto(
        UUID id,
        UUID userId,
        UUID classId,
        Long totalPoints,
        Level level,
        List<MilestoneDto> milestones,
        OffsetDateTime createdAt) {
}
