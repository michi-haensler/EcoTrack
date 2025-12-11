package at.ecotrack.administration.entity;

import at.ecotrack.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * SchoolClass - Schulklasse mit zugeordneten Lehrern (aus Keycloak).
 */
@Entity
@Table(name = "school_classes", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "school_year", nullable = false)
    private String schoolYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Lehrer-IDs (aus Keycloak) die diese Klasse betreuen.
     */
    @ElementCollection
    @CollectionTable(name = "class_teachers", schema = "admin", joinColumns = @JoinColumn(name = "class_id"))
    @Column(name = "teacher_user_id")
    @Builder.Default
    private Set<UUID> teacherUserIds = new HashSet<>();

    public void addTeacher(UUID teacherUserId) {
        teacherUserIds.add(teacherUserId);
    }

    public void removeTeacher(UUID teacherUserId) {
        teacherUserIds.remove(teacherUserId);
    }
}
