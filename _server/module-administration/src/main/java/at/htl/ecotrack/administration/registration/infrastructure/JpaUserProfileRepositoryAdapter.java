package at.htl.ecotrack.administration.registration.infrastructure;

import org.springframework.stereotype.Repository;

import at.htl.ecotrack.administration.domain.AppUser;
import at.htl.ecotrack.administration.domain.AppUserRepository;
import at.htl.ecotrack.administration.registration.domain.UserProfile;
import at.htl.ecotrack.administration.registration.domain.UserProfileRepository;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.model.UserStatus;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;

/**
 * «Adapter» (Infrastructure): JPA-Implementierung des
 * {@link UserProfileRepository}-Ports.
 *
 * Übersetzt das Domänen-Aggregat {@link UserProfile} in die bestehenden
 * JPA-Entitäten {@code AppUser} und {@code EcoUserProfile} und delegiert
 * die Persistierung an die jeweiligen Repositories.
 *
 * Damit bleibt die Domäne vollständig unabhängig von JPA/Hibernate.
 */
@Repository
public class JpaUserProfileRepositoryAdapter implements UserProfileRepository {

    private final AppUserRepository appUserRepository;
    private final EcoUserProfileService ecoUserProfileService;

    public JpaUserProfileRepositoryAdapter(AppUserRepository appUserRepository,
            EcoUserProfileService ecoUserProfileService) {
        this.appUserRepository = appUserRepository;
        this.ecoUserProfileService = ecoUserProfileService;
    }

    /**
     * Speichert das UserProfile, indem sowohl ein {@code AppUser}
     * als auch ein {@code EcoUserProfile} angelegt werden.
     */
    @Override
    public UserProfile save(UserProfile userProfile) {
        // AppUser für Statusverwaltung und Authentifizierung anlegen
        AppUser appUser = new AppUser();
        appUser.setUserId(userProfile.getExternalUserId());
        appUser.setEmail(userProfile.getEmail());
        appUser.setPasswordHash(null); // Passwort wird von Keycloak verwaltet
        appUser.setRole(userProfile.getRole());
        appUser.setStatus(UserStatus.ACTIVE);
        appUser.setMustChangePassword(false);
        appUser.setFailedLoginAttempts(0);
        appUserRepository.save(appUser);

        // Erweitertes EcoUserProfile für Scoring & Challenges anlegen
        ecoUserProfileService.createProfile(
                userProfile.getExternalUserId(),
                userProfile.getEmail(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getRole(),
                userProfile.getRole() == Role.SCHUELER ? userProfile.getClassId() : null,
                null, // className wird später über ClassRepository befüllt
                null, // schoolId
                null // schoolName
        );

        return userProfile;
    }

    /**
     * Prüft ob die E-Mail bereits lokal registriert ist (case-insensitive).
     */
    @Override
    public boolean existsByEmail(String email) {
        return appUserRepository.findByEmailIgnoreCase(email).isPresent();
    }
}
