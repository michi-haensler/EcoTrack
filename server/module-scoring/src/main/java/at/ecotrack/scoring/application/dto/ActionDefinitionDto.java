package at.ecotrack.scoring.application.dto;

import at.ecotrack.scoring.domain.model.Category;
import at.ecotrack.scoring.domain.model.Unit;

import java.util.UUID;

/**
 * DTO für ActionDefinition.
 */
public record ActionDefinitionDto(
        UUID id,
        String name,
        String description,
        Category category,
        Unit unit,
        Integer basePoints,
        Boolean active) {
}
