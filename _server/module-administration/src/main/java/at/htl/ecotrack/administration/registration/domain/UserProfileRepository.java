package at.htl.ecotrack.administration.registration.domain;

/**
 * Port (Ausgehende Schnittstelle): Abstraktion für die Persistierung von
 * UserProfile-Aggregaten.
 *
 * Entkoppelt die Domäne von der konkreten Persistierungstechnologie
 * (JPA/Hibernate).
 */
public interface UserProfileRepository {

    /**
     * Speichert ein UserProfile-Aggregat (inkl. AppUser und EcoUserProfile).
     *
     * @param userProfile das zu speichernde Profil
     * @return das gespeicherte Profil
     */
    UserProfile save(UserProfile userProfile);

    /**
     * Prüft, ob ein Profil mit der angegebenen E-Mail bereits existiert.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return true, wenn die E-Mail bereits vergeben ist
     */
    boolean existsByEmail(String email);
}
