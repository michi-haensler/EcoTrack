package at.htl.ecotrack.administration.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Konfigurationseigenschaften für den Keycloak Admin REST Client.
 * Werte werden aus application.yml unter dem Prefix {@code keycloak.admin}
 * gelesen.
 */
@ConfigurationProperties(prefix = "keycloak.admin")
@Component
public record KeycloakAdminProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret) {
}
