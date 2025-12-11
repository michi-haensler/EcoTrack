package at.ecotrack.userprofile.dto;

import java.util.UUID;

/**
 * DTO für Milestone.
 */
public record MilestoneDto(
        UUID id,
        String name,
        Long requiredPoints,
        String badgeAsset,
        String description) {
}
