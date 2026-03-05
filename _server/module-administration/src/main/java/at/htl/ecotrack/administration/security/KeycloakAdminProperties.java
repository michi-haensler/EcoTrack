package at.htl.ecotrack.administration.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Konfigurationseigenschaften für den Keycloak Admin REST Client.
 * Werte werden aus application.yml unter dem Prefix {@code keycloak.admin}
 * gelesen.
 */
@ConfigurationProperties(prefix = "keycloak.admin")
public record KeycloakAdminProperties(
                String serverUrl,
                String realm,
                String clientId,
                String clientSecret) {
}
