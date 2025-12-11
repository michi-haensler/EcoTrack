package at.ecotrack.userprofile.entity;

import at.ecotrack.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EcoUser - Der fachliche Benutzer mit Punktestand und Gamification-Daten.
 * Supporting Domain: Einfache Entity ohne komplexe Business-Logik.
 */
@Entity
@Table(name = "eco_users", schema = "userprofile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Long totalPoints = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Level level = Level.SETZLING;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "eco_user_milestones", schema = "userprofile", joinColumns = @JoinColumn(name = "eco_user_id"), inverseJoinColumns = @JoinColumn(name = "milestone_id"))
    @Builder.Default
    private Set<Milestone> milestones = new HashSet<>();

    /**
     * Fügt Punkte hinzu und aktualisiert Level.
     */
    public void addPoints(int points) {
        this.totalPoints += points;
        this.level = Level.fromPoints(this.totalPoints);
    }

    /**
     * Überprüft und fügt erreichte Milestones hinzu.
     */
    public void checkAndAddMilestone(Milestone milestone) {
        if (this.totalPoints >= milestone.getRequiredPoints() && !milestones.contains(milestone)) {
            milestones.add(milestone);
        }
    }
}
