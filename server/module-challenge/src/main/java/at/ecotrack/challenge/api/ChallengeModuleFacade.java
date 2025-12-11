package at.ecotrack.challenge.api;

import at.ecotrack.challenge.application.dto.ChallengeDto;
import at.ecotrack.shared.valueobject.ClassId;

import java.util.List;
import java.util.UUID;

/**
 * Öffentliche API des Challenge-Moduls.
 * Nur diese Schnittstelle darf von anderen Modulen verwendet werden.
 */
public interface ChallengeModuleFacade {

    /**
     * Gibt alle aktiven Challenges für eine Klasse zurück.
     */
    List<ChallengeDto> getActiveChallenges(ClassId classId);

    /**
     * Gibt eine Challenge nach ID zurück.
     */
    ChallengeDto getChallenge(UUID challengeId);
}
