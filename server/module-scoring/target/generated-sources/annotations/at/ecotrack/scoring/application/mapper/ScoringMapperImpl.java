package at.ecotrack.scoring.application.mapper;

import at.ecotrack.scoring.application.dto.ActionDefinitionDto;
import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.domain.model.ActionDefinition;
import at.ecotrack.scoring.domain.model.ActivityEntry;
import at.ecotrack.scoring.domain.model.ActivitySource;
import at.ecotrack.scoring.domain.model.Category;
import at.ecotrack.scoring.domain.model.Unit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-14T12:12:17+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ScoringMapperImpl implements ScoringMapper {

    @Override
    public ActionDefinitionDto toDto(ActionDefinition actionDefinition) {
        if ( actionDefinition == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;
        Category category = null;
        Unit unit = null;
        Integer basePoints = null;
        Boolean active = null;

        id = actionDefinition.getId();
        name = actionDefinition.getName();
        description = actionDefinition.getDescription();
        category = actionDefinition.getCategory();
        unit = actionDefinition.getUnit();
        basePoints = actionDefinition.getBasePoints();
        active = actionDefinition.getActive();

        ActionDefinitionDto actionDefinitionDto = new ActionDefinitionDto( id, name, description, category, unit, basePoints, active );

        return actionDefinitionDto;
    }

    @Override
    public List<ActionDefinitionDto> toActionDefinitionDtoList(List<ActionDefinition> actionDefinitions) {
        if ( actionDefinitions == null ) {
            return null;
        }

        List<ActionDefinitionDto> list = new ArrayList<ActionDefinitionDto>( actionDefinitions.size() );
        for ( ActionDefinition actionDefinition : actionDefinitions ) {
            list.add( toDto( actionDefinition ) );
        }

        return list;
    }

    @Override
    public ActivityEntryDto toDto(ActivityEntry activityEntry) {
        if ( activityEntry == null ) {
            return null;
        }

        ActionDefinitionDto actionDefinition = null;
        UUID id = null;
        UUID ecoUserId = null;
        BigDecimal quantity = null;
        Integer points = null;
        ActivitySource source = null;
        LocalDate activityDate = null;
        UUID challengeId = null;
        OffsetDateTime createdAt = null;

        actionDefinition = toDto( activityEntry.getActionDefinition() );
        id = activityEntry.getId();
        ecoUserId = activityEntry.getEcoUserId();
        quantity = activityEntry.getQuantity();
        points = activityEntry.getPoints();
        source = activityEntry.getSource();
        activityDate = activityEntry.getActivityDate();
        challengeId = activityEntry.getChallengeId();
        createdAt = activityEntry.getCreatedAt();

        ActivityEntryDto activityEntryDto = new ActivityEntryDto( id, ecoUserId, actionDefinition, quantity, points, source, activityDate, challengeId, createdAt );

        return activityEntryDto;
    }

    @Override
    public List<ActivityEntryDto> toActivityEntryDtoList(List<ActivityEntry> activityEntries) {
        if ( activityEntries == null ) {
            return null;
        }

        List<ActivityEntryDto> list = new ArrayList<ActivityEntryDto>( activityEntries.size() );
        for ( ActivityEntry activityEntry : activityEntries ) {
            list.add( toDto( activityEntry ) );
        }

        return list;
    }
}
