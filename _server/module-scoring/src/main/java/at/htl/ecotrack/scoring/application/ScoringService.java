package at.htl.ecotrack.scoring.application;

import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Category;
import at.htl.ecotrack.shared.model.Level;
import at.htl.ecotrack.shared.model.PeriodType;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;
import at.htl.ecotrack.scoring.domain.ActionDefinition;
import at.htl.ecotrack.scoring.domain.ActionDefinitionRepository;
import at.htl.ecotrack.scoring.domain.ActivityEntry;
import at.htl.ecotrack.scoring.domain.ActivityEntryRepository;
import at.htl.ecotrack.scoring.domain.PointsLedger;
import at.htl.ecotrack.scoring.domain.PointsLedgerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static at.htl.ecotrack.shared.model.ActivitySource.APP;

@Service
public class ScoringService {

    private final ActionDefinitionRepository actionRepository;
    private final ActivityEntryRepository activityRepository;
    private final PointsLedgerRepository ledgerRepository;
    private final EcoUserProfileService profileService;

    private final int level1;
    private final int level2;
    private final int level3;
    private final int level4;

    public ScoringService(ActionDefinitionRepository actionRepository,
                          ActivityEntryRepository activityRepository,
                          PointsLedgerRepository ledgerRepository,
                          EcoUserProfileService profileService,
                          @Value("${ecotrack.level-thresholds.jungbaum:100}") int level1,
                          @Value("${ecotrack.level-thresholds.baum:250}") int level2,
                          @Value("${ecotrack.level-thresholds.altbaum:500}") int level3,
                          @Value("${ecotrack.level-thresholds.legend:1000}") int level4) {
        this.actionRepository = actionRepository;
        this.activityRepository = activityRepository;
        this.ledgerRepository = ledgerRepository;
        this.profileService = profileService;
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;
    }

    public List<ScoringDtos.ActionDefinitionResponse> getCatalog(Category category) {
        List<ActionDefinition> actions = category == null
                ? actionRepository.findByActiveTrue()
                : actionRepository.findByActiveTrueAndCategory(category);
        return actions.stream().map(this::toActionResponse).toList();
    }

    @Transactional
    public ScoringDtos.ActivityEntryResponse createActivity(CurrentUser currentUser, ScoringDtos.CreateActivityRequest request) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        ActionDefinition action = actionRepository.findById(request.actionDefinitionId())
                .filter(ActionDefinition::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ACTION_NOT_FOUND", "Aktion nicht verfügbar"));

        LocalDate activityDate = request.date() == null ? LocalDate.now() : request.date();
        activityRepository
                .findTopByEcoUserIdAndActionDefinitionIdAndQuantityAndActivityDateOrderByTimestampDesc(
                        profile.getEcoUserId(),
                        request.actionDefinitionId(),
                        request.quantity(),
                        activityDate
                )
                .ifPresent(existing -> {
                    if (existing.getTimestamp().isAfter(OffsetDateTime.now().minusMinutes(5))) {
                        throw new ApiException(HttpStatus.CONFLICT, "DUPLICATE_ACTIVITY", "Duplikat innerhalb von 5 Minuten erkannt");
                    }
                });

        int points = (int) Math.round(request.quantity() * action.getBasePoints());

        ActivityEntry entry = new ActivityEntry();
        entry.setActivityEntryId(UUID.randomUUID());
        entry.setEcoUserId(profile.getEcoUserId());
        entry.setActionDefinitionId(action.getActionDefinitionId());
        entry.setActionName(action.getName());
        entry.setCategory(action.getCategory());
        entry.setQuantity(request.quantity());
        entry.setUnit(action.getUnit());
        entry.setPoints(points);
        entry.setActivityDate(activityDate);
        entry.setSource(APP);

        ActivityEntry saved = activityRepository.save(entry);

        PointsLedger ledger = ledgerRepository.findById(profile.getEcoUserId()).orElseGet(() -> {
            PointsLedger l = new PointsLedger();
            l.setEcoUserId(profile.getEcoUserId());
            l.setTotalPoints(0);
            return l;
        });
        ledger.setTotalPoints(ledger.getTotalPoints() + points);
        ledgerRepository.save(ledger);

        return toActivityResponse(saved);
    }

