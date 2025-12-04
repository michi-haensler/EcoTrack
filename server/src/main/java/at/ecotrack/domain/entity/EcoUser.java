package at.ecotrack.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Fachlicher Endnutzer von EcoTrack.
 * Aggregate Root im User Profile Context.
 */
@Entity
@Table(name = "eco_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Long totalPoints = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void addPoints(int points) {
        this.totalPoints += points;
    }
}
