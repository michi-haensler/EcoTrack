package at.ecotrack.challenge.api;

import at.ecotrack.challenge.application.dto.ChallengeDto;
import at.ecotrack.challenge.application.service.ChallengeService;
import at.ecotrack.shared.valueobject.ClassId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementierung der Modul-Facade.
 */
@Service
@RequiredArgsConstructor
public class ChallengeModuleFacadeImpl implements ChallengeModuleFacade {

    private final ChallengeService challengeService;

    @Override
    public List<ChallengeDto> getActiveChallenges(ClassId classId) {
        return challengeService.getActiveChallengesForClass(classId);
    }

    @Override
    public ChallengeDto getChallenge(UUID challengeId) {
        return challengeService.getChallenge(challengeId);
    }
}
