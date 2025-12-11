package at.ecotrack.administration.entity;

import at.ecotrack.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * School - Schule als Organisationseinheit.
 */
@Entity
@Table(name = "schools", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String address;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SchoolClass> classes = new HashSet<>();

    public void addClass(SchoolClass schoolClass) {
        classes.add(schoolClass);
        schoolClass.setSchool(this);
    }

    public void removeClass(SchoolClass schoolClass) {
        classes.remove(schoolClass);
        schoolClass.setSchool(null);
    }
}
