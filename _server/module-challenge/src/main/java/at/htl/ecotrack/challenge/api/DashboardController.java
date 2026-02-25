package at.htl.ecotrack.challenge.api;

import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.challenge.application.ChallengeDtos;
import at.htl.ecotrack.challenge.application.ChallengeService;
import at.htl.ecotrack.shared.model.PeriodType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ChallengeService challengeService;

    public DashboardController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/class/{classId}")
    public ChallengeDtos.ClassSummaryResponse getClassDashboard(@AuthenticationPrincipal CurrentUser currentUser,
                                                                @PathVariable UUID classId,
                                                                @RequestParam(required = false) PeriodType period) {
        return challengeService.getClassDashboard(currentUser, classId, period);
    }
}
