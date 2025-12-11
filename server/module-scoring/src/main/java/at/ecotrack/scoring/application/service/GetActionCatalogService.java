package at.ecotrack.scoring.application.service;

import at.ecotrack.scoring.application.dto.ActionDefinitionDto;
import at.ecotrack.scoring.application.mapper.ScoringMapper;
import at.ecotrack.scoring.domain.model.Category;
import at.ecotrack.scoring.domain.port.in.GetActionCatalogUseCase;
import at.ecotrack.scoring.domain.port.out.ActionDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service: Aktionskatalog abfragen.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetActionCatalogService implements GetActionCatalogUseCase {

    private final ActionDefinitionRepository actionDefinitionRepository;
    private final ScoringMapper mapper;

    @Override
    public List<ActionDefinitionDto> getAllActive() {
        return mapper.toActionDefinitionDtoList(
                actionDefinitionRepository.findAllActive());
    }

    @Override
    public List<ActionDefinitionDto> getByCategory(Category category) {
        return mapper.toActionDefinitionDtoList(
                actionDefinitionRepository.findByCategoryAndActive(category));
    }
}
