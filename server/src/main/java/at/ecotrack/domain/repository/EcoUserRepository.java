package at.ecotrack.domain.repository;

import at.ecotrack.domain.entity.EcoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EcoUserRepository extends JpaRepository<EcoUser, UUID> {

    Optional<EcoUser> findByUserId(UUID userId);

    @Query("SELECT e FROM EcoUser e WHERE e.schoolClass.id = :classId ORDER BY e.totalPoints DESC")
    List<EcoUser> findByClassIdOrderByPointsDesc(UUID classId);

    @Query("SELECT COUNT(e) FROM EcoUser e WHERE e.schoolClass.id = :classId")
    long countByClassId(UUID classId);
}