    public ScoringDtos.ActivityPageResponse getMyActivities(CurrentUser currentUser, int page, int size, Category category) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<ActivityEntry> result = category == null
                ? activityRepository.findByEcoUserIdOrderByTimestampDesc(profile.getEcoUserId(), pageable)
                : activityRepository.findByEcoUserIdAndCategoryOrderByTimestampDesc(profile.getEcoUserId(), category, pageable);

        return new ScoringDtos.ActivityPageResponse(
                result.getContent().stream().map(this::toActivityResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public ScoringDtos.PointsLedgerResponse getMyPoints(CurrentUser currentUser) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        PointsLedger ledger = ledgerRepository.findById(profile.getEcoUserId()).orElseGet(() -> {
            PointsLedger l = new PointsLedger();
            l.setEcoUserId(profile.getEcoUserId());
            l.setTotalPoints(0);
            return l;
        });
        return new ScoringDtos.PointsLedgerResponse(ledger.getEcoUserId(), ledger.getTotalPoints(), ledger.getLastUpdated());
    }

    public ScoringDtos.ProgressSnapshotResponse getMyProgress(CurrentUser currentUser) {
        ScoringDtos.PointsLedgerResponse points = getMyPoints(currentUser);
        int totalPoints = points.totalPoints();

        Level level = toLevel(totalPoints);
        int nextLevel = switch (level) {
            case SETZLING -> level1;
            case JUNGBAUM -> level2;
            case BAUM -> level3;
            case ALTBAUM -> level4;
        };
        int previousLevel = switch (level) {
            case SETZLING -> 0;
            case JUNGBAUM -> level1;
            case BAUM -> level2;
            case ALTBAUM -> level3;
        };

        int pointsToNext = Math.max(nextLevel - totalPoints, 0);
        double progress = nextLevel == previousLevel ? 100d : Math.min(100d, ((double) (totalPoints - previousLevel) / (double) (nextLevel - previousLevel)) * 100d);

        return new ScoringDtos.ProgressSnapshotResponse(
                points.ecoUserId(),
                totalPoints,
                level,
                pointsToNext,
                progress,
                List.of(
                        new ScoringDtos.MilestoneResponse(UUID.randomUUID(), "Setzling", 0, totalPoints >= 0, null),
                        new ScoringDtos.MilestoneResponse(UUID.randomUUID(), "Jungbaum", level1, totalPoints >= level1, null),
                        new ScoringDtos.MilestoneResponse(UUID.randomUUID(), "Baum", level2, totalPoints >= level2, null),
                        new ScoringDtos.MilestoneResponse(UUID.randomUUID(), "Altbaum", level3, totalPoints >= level3, null)
                ),
                new ScoringDtos.TreeVisualizationResponse(level, level.name().toLowerCase(), progress)
        );
    }

    public ScoringDtos.RankingTableResponse getClassLeaderboard(CurrentUser currentUser, PeriodType period) {
        EcoUserProfile me = profileService.getByUserId(currentUser.userId());
        if (me.getClassId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CLASS_REQUIRED", "Nutzer ist keiner Klasse zugeordnet");
        }
        List<EcoUserProfile> profiles = profileService.getByClassId(me.getClassId());
        return buildLeaderboard("CLASS", period, profiles, me.getEcoUserId());
    }

    public ScoringDtos.RankingTableResponse getSchoolLeaderboard(CurrentUser currentUser, PeriodType period) {
        EcoUserProfile me = profileService.getByUserId(currentUser.userId());
        if (me.getSchoolId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "SCHOOL_REQUIRED", "Nutzer ist keiner Schule zugeordnet");
        }
        List<EcoUserProfile> profiles = profileService.getBySchoolId(me.getSchoolId());
        return buildLeaderboard("SCHOOL", period, profiles, me.getEcoUserId());
    }

