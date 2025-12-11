package at.ecotrack.scoring.domain.model;

import at.ecotrack.shared.domain.AggregateRoot;
import at.ecotrack.shared.domain.BaseEntity;
import at.ecotrack.shared.valueobject.EcoUserId;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * PointsLedgerEntry - Entity im Scoring Context.
 * Protokolliert jede Punktebewegung (Audit-Trail).
 */
@Entity
@Table(name = "points_ledger", schema = "scoring")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PointsLedgerEntry extends BaseEntity implements AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @Column(nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    private String description;

    public enum TransactionType {
        ACTIVITY_LOGGED,
        CHALLENGE_BONUS,
        MANUAL_ADJUSTMENT,
        POINTS_EXPIRED
    }

    /**
     * Factory-Methode für Activity-Punkte.
     */
    public static PointsLedgerEntry forActivity(EcoUserId ecoUserId, UUID activityId, int points) {
        return PointsLedgerEntry.builder()
                .ecoUserId(ecoUserId.value())
                .points(points)
                .transactionType(TransactionType.ACTIVITY_LOGGED)
                .referenceId(activityId)
                .referenceType("ActivityEntry")
                .description("Punkte für Aktivität")
                .build();
    }

    /**
     * Factory-Methode für Challenge-Bonus.
     */
    public static PointsLedgerEntry forChallengeBonus(EcoUserId ecoUserId, UUID challengeId, int bonusPoints) {
        return PointsLedgerEntry.builder()
                .ecoUserId(ecoUserId.value())
                .points(bonusPoints)
                .transactionType(TransactionType.CHALLENGE_BONUS)
                .referenceId(challengeId)
                .referenceType("Challenge")
                .description("Bonus für Challenge-Abschluss")
                .build();
    }

    public EcoUserId getEcoUserIdTyped() {
        return EcoUserId.of(ecoUserId);
    }
}
