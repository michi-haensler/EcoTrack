package at.ecotrack.administration.api;

import at.ecotrack.administration.dto.KeycloakUserDto;
import at.ecotrack.administration.dto.SchoolClassDto;
import at.ecotrack.shared.valueobject.ClassId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Öffentliche API des Administration-Moduls.
 * Nur diese Schnittstelle darf von anderen Modulen verwendet werden.
 */
public interface AdministrationModuleFacade {

    /**
     * Gibt eine Schulklasse nach ID zurück.
     */
    Optional<SchoolClassDto> getSchoolClass(ClassId classId);

    /**
     * Gibt einen Benutzer aus Keycloak zurück.
     */
    Optional<KeycloakUserDto> getUser(UUID userId);

    /**
     * Gibt alle Klassen eines Lehrers zurück.
     */
    List<SchoolClassDto> getClassesForTeacher(UUID teacherUserId);
}
