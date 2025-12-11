package at.ecotrack.scoring.application.service;

import at.ecotrack.scoring.application.dto.ActivityEntryDto;
import at.ecotrack.scoring.application.mapper.ScoringMapper;
import at.ecotrack.scoring.domain.port.in.GetUserActivitiesUseCase;
import at.ecotrack.scoring.domain.port.out.ActivityEntryRepository;
import at.ecotrack.shared.valueobject.EcoUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Application Service: Aktivitäten eines Users abfragen.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserActivitiesService implements GetUserActivitiesUseCase {

    private final ActivityEntryRepository activityEntryRepository;
    private final ScoringMapper mapper;

    @Override
    public List<ActivityEntryDto> getActivities(EcoUserId ecoUserId, LocalDate from, LocalDate to) {
        return mapper.toActivityEntryDtoList(
                activityEntryRepository.findByEcoUserIdAndDateRange(ecoUserId.value(), from, to));
    }

    @Override
    public List<ActivityEntryDto> getRecentActivities(EcoUserId ecoUserId, int limit) {
        return mapper.toActivityEntryDtoList(
                activityEntryRepository.findRecentByEcoUserId(ecoUserId.value(), limit));
    }
}
