package at.htl.ecotrack.administration.registration.domain;

import java.util.UUID;

import at.htl.ecotrack.shared.model.Role;

/**
 * «AggregateRoot» — Domänenmodell für ein Benutzerprofil nach der
 * Registrierung.
 *
 * UserProfile kapselt die Invarianten bei der Erstellung und stellt sicher,
 * dass kein UserProfile in einem ungültigen Zustand existiert.
 * Die Persistierung erfolgt über das {@link UserProfileRepository}-Port.
 */
public class UserProfile {

    private UUID externalUserId;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean active;
    private UUID classId;

    private UserProfile() {
    }

    /**
     * Factory-Methode: Erstellt ein gültiges UserProfile und prüft alle
     * Invarianten.
     *
     * @param externalUserId Keycloak-UUID des Benutzers
     * @param email          verifizierte E-Mail-Adresse (eindeutig)
     * @param firstName      Vorname
     * @param lastName       Nachname
     * @param role           initial zugewiesene Rolle
     * @param classId        Klassen-UUID (nur für SCHUELER erforderlich, sonst
     *                       null)
     * @return ein gültiges UserProfile-Objekt
     */
    public static UserProfile create(UUID externalUserId, String email,
            String firstName, String lastName, Role role, UUID classId) {
        if (externalUserId == null) {
            throw new IllegalArgumentException("externalUserId darf nicht null sein");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-Mail darf nicht leer sein");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein");
        }
        if (role == null) {
            throw new IllegalArgumentException("Rolle darf nicht null sein");
        }

        UserProfile profile = new UserProfile();
        profile.externalUserId = externalUserId;
        profile.email = email.toLowerCase().trim();
        profile.firstName = firstName.trim();
        profile.lastName = lastName.trim();
        profile.role = role;
        profile.active = true;
        profile.classId = classId;
        return profile;
    }

    public UUID getExternalUserId() {
        return externalUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getClassId() {
        return classId;
    }
}
