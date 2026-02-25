package at.htl.ecotrack.challenge.application;

import at.htl.ecotrack.administration.domain.SchoolClass;
import at.htl.ecotrack.administration.domain.SchoolClassRepository;
import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.challenge.domain.Challenge;
import at.htl.ecotrack.challenge.domain.ChallengeRepository;
import at.htl.ecotrack.scoring.application.ScoringService;
import at.htl.ecotrack.scoring.domain.ActivityEntry;
import at.htl.ecotrack.scoring.domain.ActivityEntryRepository;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.ChallengeStatus;
import at.htl.ecotrack.shared.model.GoalUnit;
import at.htl.ecotrack.shared.model.Level;
import at.htl.ecotrack.shared.model.PeriodType;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;
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

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SchoolClassRepository classRepository;
    private final EcoUserProfileService profileService;
    private final ActivityEntryRepository activityEntryRepository;
    private final ScoringService scoringService;

    public ChallengeService(ChallengeRepository challengeRepository,
                            SchoolClassRepository classRepository,
                            EcoUserProfileService profileService,
                            ActivityEntryRepository activityEntryRepository,
                            ScoringService scoringService) {
        this.challengeRepository = challengeRepository;
        this.classRepository = classRepository;
        this.profileService = profileService;
        this.activityEntryRepository = activityEntryRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public ChallengeDtos.ChallengeResponse createChallenge(CurrentUser currentUser, ChallengeDtos.CreateChallengeRequest request) {
        if (currentUser.role() != Role.LEHRER && currentUser.role() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Nur Lehrer/Admin dürfen Challenges anlegen");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PERIOD", "Enddatum darf nicht vor Startdatum liegen");
        }

        SchoolClass schoolClass = classRepository.findById(request.classId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "CLASS_NOT_FOUND", "Klasse nicht gefunden"));

        challengeRepository.findByClassIdAndTitleIgnoreCase(request.classId(), request.title())
                .ifPresent(existing -> {
                    throw new ApiException(HttpStatus.CONFLICT, "DUPLICATE_CHALLENGE", "Challenge mit Titel existiert bereits");
                });

        Challenge challenge = new Challenge();
        challenge.setChallengeId(UUID.randomUUID());
        challenge.setTitle(request.title());
        challenge.setDescription(request.description());
        challenge.setGoalValue(request.goalValue());
        challenge.setGoalUnit(request.goalUnit());
        challenge.setStartDate(request.startDate());
        challenge.setEndDate(request.endDate());
        challenge.setClassId(request.classId());
        challenge.setClassName(schoolClass.getName());
        challenge.setCreatedBy(currentUser.userId());
        challenge.setStatus(computeStatus(challenge));

        Challenge saved = challengeRepository.save(challenge);
        return toChallengeResponse(saved);
    }

    public List<ChallengeDtos.ChallengeResponse> getMyChallenges(CurrentUser currentUser, ChallengeStatus status) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        if (profile.getClassId() == null) {
            return List.of();
        }
        List<Challenge> challenges = challengeRepository.findByClassId(profile.getClassId());

        return challenges.stream().map(challenge -> {
            challenge.setStatus(computeStatus(challenge));
            return toChallengeResponse(challenge);
        }).filter(challenge -> status == null || challenge.status() == status).toList();
    }

    public ChallengeDtos.ChallengeDetailResponse getChallengeDetail(UUID challengeId) {
        Challenge challenge = loadChallenge(challengeId);
        ChallengeDtos.ChallengeResponse base = toChallengeResponse(challenge);
        return new ChallengeDtos.ChallengeDetailResponse(
                base.challengeId(),
                base.title(),
                base.description(),
                base.status(),
                base.goal(),
                base.startDate(),
                base.endDate(),
                base.classId(),
                base.className(),
                base.createdBy(),
                getChallengeProgress(challengeId),
                profileService.getByClassId(challenge.getClassId()).size()
        );
    }

    public ChallengeDtos.ChallengeProgressResponse getChallengeProgress(UUID challengeId) {
        Challenge challenge = loadChallenge(challengeId);
        List<EcoUserProfile> profiles = profileService.getByClassId(challenge.getClassId());
        List<UUID> ecoUserIds = profiles.stream().map(EcoUserProfile::getEcoUserId).toList();

        double currentValue;
        if (challenge.getGoalUnit() == GoalUnit.POINTS) {
            currentValue = activityEntryRepository
                    .findByEcoUserIdInAndActivityDateBetween(ecoUserIds, challenge.getStartDate(), challenge.getEndDate())
                    .stream()
                    .mapToInt(ActivityEntry::getPoints)
                    .sum();
        } else {
            currentValue = activityEntryRepository
                    .findByEcoUserIdInAndActivityDateBetween(ecoUserIds, challenge.getStartDate(), challenge.getEndDate())
                    .size();
        }

        double percentage = challenge.getGoalValue() <= 0 ? 0d : Math.min(100d, (currentValue / challenge.getGoalValue()) * 100d);
        boolean completed = currentValue >= challenge.getGoalValue();

        return new ChallengeDtos.ChallengeProgressResponse(
                challenge.getChallengeId(),
                currentValue,
                challenge.getGoalValue(),
                percentage,
                completed
        );
    }

    public ChallengeDtos.ClassSummaryResponse getClassDashboard(CurrentUser currentUser, UUID classId, PeriodType period) {
        if (currentUser.role() != Role.LEHRER && currentUser.role() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Keine Berechtigung");
        }

        SchoolClass schoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CLASS_NOT_FOUND", "Klasse nicht gefunden"));

        List<EcoUserProfile> profiles = profileService.getByClassId(classId);
        List<UUID> ecoIds = profiles.stream().map(EcoUserProfile::getEcoUserId).toList();
        LocalDate end = LocalDate.now();
        LocalDate start = switch (period == null ? PeriodType.TOTAL : period) {
            case TOTAL -> LocalDate.of(1970, 1, 1);
            case MONTH -> end.minusDays(30);
            case WEEK -> end.minusDays(7);
        };

        List<ActivityEntry> entries = ecoIds.isEmpty()
                ? List.of()
                : activityEntryRepository.findByEcoUserIdInAndActivityDateBetween(ecoIds, start, end);

        int totalPoints = entries.stream().mapToInt(ActivityEntry::getPoints).sum();
        int totalActivities = entries.size();
        int activeStudents = (int) entries.stream().map(ActivityEntry::getEcoUserId).distinct().count();

        List<ChallengeDtos.ChallengeSummary> challengeSummaries = challengeRepository.findByClassId(classId)
                .stream()
                .map(challenge -> {
                    ChallengeDtos.ChallengeProgressResponse progress = getChallengeProgress(challenge.getChallengeId());
                    int remaining = (int) ChronoUnit.DAYS.between(LocalDate.now(), challenge.getEndDate());
                    return new ChallengeDtos.ChallengeSummary(
                            challenge.getChallengeId(),
                            challenge.getTitle(),
                            computeStatus(challenge),
                            progress.percentage(),
                            Math.max(remaining, 0)
                    );
                })
                .filter(ch -> ch.status() == ChallengeStatus.ACTIVE)
                .toList();

        Map<UUID, List<ActivityEntry>> byAction = entries.stream().collect(Collectors.groupingBy(ActivityEntry::getActionDefinitionId));
        List<ChallengeDtos.ActionStats> topActions = byAction.values().stream()
                .map(list -> new ChallengeDtos.ActionStats(
                        list.getFirst().getActionDefinitionId(),
                        list.getFirst().getActionName(),
                        list.size(),
                        list.stream().mapToInt(ActivityEntry::getPoints).sum()
                ))
                .sorted(Comparator.comparingInt(ChallengeDtos.ActionStats::totalPoints).reversed())
                .limit(3)
                .toList();

        List<ChallengeDtos.StudentSummary> topStudents = buildTopStudents(profiles, start, end);

        return new ChallengeDtos.ClassSummaryResponse(
                classId,
                schoolClass.getName(),
                period == null ? PeriodType.TOTAL : period,
                totalPoints,
                totalActivities,
                activeStudents,
                profiles.size(),
                challengeSummaries,
                topActions,
                topStudents
        );
    }

    private List<ChallengeDtos.StudentSummary> buildTopStudents(List<EcoUserProfile> profiles, LocalDate start, LocalDate end) {
        List<StudentTmp> rows = new ArrayList<>();
        for (EcoUserProfile profile : profiles) {
            int points = scoringService.getUserPointsInRange(profile.getEcoUserId(), start, end);
            rows.add(new StudentTmp(profile, points));
        }

        rows.sort(Comparator.comparingInt(StudentTmp::points).reversed());
        List<ChallengeDtos.StudentSummary> result = new ArrayList<>();
        int rank = 0;
        int last = Integer.MIN_VALUE;
        for (int i = 0; i < rows.size(); i++) {
            StudentTmp tmp = rows.get(i);
            if (tmp.points() != last) {
                rank = i + 1;
                last = tmp.points();
            }
            result.add(new ChallengeDtos.StudentSummary(
                    tmp.profile().getEcoUserId(),
                    tmp.profile().getDisplayName(),
                    tmp.points(),
                    rank,
                    toLevel(tmp.points())
            ));
        }
        return result.stream().limit(5).toList();
    }

    private Level toLevel(int points) {
        if (points >= 500) {
            return Level.ALTBAUM;
        }
        if (points >= 250) {
            return Level.BAUM;
        }
        if (points >= 100) {
            return Level.JUNGBAUM;
        }
        return Level.SETZLING;
    }

    private Challenge loadChallenge(UUID challengeId) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challenge.setStatus(computeStatus(challenge));
                    return challenge;
                })
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CHALLENGE_NOT_FOUND", "Challenge nicht gefunden"));
    }

    private ChallengeStatus computeStatus(Challenge challenge) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(challenge.getEndDate())) {
            return ChallengeStatus.CLOSED;
        }
        if (!now.isBefore(challenge.getStartDate())) {
            return ChallengeStatus.ACTIVE;
        }
        return ChallengeStatus.DRAFT;
    }

    private ChallengeDtos.ChallengeResponse toChallengeResponse(Challenge challenge) {
        return new ChallengeDtos.ChallengeResponse(
                challenge.getChallengeId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getStatus(),
                new ChallengeDtos.ChallengeGoalResponse(challenge.getGoalValue(), challenge.getGoalUnit()),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getClassId(),
                challenge.getClassName(),
                challenge.getCreatedBy()
        );
    }

    private record StudentTmp(EcoUserProfile profile, int points) {
    }
}
