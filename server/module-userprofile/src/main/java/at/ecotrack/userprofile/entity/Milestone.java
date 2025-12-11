package at.ecotrack.userprofile.entity;

import at.ecotrack.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Milestone - Badge/Auszeichnung bei Erreichen bestimmter Punkte.
 */
@Entity
@Table(name = "milestones", schema = "userprofile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "required_points", nullable = false)
    private Long requiredPoints;

    @Column(name = "badge_asset")
    private String badgeAsset;

    private String description;
}
