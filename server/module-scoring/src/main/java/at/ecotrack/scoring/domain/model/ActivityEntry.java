package at.ecotrack.scoring.domain.model;

import at.ecotrack.shared.domain.AggregateRoot;
import at.ecotrack.shared.domain.BaseEntity;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * ActivityEntry - Aggregate Root im Scoring Context.
 * Repräsentiert eine einzelne durchgeführte nachhaltige Aktion.
 */
@Entity
@Table(name = "activity_entries", schema = "scoring")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ActivityEntry extends BaseEntity implements AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_definition_id", nullable = false)
    private ActionDefinition actionDefinition;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ActivitySource source = ActivitySource.APP;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "challenge_id")
    private UUID challengeId;

    /**
     * Factory-Methode zur Erstellung einer neuen Activity.
     */
    public static ActivityEntry create(
            EcoUserId ecoUserId,
            ActionDefinition actionDefinition,
            BigDecimal quantity,
            LocalDate activityDate,
            ActivitySource source,
            ChallengeId challengeId) {
        if (!actionDefinition.getActive()) {
            throw new IllegalStateException("Aktion ist nicht aktiv: " + actionDefinition.getName());
        }

        int calculatedPoints = actionDefinition.calculatePoints(quantity.doubleValue());

        return ActivityEntry.builder()
                .ecoUserId(ecoUserId.value())
                .actionDefinition(actionDefinition)
                .quantity(quantity)
                .points(calculatedPoints)
                .activityDate(activityDate)
                .source(source)
                .challengeId(challengeId != null ? challengeId.value() : null)
                .build();
    }

    public EcoUserId getEcoUserIdTyped() {
        return EcoUserId.of(ecoUserId);
    }

    public ChallengeId getChallengeIdTyped() {
        return challengeId != null ? ChallengeId.of(challengeId) : null;
    }
}
