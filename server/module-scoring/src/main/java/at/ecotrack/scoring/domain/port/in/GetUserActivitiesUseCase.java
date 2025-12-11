package at.ecotrack.scoring.domain.port.in;

import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.shared.valueobject.EcoUserId;

import java.time.LocalDate;
import java.util.List;

/**
 * Use Case Port: Aktivitäten eines Users abfragen.
 */
public interface GetUserActivitiesUseCase {
    List<ActivityEntryDto> getActivities(EcoUserId ecoUserId, LocalDate from, LocalDate to);

    List<ActivityEntryDto> getRecentActivities(EcoUserId ecoUserId, int limit);
}
