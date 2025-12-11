package at.ecotrack.scoring.application.service;

import at.ecotrack.scoring.application.command.LogActivityCommand;
import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.application.mapper.ScoringMapper;
import at.ecotrack.scoring.domain.event.ActivityLoggedEvent;
import at.ecotrack.scoring.domain.model.ActionDefinition;
import at.ecotrack.scoring.domain.model.ActivityEntry;
import at.ecotrack.scoring.domain.model.PointsLedgerEntry;
import at.ecotrack.scoring.domain.port.in.LogActivityUseCase;
import at.ecotrack.scoring.domain.port.out.ActionDefinitionRepository;
import at.ecotrack.scoring.domain.port.out.ActivityEntryRepository;
import at.ecotrack.scoring.domain.port.out.EventPublisher;
import at.ecotrack.scoring.domain.port.out.PointsLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Service: Aktivität erfassen.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LogActivityService implements LogActivityUseCase {

    private final ActivityEntryRepository activityEntryRepository;
    private final ActionDefinitionRepository actionDefinitionRepository;
    private final PointsLedgerRepository pointsLedgerRepository;
    private final EventPublisher eventPublisher;
    private final ScoringMapper mapper;

    @Override
    public ActivityEntryDto handle(LogActivityCommand command) {
        // 1. ActionDefinition laden
        ActionDefinition actionDefinition = actionDefinitionRepository
                .findById(command.actionDefinitionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ActionDefinition nicht gefunden: " + command.actionDefinitionId()));

        // 2. ActivityEntry erstellen (Punkte werden im Domain-Model berechnet)
        ActivityEntry activityEntry = ActivityEntry.create(
                command.ecoUserId(),
                actionDefinition,
                command.quantity(),
                command.activityDate(),
                command.source(),
                command.challengeId());

        // 3. Speichern
        ActivityEntry savedEntry = activityEntryRepository.save(activityEntry);

        // 4. PointsLedger-Eintrag erstellen
        PointsLedgerEntry ledgerEntry = PointsLedgerEntry.forActivity(
                command.ecoUserId(),
                savedEntry.getId(),
                savedEntry.getPoints());
        pointsLedgerRepository.save(ledgerEntry);

        // 5. Event publizieren (für UserProfile-Modul)
        eventPublisher.publish(new ActivityLoggedEvent(
                savedEntry.getId(),
                command.ecoUserId(),
                savedEntry.getPoints(),
                savedEntry.getActivityDate(),
                command.challengeId()));

        return mapper.toDto(savedEntry);
    }
}
