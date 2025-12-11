package at.ecotrack.administration.repository;

import at.ecotrack.administration.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data Repository für School.
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, UUID> {

    Optional<School> findByCode(String code);

    List<School> findByIsActiveTrue();

    boolean existsByCode(String code);
}
