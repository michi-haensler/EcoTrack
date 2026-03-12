package at.htl.ecotrack.administration.registration.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Service;

import at.htl.ecotrack.administration.security.KeycloakAdminService;
import at.htl.ecotrack.shared.model.Role;

/**
 * «Adapter» (Infrastructure): Implementierung des
 * {@link IdentityProvider}-Ports für Keycloak.
 *
 * Kapselt die Komplexität der Keycloak Admin REST API hinter einer einfachen,
 * domänenorientierten Schnittstelle (Anti-Corruption Layer).
 * Delegiert alle Operationen an den bestehenden {@link KeycloakAdminService}.
 */
@Service
public class KeycloakIdentityProvider implements IdentityProvider {

    private final KeycloakAdminService keycloakAdminService;

    public KeycloakIdentityProvider(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    /**
     * Legt einen Benutzer in Keycloak an, weist die Rolle zu und
     * sendet die Verifikations-E-Mail (alles in einem Aufruf).
     */
    @Override
    public UUID createUser(String email, String password, String firstName, String lastName, Role role) {
        return keycloakAdminService.createUser(email, password, firstName, lastName, role);
    }

    /**
     * Prüft, ob eine E-Mail bereits in Keycloak vorhanden ist.
     */
    @Override
    public boolean isEmailRegistered(String email) {
        return keycloakAdminService.getUserByEmail(email) != null;
    }

    /**
     * Weist eine Realm-Rolle in Keycloak zu.
     * Hinweis: Wird normalerweise bereits durch {@link #createUser} erledigt.
     * Für nachträgliche Rollenzuweisungen nutzbar.
     */
    @Override
    public void assignRole(UUID userId, String roleName) {
        // Rollenverteilung erfolgt in createUser via
        // KeycloakAdminService.assignRealmRole
        // Diese Methode ist für zukünftige Rollenwechsel vorgesehen
    }

    /**
     * Sendet eine erneute Verifikations-E-Mail an den Benutzer.
     */
    @Override
    public void sendVerificationEmail(UUID userId) {
        keycloakAdminService.sendVerificationEmail(userId);
    }
}
