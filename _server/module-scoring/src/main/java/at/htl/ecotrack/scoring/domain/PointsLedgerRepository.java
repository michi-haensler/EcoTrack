package at.htl.ecotrack.scoring.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PointsLedgerRepository extends JpaRepository<PointsLedger, UUID> {
    List<PointsLedger> findByEcoUserIdIn(List<UUID> ecoUserIds);
}
