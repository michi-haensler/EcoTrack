package at.ecotrack.administration.acl;

import at.ecotrack.administration.dto.KeycloakUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementierung des Keycloak-Adapters mit WebClient.
 * ACL: Übersetzt Keycloak REST API in unser Domain-Modell.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdapterImpl implements KeycloakAdapter {

    private final WebClient.Builder webClientBuilder;

    @Value("${keycloak.admin.url:http://localhost:8180}")
    private String keycloakUrl;

    @Value("${keycloak.admin.realm:ecotrack}")
    private String realm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin.client-secret:}")
    private String clientSecret;

    @Override
    public List<KeycloakUserDto> getAllUsers() {
        try {
            String token = getAdminToken();

            List<Map<String, Object>> keycloakUsers = webClientBuilder.build()
                    .get()
                    .uri(keycloakUrl + "/admin/realms/" + realm + "/users")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (keycloakUsers == null) {
                return Collections.emptyList();
            }

            return keycloakUsers.stream()
                    .map(this::mapToKeycloakUserDto)
                    .toList();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Keycloak-Benutzer", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<KeycloakUserDto> getUsersByRole(String roleName) {
        try {
            String token = getAdminToken();

            List<Map<String, Object>> keycloakUsers = webClientBuilder.build()
                    .get()
                    .uri(keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName + "/users")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (keycloakUsers == null) {
                return Collections.emptyList();
            }

            return keycloakUsers.stream()
                    .map(user -> mapToKeycloakUserDtoWithRole(user, roleName))
                    .toList();
        } catch (Exception e) {
            log.error("Fehler beim Abrufen der Keycloak-Benutzer für Rolle: " + roleName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<KeycloakUserDto> getUserById(UUID userId) {
        try {
            String token = getAdminToken();

            Map<String, Object> keycloakUser = webClientBuilder.build()
                    .get()
                    .uri(keycloakUrl + "/admin/realms/" + realm + "/users/" + userId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if (keycloakUser == null) {
                return Optional.empty();
            }

            return Optional.of(mapToKeycloakUserDto(keycloakUser));
        } catch (Exception e) {
            log.error("Fehler beim Abrufen des Keycloak-Benutzers: " + userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<KeycloakUserDto> getUserByUsername(String username) {
        try {
            String token = getAdminToken();

            List<Map<String, Object>> keycloakUsers = webClientBuilder.build()
                    .get()
                    .uri(keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username + "&exact=true")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    })
                    .block();

            if (keycloakUsers == null || keycloakUsers.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(mapToKeycloakUserDto(keycloakUsers.get(0)));
        } catch (Exception e) {
            log.error("Fehler beim Abrufen des Keycloak-Benutzers: " + username, e);
            return Optional.empty();
        }
    }

    /**
     * Holt ein Admin-Token vom Keycloak-Server.
     */
    private String getAdminToken() {
        Map<String, Object> response = webClientBuilder.build()
                .post()
                .uri(keycloakUrl + "/realms/master/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("Konnte kein Admin-Token von Keycloak erhalten");
        }

        return (String) response.get("access_token");
    }

    /**
     * ACL-Transformation: Keycloak Map → KeycloakUserDto.
     */
    private KeycloakUserDto mapToKeycloakUserDto(Map<String, Object> keycloakUser) {
        return new KeycloakUserDto(
                UUID.fromString((String) keycloakUser.get("id")),
                (String) keycloakUser.get("username"),
                (String) keycloakUser.get("email"),
                (String) keycloakUser.get("firstName"),
                (String) keycloakUser.get("lastName"),
                extractRoles(keycloakUser),
                Boolean.TRUE.equals(keycloakUser.get("enabled")));
    }

    private KeycloakUserDto mapToKeycloakUserDtoWithRole(Map<String, Object> keycloakUser, String role) {
        return new KeycloakUserDto(
                UUID.fromString((String) keycloakUser.get("id")),
                (String) keycloakUser.get("username"),
                (String) keycloakUser.get("email"),
                (String) keycloakUser.get("firstName"),
                (String) keycloakUser.get("lastName"),
                List.of(role),
                Boolean.TRUE.equals(keycloakUser.get("enabled")));
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Map<String, Object> keycloakUser) {
        Object realmAccess = keycloakUser.get("realmRoles");
        if (realmAccess instanceof List) {
            return ((List<String>) realmAccess);
        }
        return Collections.emptyList();
    }
}
