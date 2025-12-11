package at.ecotrack.administration.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO für School.
 */
public record SchoolDto(
        UUID id,
        String name,
        String code,
        String address,
        String contactEmail,
        Boolean isActive,
        Set<SchoolClassDto> classes,
        OffsetDateTime createdAt) {
}
