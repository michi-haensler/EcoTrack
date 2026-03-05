package at.htl.ecotrack.administration.security;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;

/**
 * Kapselt alle Operationen gegen die Keycloak Admin REST API.
 *
 * <p>
 * Verwendet den {@code ecotrack-backend} Service-Account (Client Credentials
 * Grant)
 * um Admin-Tokens zu beziehen. Jede Methode holt sich frisch ein Admin-Token
 * (kurzlebige Gültigkeit, kein lokales Caching nötig für diese Schulgröße).
 */
@Service
public class KeycloakAdminService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

    private final RestClient restClient;
    private final KeycloakAdminProperties props;

    public KeycloakAdminService(KeycloakAdminProperties props) {
        this.props = props;
        this.restClient = RestClient.create();
    }

    // ---------------------------------------------------------------------------
    // Benutzer-Management
    // ---------------------------------------------------------------------------

    /**
     * Legt einen neuen Benutzer in Keycloak an, setzt sein Passwort und weist die
     * Rolle zu.
     *
     * @return die Keycloak-UUID des neu erstellten Benutzers
     * @throws ApiException wenn die E-Mail bereits in Keycloak existiert oder ein
     *                      anderer Fehler auftritt
     */
    public UUID createUser(String email, String password, String firstName, String lastName, Role role) {
        String adminToken = obtainAdminToken();
        String usersUrl = adminUsersUrl();

        Map<String, Object> userRepresentation = Map.of(
                "username", email.toLowerCase(),
                "email", email.toLowerCase(),
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false)));

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(usersUrl)
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepresentation)
                    .retrieve()
                    .toBodilessEntity();

            URI location = response.getHeaders().getLocation();
            if (location == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "KEYCLOAK_ERROR",
                        "Keine Location-Header in Keycloak-Antwort");
            }

            // Location: .../admin/realms/ecotrack/users/{userId}
            String path = location.getPath();
            UUID keycloakUserId = UUID.fromString(path.substring(path.lastIndexOf('/') + 1));

            assignRealmRole(adminToken, keycloakUserId, role);
            return keycloakUserId;

        } catch (HttpClientErrorException ex) {
            log.error("Keycloak Benutzeranlage fehlgeschlagen für {}: {}", email, ex.getResponseBodyAsString());
            if (ex.getStatusCode().value() == 409) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "EMAIL_EXISTS",
                        "E-Mail ist bereits in Keycloak registriert");
            }
            throw new ApiException(HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR", "Keycloak nicht erreichbar");
        }
    }

    /**
     * Löscht einen Benutzer aus Keycloak (Kompensations-Transaktion bei
     * Registrierungsfehlern).
     */
    public void deleteUser(UUID keycloakUserId) {
        String adminToken = obtainAdminToken();
        try {
            restClient.delete()
                    .uri(adminUsersUrl() + "/" + keycloakUserId)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak Benutzerlöschung fehlgeschlagen für {}: {}", keycloakUserId, ex.getMessage());
        }
    }

    /**
     * Löscht alle aktiven Sessions eines Benutzers in Keycloak (Logout-All).
     */
    public void logoutAllSessions(UUID keycloakUserId) {
        String adminToken = obtainAdminToken();
        try {
            restClient.post()
                    .uri(adminUsersUrl() + "/" + keycloakUserId + "/logout")
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak Logout-All fehlgeschlagen für {}: {}", keycloakUserId, ex.getMessage());
        }
    }

    /**
     * Schickt eine Password-Reset-E-Mail über Keycloak an den Benutzer.
     */
    public void sendPasswordResetEmail(String email) {
        String adminToken = obtainAdminToken();
        UUID userId = findUserIdByEmail(adminToken, email);
        if (userId == null) {
            // E-Mail nicht in Keycloak gefunden – still ignorieren (Security best practice)
            return;
        }
        try {
            restClient.put()
                    .uri(adminUsersUrl() + "/" + userId + "/execute-actions-email")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of("UPDATE_PASSWORD"))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak Password-Reset-E-Mail fehlgeschlagen für {}: {}", email, ex.getMessage());
        }
    }

    // ---------------------------------------------------------------------------
    // Rollen-Management
    // ---------------------------------------------------------------------------

    private void assignRealmRole(String adminToken, UUID userId, Role role) {
        Map<String, Object> roleRep = getRealmRole(adminToken, role.name());
        restClient.post()
                .uri(adminUsersUrl() + "/" + userId + "/role-mappings/realm")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(roleRep))
                .retrieve()
                .toBodilessEntity();
    }

    private Map<String, Object> getRealmRole(String adminToken, String roleName) {
        String url = props.serverUrl() + "/admin/realms/" + props.realm() + "/roles/" + roleName;
        @SuppressWarnings("unchecked")
        Map<String, Object> role = restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .body(Map.class);
        if (role == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "KEYCLOAK_ERROR",
                    "Rolle nicht gefunden: " + roleName);
        }
        return role;
    }

    // ---------------------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------------------

    /**
     * Bezieht einen Admin-Token über den Client-Credentials-Flow.
     */
    private String obtainAdminToken() {
        String tokenUrl = props.serverUrl() + "/realms/" + props.realm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", props.clientId());
        body.add("client_secret", props.clientSecret());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("access_token") == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "KEYCLOAK_ERROR", "Kein Admin-Token erhalten");
            }
            return (String) response.get("access_token");
        } catch (HttpClientErrorException ex) {
            log.error("Keycloak Admin-Token-Fehler: {}", ex.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR", "Keycloak Admin nicht erreichbar");
        }
    }

    private UUID findUserIdByEmail(String adminToken, String email) {
        String url = props.serverUrl() + "/admin/realms/" + props.realm() + "/users?email=" + email + "&exact=true";
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(List.class);
            if (users == null || users.isEmpty())
                return null;
            return UUID.fromString((String) users.get(0).get("id"));
        } catch (Exception ex) {
            log.warn("Keycloak User-Suche fehlgeschlagen für {}: {}", email, ex.getMessage());
            return null;
        }
    }

    private String adminUsersUrl() {
        return props.serverUrl() + "/admin/realms/" + props.realm() + "/users";
    }
}
