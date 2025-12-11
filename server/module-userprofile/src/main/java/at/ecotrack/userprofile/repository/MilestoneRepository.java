package at.ecotrack.userprofile.repository;

import at.ecotrack.userprofile.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data Repository für Milestone.
 */
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID> {

    List<Milestone> findAllByOrderByRequiredPointsAsc();

    @Query("SELECT m FROM Milestone m WHERE m.requiredPoints <= :points ORDER BY m.requiredPoints DESC")
    List<Milestone> findReachableMilestones(@Param("points") long points);
}
