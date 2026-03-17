package at.htl.ecotrack.challenge.api;

import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.challenge.application.ChallengeDtos;
import at.htl.ecotrack.challenge.application.ChallengeService;
import at.htl.ecotrack.shared.model.ChallengeStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public List<ChallengeDtos.ChallengeResponse> getMyChallenges(@AuthenticationPrincipal CurrentUser currentUser,
                                                                 @RequestParam(name = "status", required = false) ChallengeStatus status) {
        return challengeService.getMyChallenges(currentUser, status);
    }

    @PostMapping
    public ResponseEntity<ChallengeDtos.ChallengeResponse> createChallenge(@AuthenticationPrincipal CurrentUser currentUser,
                                                                           @Valid @RequestBody ChallengeDtos.CreateChallengeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.createChallenge(currentUser, request));
    }

    @GetMapping("/{challengeId}")
    public ChallengeDtos.ChallengeDetailResponse getChallenge(@PathVariable UUID challengeId) {
        return challengeService.getChallengeDetail(challengeId);
    }

    @GetMapping("/{challengeId}/progress")
    public ChallengeDtos.ChallengeProgressResponse getProgress(@PathVariable UUID challengeId) {
        return challengeService.getChallengeProgress(challengeId);
    }
}
