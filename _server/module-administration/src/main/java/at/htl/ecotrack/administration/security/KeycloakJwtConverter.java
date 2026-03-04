package at.htl.ecotrack.administration.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.security.CurrentUser;

/**
 * Konvertiert einen Keycloak-JWT in ein Spring-Security
 * {@link AbstractAuthenticationToken}.
 *
 * <p>
 * Extrahiert aus den Token-Claims:
 * <ul>
 * <li>{@code sub} → interne {@link UUID} des Benutzers (= Keycloak-UUID)</li>
 * <li>{@code email} → E-Mail des Benutzers</li>
 * <li>{@code realm_access.roles} → die höchste Rolle (ADMIN > LEHRER >
 * SCHUELER)</li>
 * </ul>
 * Das resultierende Principal ist eine {@link CurrentUser}-Instanz.
 */
@Component
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        Role role = extractHighestRole(jwt);

        CurrentUser principal = new CurrentUser(userId, email, role);
        Collection<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name()));

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    /**
     * Ermittelt die höchste Rolle aus {@code realm_access.roles}.
     * Priorität: ADMIN > LEHRER (inkl. TEACHER für Kompatibilität) > SCHUELER.
     */
    private Role extractHighestRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return Role.SCHUELER;
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null || roles.isEmpty()) {
            return Role.SCHUELER;
        }

        if (roles.contains("ADMIN"))
            return Role.ADMIN;
        if (roles.contains("LEHRER") || roles.contains("TEACHER"))
            return Role.LEHRER;
        return Role.SCHUELER;
    }
}
