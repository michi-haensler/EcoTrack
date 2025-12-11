package at.ecotrack.administration.dto;

import java.util.List;
import java.util.UUID;

/**
 * DTO für Benutzer aus Keycloak (ACL-transformiert).
 */
public record KeycloakUserDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        boolean enabled) {
}
