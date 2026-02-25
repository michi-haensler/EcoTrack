package at.htl.ecotrack.challenge.domain;

import at.htl.ecotrack.shared.model.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByClassId(UUID classId);
    List<Challenge> findByClassIdAndStatus(UUID classId, ChallengeStatus status);
    Optional<Challenge> findByClassIdAndTitleIgnoreCase(UUID classId, String title);
}
