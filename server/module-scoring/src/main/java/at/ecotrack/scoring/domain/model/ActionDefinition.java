package at.ecotrack.scoring.domain.model;

import at.ecotrack.shared.domain.AggregateRoot;
import at.ecotrack.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * ActionDefinition - Aggregate Root im Scoring Context.
 * Definiert einen Typ von nachhaltiger Aktion mit Basispunkten.
 */
@Entity
@Table(name = "action_definitions", schema = "scoring")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ActionDefinition extends BaseEntity implements AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(name = "base_points", nullable = false)
    private Integer basePoints;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Berechnet Punkte basierend auf Menge.
     */
    public int calculatePoints(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Menge muss größer als 0 sein");
        }
        return (int) Math.round(quantity * basePoints);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateBasePoints(int newBasePoints) {
        if (newBasePoints <= 0) {
            throw new IllegalArgumentException("Basispunkte müssen größer als 0 sein");
        }
        this.basePoints = newBasePoints;
    }
}