    public int getUserPointsInRange(UUID ecoUserId, LocalDate startDate, LocalDate endDate) {
        return activityRepository.findByEcoUserIdInAndActivityDateBetween(List.of(ecoUserId), startDate, endDate)
                .stream()
                .mapToInt(ActivityEntry::getPoints)
                .sum();
    }

    private ScoringDtos.RankingTableResponse buildLeaderboard(String scope,
                                                              PeriodType period,
                                                              List<EcoUserProfile> profiles,
                                                              UUID currentEcoUserId) {
        List<UUID> ids = profiles.stream().map(EcoUserProfile::getEcoUserId).toList();

        Map<UUID, Integer> pointsByUser;
        if (period == null || period == PeriodType.TOTAL) {
            pointsByUser = ledgerRepository.findByEcoUserIdIn(ids).stream()
                    .collect(Collectors.toMap(PointsLedger::getEcoUserId, PointsLedger::getTotalPoints));
        } else {
            OffsetDateTime since = period == PeriodType.WEEK
                    ? OffsetDateTime.now().minus(7, ChronoUnit.DAYS)
                    : OffsetDateTime.now().minus(30, ChronoUnit.DAYS);
            pointsByUser = activityRepository.findByEcoUserIdInAndTimestampAfter(ids, since)
                    .stream()
                    .collect(Collectors.groupingBy(ActivityEntry::getEcoUserId, Collectors.summingInt(ActivityEntry::getPoints)));
        }

        List<RowTmp> sorted = new ArrayList<>();
        for (EcoUserProfile profile : profiles) {
            int points = pointsByUser.getOrDefault(profile.getEcoUserId(), 0);
            sorted.add(new RowTmp(profile, points));
        }
        sorted.sort(Comparator.comparingInt(RowTmp::points).reversed().thenComparing(r -> r.profile().getDisplayName()));

        List<ScoringDtos.RankingRowResponse> rows = new ArrayList<>();
        int previousPoints = Integer.MIN_VALUE;
        int rank = 0;
        for (int i = 0; i < sorted.size(); i++) {
            RowTmp row = sorted.get(i);
            if (row.points() != previousPoints) {
                rank = i + 1;
                previousPoints = row.points();
            }
            rows.add(new ScoringDtos.RankingRowResponse(
                    rank,
                    row.profile().getEcoUserId(),
                    row.profile().getDisplayName(),
                    row.points(),
                    toLevel(row.points()),
                    row.profile().getEcoUserId().equals(currentEcoUserId)
            ));
        }

        return new ScoringDtos.RankingTableResponse(scope, period == null ? PeriodType.TOTAL : period, OffsetDateTime.now(), rows);
    }

    private ScoringDtos.ActionDefinitionResponse toActionResponse(ActionDefinition action) {
        return new ScoringDtos.ActionDefinitionResponse(
                action.getActionDefinitionId(),
                action.getName(),
                action.getDescription(),
                action.getCategory(),
                action.getUnit(),
                action.getBasePoints(),
                action.isActive()
        );
    }

    private ScoringDtos.ActivityEntryResponse toActivityResponse(ActivityEntry entry) {
        return new ScoringDtos.ActivityEntryResponse(
                entry.getActivityEntryId(),
                entry.getActionDefinitionId(),
                entry.getActionName(),
                entry.getCategory(),
                entry.getQuantity(),
                entry.getUnit(),
                entry.getPoints(),
                entry.getTimestamp(),
                entry.getSource()
        );
    }

    private Level toLevel(int points) {
        if (points >= level3) {
            return Level.ALTBAUM;
        }
        if (points >= level2) {
            return Level.BAUM;
        }
        if (points >= level1) {
            return Level.JUNGBAUM;
        }
        return Level.SETZLING;
    }

    private record RowTmp(EcoUserProfile profile, int points) {
    }
}
