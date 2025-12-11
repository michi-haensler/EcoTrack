package at.ecotrack.scoring.api;

import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.domain.port.in.GetUserActivitiesUseCase;
import at.ecotrack.scoring.domain.port.out.ActivityEntryRepository;
import at.ecotrack.shared.valueobject.EcoUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementierung der Modul-Facade.
 */
@Service
@RequiredArgsConstructor
public class ScoringModuleFacadeImpl implements ScoringModuleFacade {

    private final GetUserActivitiesUseCase getUserActivitiesUseCase;
    private final ActivityEntryRepository activityEntryRepository;

    @Override
    public List<ActivityEntryDto> getRecentActivities(EcoUserId ecoUserId, int limit) {
        return getUserActivitiesUseCase.getRecentActivities(ecoUserId, limit);
    }

    @Override
    public int getPointsInPeriod(EcoUserId ecoUserId, LocalDate from, LocalDate to) {
        return activityEntryRepository.sumPointsByEcoUserIdAndDateRange(ecoUserId.value(), from, to);
    }
}
