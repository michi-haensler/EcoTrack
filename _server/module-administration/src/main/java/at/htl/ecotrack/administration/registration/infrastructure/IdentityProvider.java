package at.htl.ecotrack.administration.registration.infrastructure;

import java.util.UUID;

import at.htl.ecotrack.shared.model.Role;

/**
 * Port (Ausgehende Schnittstelle): Abstraktion für den Identity Provider.
 *
 * Verhindert direkte Abhängigkeit der Domäne von Keycloak-spezifischem Code
 * (Anti-Corruption Layer / Adapter Pattern).
 */
public interface IdentityProvider {

    /**
     * Legt einen neuen Benutzer im Identity Provider an.
     *
     * @param email     E-Mail-Adresse (wird auch als Username verwendet)
     * @param password  initiales Passwort
     * @param firstName Vorname
     * @param lastName  Nachname
     * @param role      zugewiesene Rolle
     * @return die externe Benutzer-UUID (z.B. Keycloak-ID)
     */
    UUID createUser(String email, String password, String firstName, String lastName, Role role);

    /**
     * Prüft, ob eine E-Mail-Adresse bereits im Identity Provider registriert ist.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return true, wenn die E-Mail bereits vergeben ist
     */
    boolean isEmailRegistered(String email);

    /**
     * Weist dem Benutzer eine Rolle im Identity Provider zu.
     * (Wird bei einer separaten Rollenzuweisung nach User-Anlage benötigt.)
     *
     * @param userId   externe Benutzer-UUID
     * @param roleName Name der Rolle (z.B. "SCHUELER", "LEHRER")
     */
    void assignRole(UUID userId, String roleName);

    /**
     * Sendet eine Verifikations-E-Mail an den Benutzer.
     *
     * @param userId externe Benutzer-UUID im Identity Provider
     */
    void sendVerificationEmail(UUID userId);
}
