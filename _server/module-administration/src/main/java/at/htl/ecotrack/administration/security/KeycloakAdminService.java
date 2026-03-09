package at.htl.ecotrack.administration.security;

import java.net.URI;
import java.util.HashMap;
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

        Map<String, Object> userRepresentation = new HashMap<>();
        userRepresentation.put("username", email.toLowerCase());
        userRepresentation.put("email", email.toLowerCase());
        userRepresentation.put("firstName", firstName);
        userRepresentation.put("lastName", lastName);
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", false);
        userRepresentation.put("requiredActions", List.of("VERIFY_EMAIL"));
        userRepresentation.put("credentials", List.of(Map.of(
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

            // Verifikations-Email senden
            sendVerificationEmail(adminToken, keycloakUserId);

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
     * Gibt die vollständigen Keycloak-Benutzerdaten für eine E-Mail zurück.
     *
     * @return Map mit Keycloak-Feldern (id, firstName, lastName, email, enabled,
     *         requiredActions, …)
     *         oder {@code null} wenn kein Benutzer existiert
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserByEmail(String email) {
        String adminToken = obtainAdminToken();
        String url = adminUsersUrl() + "?email=" + email.toLowerCase() + "&exact=true";
        try {
            List<Map<String, Object>> users = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(List.class);
            if (users == null || users.isEmpty())
                return null;
            return users.get(0);
        } catch (Exception ex) {
            log.warn("Keycloak User-Suche fehlgeschlagen für {}: {}", email, ex.getMessage());
            return null;
        }
    }

    /**
     * Gibt die Realm-Rollen eines Keycloak-Benutzers zurück.
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserRealmRoles(UUID keycloakUserId) {
        String adminToken = obtainAdminToken();
        String url = adminUsersUrl() + "/" + keycloakUserId + "/role-mappings/realm";
        try {
            List<Map<String, Object>> roles = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(List.class);
            if (roles == null)
                return List.of();
            return roles.stream()
                    .map(r -> (String) r.get("name"))
                    .toList();
        } catch (Exception ex) {
            log.warn("Keycloak Rollen-Abfrage fehlgeschlagen für {}: {}", keycloakUserId, ex.getMessage());
            return List.of();
        }
    }

    /**
     * Schickt eine E-Mail-Verifikationsaufforderung – holt sich selbst ein
     * Admin-Token.
     * Wird vom IdentityProvider-Adapter genutzt.
     */
    public void sendVerificationEmail(UUID keycloakUserId) {
        sendVerificationEmail(obtainAdminToken(), keycloakUserId);
    }

    /**
     * Schickt eine E-Mail-Verifikationsaufforderung an einen Keycloak-Benutzer.
     */
    public void sendVerificationEmail(String adminToken, UUID keycloakUserId) {
        try {
            restClient.put()
                    .uri(adminUsersUrl() + "/" + keycloakUserId + "/execute-actions-email")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of("VERIFY_EMAIL"))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Verifikations-E-Mail gesendet an Keycloak-User {}", keycloakUserId);
        } catch (HttpClientErrorException ex) {
            log.warn("Verifikations-E-Mail fehlgeschlagen für {}: {}", keycloakUserId, ex.getMessage());
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

    /**
     * Setzt das Passwort eines Benutzers über die Keycloak Admin API und
     * entfernt die {@code UPDATE_PASSWORD} Required-Action, falls vorhanden.
     *
     * @param keycloakUserId Keycloak-UUID des Benutzers
     * @param newPassword    das neue Passwort
     */
    public void resetUserPassword(UUID keycloakUserId, String newPassword) {
        String adminToken = obtainAdminToken();
        String userUrl = adminUsersUrl() + "/" + keycloakUserId;

        // 1. Neues Passwort setzen via reset-password Endpoint
        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false);
        try {
            restClient.put()
                    .uri(userUrl + "/reset-password")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(credential)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.error("Keycloak Passwort-Reset fehlgeschlagen für {}: {}", keycloakUserId,
                    ex.getResponseBodyAsString());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR",
                    "Passwort konnte nicht in Keycloak gesetzt werden");
        }

        // 2. UPDATE_PASSWORD aus requiredActions entfernen
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> userRep = restClient.get()
                    .uri(userUrl)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .body(Map.class);

            if (userRep != null) {
                @SuppressWarnings("unchecked")
                List<String> actions = (List<String>) userRep.getOrDefault("requiredActions", List.of());
                List<String> filtered = actions.stream()
                        .filter(a -> !"UPDATE_PASSWORD".equals(a))
                        .toList();
                userRep.put("requiredActions", filtered);

                restClient.put()
                        .uri(userUrl)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userRep)
                        .retrieve()
                        .toBodilessEntity();
            }
        } catch (HttpClientErrorException ex) {
            log.warn("UPDATE_PASSWORD Required-Action konnte nicht entfernt werden für {}: {}",
                    keycloakUserId, ex.getMessage());
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
