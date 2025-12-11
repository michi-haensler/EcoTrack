package at.ecotrack.administration.dto;

import java.util.Set;
import java.util.UUID;

/**
 * DTO für SchoolClass.
 */
public record SchoolClassDto(
        UUID id,
        String name,
        String schoolYear,
        UUID schoolId,
        String schoolName,
        Boolean isActive,
        Set<UUID> teacherUserIds) {
}
