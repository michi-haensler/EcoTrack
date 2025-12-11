package at.ecotrack.userprofile.eventhandler;

import at.ecotrack.scoring.domain.event.ActivityLoggedEvent;
import at.ecotrack.userprofile.entity.EcoUser;
import at.ecotrack.userprofile.entity.Milestone;
import at.ecotrack.userprofile.repository.EcoUserRepository;
import at.ecotrack.userprofile.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Event Handler: Reagiert auf Events aus dem Scoring-Modul.
 * Aktualisiert Punkte und prüft Milestones.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLoggedEventHandler {

    private final EcoUserRepository ecoUserRepository;
    private final MilestoneRepository milestoneRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(ActivityLoggedEvent event) {
        log.info("ActivityLoggedEvent empfangen: userId={}, points={}",
                event.ecoUserId(), event.points());

        EcoUser ecoUser = ecoUserRepository.findById(event.ecoUserId().value())
                .orElseThrow(() -> new IllegalStateException(
                        "EcoUser nicht gefunden: " + event.ecoUserId()));

        // Punkte hinzufügen (Level wird automatisch aktualisiert)
        ecoUser.addPoints(event.points());

        // Erreichbare Milestones prüfen und hinzufügen
        List<Milestone> reachableMilestones = milestoneRepository
                .findReachableMilestones(ecoUser.getTotalPoints());

        for (Milestone milestone : reachableMilestones) {
            ecoUser.checkAndAddMilestone(milestone);
        }

        ecoUserRepository.save(ecoUser);

        log.info("EcoUser aktualisiert: id={}, totalPoints={}, level={}",
                ecoUser.getId(), ecoUser.getTotalPoints(), ecoUser.getLevel());
    }
}
