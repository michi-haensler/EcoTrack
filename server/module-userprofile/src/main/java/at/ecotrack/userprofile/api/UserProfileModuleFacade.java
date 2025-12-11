package at.ecotrack.userprofile.api;

import at.ecotrack.shared.valueobject.ClassId;
import at.ecotrack.shared.valueobject.EcoUserId;
import at.ecotrack.userprofile.dto.EcoUserDto;
import at.ecotrack.userprofile.dto.LeaderboardEntryDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Öffentliche API des UserProfile-Moduls.
 * Nur diese Schnittstelle darf von anderen Modulen verwendet werden.
 */
public interface UserProfileModuleFacade {

    /**
     * Gibt einen EcoUser nach ID zurück.
     */
    Optional<EcoUserDto> getEcoUser(EcoUserId ecoUserId);

    /**
     * Gibt einen EcoUser nach User-ID zurück.
     */
    Optional<EcoUserDto> getEcoUserByUserId(UUID userId);

    /**
     * Gibt das Klassen-Leaderboard zurück.
     */
    List<LeaderboardEntryDto> getClassLeaderboard(ClassId classId, int limit);
}
