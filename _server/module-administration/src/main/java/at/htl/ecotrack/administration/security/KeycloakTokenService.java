package at.htl.ecotrack.administration.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import at.htl.ecotrack.shared.error.ApiException;

/**
 * Proxiert Token-Requests (Login, Logout) an den Keycloak Authorization Server.
 *
 * <ul>
 * <li>Login: Resource Owner Password Credentials Grant</li>
 * <li>Logout: Token Revocation (RFC 7009)</li>
 * </ul>
 */
@Service
public class KeycloakTokenService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakTokenService.class);

    private final RestClient restClient;
    private final KeycloakAdminProperties props;

    public KeycloakTokenService(KeycloakAdminProperties props) {
        this.props = props;
        this.restClient = RestClient.create();
    }

    /**
     * Führt einen ROPC-Login gegen Keycloak durch.
     *
     * @return {@link KeycloakTokenResponse} mit access_token, refresh_token und
     *         expires_in
     * @throws ApiException bei ungültigen Credentials oder gesperrtem Account
     */
    public KeycloakTokenResponse login(String email, String password) {
        String tokenUrl = props.serverUrl() + "/realms/" + props.realm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", props.clientId());
        body.add("client_secret", props.clientSecret());
        body.add("username", email);
        body.add("password", password);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_ERROR", "Keycloak-Antwort leer");
            }

            return new KeycloakTokenResponse(
                    (String) response.get("access_token"),
                    (String) response.get("refresh_token"),
                    response.get("expires_in") instanceof Number n ? n.longValue() : 900L);
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak Login fehlgeschlagen für {}: {} {}", email, ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            if (ex.getStatusCode().value() == 401) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Ungültige Login-Daten");
            }
            throw new ApiException(HttpStatus.BAD_GATEWAY, "KEYCLOAK_ERROR", "Authentifizierung nicht verfügbar");
        }
    }

    /**
     * Widerruft ein Refresh-Token in Keycloak (Logout).
     */
    public void revokeRefreshToken(String refreshToken) {
        String revokeUrl = props.serverUrl() + "/realms/" + props.realm() + "/protocol/openid-connect/revoke";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", refreshToken);
        body.add("token_type_hint", "refresh_token");
        body.add("client_id", props.clientId());
        body.add("client_secret", props.clientSecret());

        try {
            restClient.post()
                    .uri(revokeUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            log.warn("Keycloak Revoke fehlgeschlagen: {}", ex.getMessage());
        }
    }

    /**
     * Token-Antwort von Keycloak.
     */
    public record KeycloakTokenResponse(String accessToken, String refreshToken, long expiresIn) {
    }
}
