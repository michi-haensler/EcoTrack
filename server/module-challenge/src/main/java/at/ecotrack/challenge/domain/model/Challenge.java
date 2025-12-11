package at.ecotrack.challenge.domain.model;

import at.ecotrack.shared.domain.AggregateRoot;
import at.ecotrack.shared.domain.BaseEntity;
import at.ecotrack.shared.valueobject.ClassId;
import at.ecotrack.shared.valueobject.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Challenge - Aggregate Root im Challenge Context.
 * Repräsentiert einen zeitlich begrenzten Wettbewerb.
 */
@Entity
@Table(name = "challenges", schema = "challenge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Challenge extends BaseEntity implements AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "goal_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal goalValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_unit", nullable = false)
    private GoalUnit goalUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.DRAFT;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "bonus_points")
    @Builder.Default
    private Integer bonusPoints = 0;

    /**
     * Factory-Methode zum Erstellen einer neuen Challenge.
     */
    public static Challenge create(
            String title,
            String description,
            BigDecimal goalValue,
            GoalUnit goalUnit,
            LocalDate startDate,
            LocalDate endDate,
            ClassId classId,
            UserId createdBy,
            Integer bonusPoints) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Enddatum muss nach Startdatum liegen");
        }
        if (goalValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Zielwert muss größer als 0 sein");
        }

        return Challenge.builder()
                .title(title)
                .description(description)
                .goalValue(goalValue)
                .goalUnit(goalUnit)
                .startDate(startDate)
                .endDate(endDate)
                .classId(classId.value())
                .createdBy(createdBy.value())
                .bonusPoints(bonusPoints != null ? bonusPoints : 0)
                .status(ChallengeStatus.DRAFT)
                .build();
    }

    public void activate() {
        if (this.status != ChallengeStatus.DRAFT) {
            throw new IllegalStateException("Nur Entwürfe können aktiviert werden");
        }
        this.status = ChallengeStatus.ACTIVE;
    }

    public void close() {
        if (this.status != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("Nur aktive Challenges können geschlossen werden");
        }
        this.status = ChallengeStatus.CLOSED;
    }

    public boolean isActive() {
        return this.status == ChallengeStatus.ACTIVE;
    }

    public boolean isWithinPeriod(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public ClassId getClassIdTyped() {
        return ClassId.of(classId);
    }

    public UserId getCreatedByTyped() {
        return UserId.of(createdBy);
    }
}
