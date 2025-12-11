package at.ecotrack.userprofile.repository;

import at.ecotrack.userprofile.entity.EcoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data Repository für EcoUser.
 */
@Repository
public interface EcoUserRepository extends JpaRepository<EcoUser, UUID> {

    Optional<EcoUser> findByUserId(UUID userId);

    List<EcoUser> findByClassId(UUID classId);

    @Query("SELECT e FROM EcoUser e WHERE e.classId = :classId ORDER BY e.totalPoints DESC")
    List<EcoUser> findByClassIdOrderByPointsDesc(@Param("classId") UUID classId);

    @Query("SELECT e FROM EcoUser e ORDER BY e.totalPoints DESC")
    List<EcoUser> findAllOrderByPointsDesc();

    long countByClassId(UUID classId);
}
