package at.htl.ecotrack.api;

import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.administration.domain.AppUserRepository;
import at.htl.ecotrack.scoring.domain.PointsLedger;
import at.htl.ecotrack.scoring.domain.PointsLedgerRepository;
import at.htl.ecotrack.shared.model.Level;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/eco-users")
public class EcoUserController {

    private final EcoUserProfileService profileService;
    private final PointsLedgerRepository pointsLedgerRepository;
    private final AppUserRepository appUserRepository;

    public EcoUserController(EcoUserProfileService profileService,
                             PointsLedgerRepository pointsLedgerRepository,
                             AppUserRepository appUserRepository) {
        this.profileService = profileService;
        this.pointsLedgerRepository = pointsLedgerRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/me")
    public EcoUserProfileResponse me(@AuthenticationPrincipal CurrentUser currentUser) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        return toResponse(profile, currentUser.role());
    }

    @GetMapping("/{ecoUserId}")
    public EcoUserProfileResponse byId(@PathVariable UUID ecoUserId) {
        EcoUserProfile profile = profileService.getByEcoUserId(ecoUserId);
        Role role = appUserRepository.findById(profile.getUserId())
                .map(user -> user.getRole())
                .orElse(Role.SCHUELER);
        return toResponse(profile, role);
    }

    private EcoUserProfileResponse toResponse(EcoUserProfile profile, Role role) {
        PointsLedger ledger = pointsLedgerRepository.findById(profile.getEcoUserId()).orElse(null);
        int points = ledger == null ? 0 : ledger.getTotalPoints();
        return new EcoUserProfileResponse(
                profile.getEcoUserId(),
                new Name(profile.getFirstName(), profile.getLastName(), profile.getDisplayName()),
                profile.getEmail(),
                profile.getClassId(),
                profile.getClassName(),
                profile.getSchoolId(),
                profile.getSchoolName(),
                points,
                toLevel(points),
                role
        );
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

    public record Name(String firstName, String lastName, String displayName) {
    }

    public record EcoUserProfileResponse(UUID ecoUserId,
                                         Name name,
                                         String email,
                                         UUID classId,
                                         String className,
                                         UUID schoolId,
                                         String schoolName,
                                         int totalPoints,
                                         Level level,
                                         Role role) {
    }
}
