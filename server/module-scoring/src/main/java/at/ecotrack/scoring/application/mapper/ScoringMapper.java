package at.ecotrack.scoring.application.mapper;

import at.ecotrack.scoring.application.dto.ActionDefinitionDto;
import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.domain.model.ActionDefinition;
import at.ecotrack.scoring.domain.model.ActivityEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScoringMapper {

    ActionDefinitionDto toDto(ActionDefinition actionDefinition);

    List<ActionDefinitionDto> toActionDefinitionDtoList(List<ActionDefinition> actionDefinitions);

    @Mapping(target = "actionDefinition", source = "actionDefinition")
    ActivityEntryDto toDto(ActivityEntry activityEntry);

    List<ActivityEntryDto> toActivityEntryDtoList(List<ActivityEntry> activityEntries);
}
