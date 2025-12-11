package at.ecotrack.scoring.domain.port.in;

import at.ecotrack.scoring.application.dto.ActionDefinitionDto;
import at.ecotrack.scoring.domain.model.Category;

import java.util.List;

/**
 * Use Case Port: Aktionskatalog abfragen.
 */
public interface GetActionCatalogUseCase {
    List<ActionDefinitionDto> getAllActive();

    List<ActionDefinitionDto> getByCategory(Category category);
}
