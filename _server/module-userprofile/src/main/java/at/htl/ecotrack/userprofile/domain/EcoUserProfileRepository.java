package at.htl.ecotrack.userprofile.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EcoUserProfileRepository extends JpaRepository<EcoUserProfile, UUID> {
    Optional<EcoUserProfile> findByUserId(UUID userId);
    Optional<EcoUserProfile> findByEmail(String email);
    List<EcoUserProfile> findByClassId(UUID classId);
    List<EcoUserProfile> findBySchoolId(UUID schoolId);
}
