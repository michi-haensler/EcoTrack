// ============================================================
// Event Publisher & Handler Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Domain Events für Modul-Kommunikation.
// ============================================================

// -----------------------------
// Domain Event Interface
// -----------------------------

import java.time.OffsetDateTime;
import java.util.List;

public interface DomainEvent {
    // Marker Interface für Domain Events
}

// -----------------------------
// Konkretes Domain Event
// -----------------------------
public record ActivityLoggedEvent(
        EcoUserId ecoUserId,
        int points,
        OffsetDateTime timestamp) implements DomainEvent {
}

public record ChallengeCompletedEvent(
        ChallengeId challengeId,
        SchoolClassId schoolClassId,
        int totalPoints,
        OffsetDateTime completedAt) implements DomainEvent {
}

// -----------------------------
// Event Publisher Port (Domain)
// -----------------------------
public interface EventPublisher {
    void publish(DomainEvent event);
}

// -----------------------------
// Spring Event Publisher Adapter
// -----------------------------
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}

// -----------------------------
// Event Handler (in anderem Modul!)
// -----------------------------
// Wichtig: Handler in REQUIRES_NEW Transaktion für Isolation

// Im UserProfile-Modul: Punkte gutschreiben
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLoggedEventHandler {

    private final EcoUserRepository ecoUserRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(ActivityLoggedEvent event) {
        log.info("Processing ActivityLoggedEvent: {}", event);

        EcoUser user = ecoUserRepository
                .findById(event.ecoUserId().value())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Business Logic
        user.addPoints(event.points());

        ecoUserRepository.save(user);

        log.info("Points added to user {}: {}",
                event.ecoUserId(), event.points());
    }
}

// Im Challenge-Modul: Challenge-Fortschritt aktualisieren
@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeProgressUpdater {

    private final ChallengeRepository challengeRepository;
    private final EventPublisher eventPublisher;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onActivityLogged(ActivityLoggedEvent event) {
        log.debug("Updating challenge progress for user: {}", event.ecoUserId());

        // Alle aktiven Challenges für den User finden
        List<Challenge> activeChallenges = challengeRepository
                .findActiveByUserId(event.ecoUserId());

        for (Challenge challenge : activeChallenges) {
            challenge.addProgress(event.points());

            // Prüfen ob Challenge abgeschlossen
            if (challenge.isCompleted()) {
                eventPublisher.publish(new ChallengeCompletedEvent(
                        challenge.getId(),
                        challenge.getSchoolClassId(),
                        challenge.getTotalPoints(),
                        OffsetDateTime.now()));
            }

            challengeRepository.save(challenge);
        }
    }
}

// -----------------------------
// Async Event Handler (optional)
// -----------------------------
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async // Asynchrone Verarbeitung
    @EventListener
    public void onChallengeCompleted(ChallengeCompletedEvent event) {
        log.info("Sending notifications for completed challenge: {}",
                event.challengeId());

        notificationService.notifyClassAboutChallengeCompletion(
                event.schoolClassId(),
                event.challengeId(),
                event.totalPoints());
    }
}
