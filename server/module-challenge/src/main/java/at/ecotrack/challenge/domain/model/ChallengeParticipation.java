package at.ecotrack.challenge.domain.model;

import at.ecotrack.shared.domain.BaseEntity;
import at.ecotrack.shared.valueobject.ChallengeId;
import at.ecotrack.shared.valueobject.EcoUserId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ChallengeParticipation - Entity im Challenge Context.
 * Verfolgt den Fortschritt eines Users in einer Challenge.
 */
@Entity
@Table(name = "challenge_participations", schema = "challenge", uniqueConstraints = @UniqueConstraint(columnNames = {
        "challenge_id", "eco_user_id" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChallengeParticipation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @Column(name = "current_value", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal currentValue = BigDecimal.ZERO;

    @Column(name = "goal_reached", nullable = false)
    @Builder.Default
    private Boolean goalReached = false;

    @Column(name = "bonus_awarded", nullable = false)
    @Builder.Default
    private Boolean bonusAwarded = false;

    /**
     * Factory-Methode zur Erstellung einer neuen Teilnahme.
     */
    public static ChallengeParticipation create(ChallengeId challengeId, EcoUserId ecoUserId) {
        return ChallengeParticipation.builder()
                .challengeId(challengeId.value())
                .ecoUserId(ecoUserId.value())
                .currentValue(BigDecimal.ZERO)
                .goalReached(false)
                .bonusAwarded(false)
                .build();
    }

    /**
     * Aktualisiert den Fortschritt.
     */
    public void updateProgress(BigDecimal additionalValue, BigDecimal goalValue) {
        this.currentValue = this.currentValue.add(additionalValue);
        if (this.currentValue.compareTo(goalValue) >= 0) {
            this.goalReached = true;
        }
    }

    /**
     * Markiert Bonus als vergeben.
     */
    public void awardBonus() {
        if (!this.goalReached) {
            throw new IllegalStateException("Bonus kann nur bei erreichtem Ziel vergeben werden");
        }
        if (this.bonusAwarded) {
            throw new IllegalStateException("Bonus wurde bereits vergeben");
        }
        this.bonusAwarded = true;
    }

    public ChallengeId getChallengeIdTyped() {
        return ChallengeId.of(challengeId);
    }

    public EcoUserId getEcoUserIdTyped() {
        return EcoUserId.of(ecoUserId);
    }

    /**
     * Berechnet Fortschritt in Prozent.
     */
    public int getProgressPercentage(BigDecimal goalValue) {
        if (goalValue.compareTo(BigDecimal.ZERO) == 0)
            return 0;
        return currentValue.multiply(BigDecimal.valueOf(100))
                .divide(goalValue, 0, java.math.RoundingMode.FLOOR)
                .min(BigDecimal.valueOf(100))
                .intValue();
    }
}
