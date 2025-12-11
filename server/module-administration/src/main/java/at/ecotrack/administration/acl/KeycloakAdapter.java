package at.ecotrack.administration.acl;

import at.ecotrack.administration.dto.KeycloakUserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer: Abstraktion für Keycloak-Zugriffe.
 * Übersetzt Keycloak-Datenmodell in unsere Domain-Sprache.
 */
public interface KeycloakAdapter {

    /**
     * Gibt alle Benutzer zurück.
     */
    List<KeycloakUserDto> getAllUsers();

    /**
     * Gibt alle Benutzer mit einer bestimmten Rolle zurück.
     */
    List<KeycloakUserDto> getUsersByRole(String roleName);

    /**
     * Gibt einen Benutzer nach ID zurück.
     */
    Optional<KeycloakUserDto> getUserById(UUID userId);

    /**
     * Gibt einen Benutzer nach Username zurück.
     */
    Optional<KeycloakUserDto> getUserByUsername(String username);

    /**
     * Gibt alle Lehrer zurück.
     */
    default List<KeycloakUserDto> getTeachers() {
        return getUsersByRole("teacher");
    }

    /**
     * Gibt alle Schüler zurück.
     */
    default List<KeycloakUserDto> getStudents() {
        return getUsersByRole("student");
    }

    /**
     * Gibt alle Admins zurück.
     */
    default List<KeycloakUserDto> getAdmins() {
        return getUsersByRole("admin");
    }
}
