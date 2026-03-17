package at.htl.ecotrack.scoring.api;

import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.scoring.application.ScoringDtos;
import at.htl.ecotrack.scoring.application.ScoringService;
import at.htl.ecotrack.shared.model.Category;
import at.htl.ecotrack.shared.model.PeriodType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @GetMapping("/activities/catalog")
    public List<ScoringDtos.ActionDefinitionResponse> catalog(@RequestParam(name = "category", required = false) Category category) {
        return scoringService.getCatalog(category);
    }

    @PostMapping("/activities")
    public ResponseEntity<ScoringDtos.ActivityEntryResponse> createActivity(@AuthenticationPrincipal CurrentUser currentUser,
                                                                            @Valid @RequestBody ScoringDtos.CreateActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scoringService.createActivity(currentUser, request));
    }

    @GetMapping("/activities")
    public ScoringDtos.ActivityPageResponse activities(@AuthenticationPrincipal CurrentUser currentUser,
                                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                                       @RequestParam(name = "size", defaultValue = "20") int size,
                                                       @RequestParam(name = "category", required = false) Category category) {
        return scoringService.getMyActivities(currentUser, page, size, category);
    }

    @GetMapping("/progress/points")
    public ScoringDtos.PointsLedgerResponse points(@AuthenticationPrincipal CurrentUser currentUser) {
        return scoringService.getMyPoints(currentUser);
    }

    @GetMapping("/progress")
    public ScoringDtos.ProgressSnapshotResponse progress(@AuthenticationPrincipal CurrentUser currentUser) {
        return scoringService.getMyProgress(currentUser);
    }

    @GetMapping("/leaderboard/class")
    public ScoringDtos.RankingTableResponse classLeaderboard(@AuthenticationPrincipal CurrentUser currentUser,
                                                             @RequestParam(name = "period", required = false) PeriodType period) {
        return scoringService.getClassLeaderboard(currentUser, period);
    }

    @GetMapping("/leaderboard/school")
    public ScoringDtos.RankingTableResponse schoolLeaderboard(@AuthenticationPrincipal CurrentUser currentUser,
                                                              @RequestParam(name = "period", required = false) PeriodType period) {
        return scoringService.getSchoolLeaderboard(currentUser, period);
    }
}
