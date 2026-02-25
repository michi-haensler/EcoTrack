package at.htl.ecotrack.scoring.domain;

import at.htl.ecotrack.shared.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityEntryRepository extends JpaRepository<ActivityEntry, UUID> {
    Page<ActivityEntry> findByEcoUserIdOrderByTimestampDesc(UUID ecoUserId, Pageable pageable);
    Page<ActivityEntry> findByEcoUserIdAndCategoryOrderByTimestampDesc(UUID ecoUserId, Category category, Pageable pageable);
    Optional<ActivityEntry> findTopByEcoUserIdAndActionDefinitionIdAndQuantityAndActivityDateOrderByTimestampDesc(
            UUID ecoUserId,
            UUID actionDefinitionId,
            double quantity,
            LocalDate activityDate
    );
    List<ActivityEntry> findByEcoUserIdInAndTimestampAfter(List<UUID> ecoUserIds, OffsetDateTime after);
    List<ActivityEntry> findByEcoUserIdIn(List<UUID> ecoUserIds);
    List<ActivityEntry> findByEcoUserIdInAndActivityDateBetween(List<UUID> ecoUserIds, LocalDate startDate, LocalDate endDate);
}
