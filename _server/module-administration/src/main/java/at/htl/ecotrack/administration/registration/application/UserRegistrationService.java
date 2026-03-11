package at.htl.ecotrack.administration.registration.application;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.htl.ecotrack.administration.registration.domain.AllowedDomain;
import at.htl.ecotrack.administration.registration.domain.IdentityProvider;
import at.htl.ecotrack.administration.registration.domain.RegistrationService;
import at.htl.ecotrack.administration.registration.domain.UserProfile;
import at.htl.ecotrack.administration.registration.domain.UserProfileRepository;
import at.htl.ecotrack.administration.registration.infrastructure.RegistrationProperties;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;

/**
 * «ApplicationService» — Orchestriert den gesamten Self-Registration-Fluss.
 *
 * Ablauf:
 * 1. Domain-Check: Ist die E-Mail-Domain für die Registrierung erlaubt?
 * 2. Duplikat-Check im IdP (Keycloak)
 * 3. Duplikat-Check in der lokalen Datenbank
 * 4. Rollenbestimmung anhand der E-Mail-Domain
 * 5. Benutzer im Identity Provider anlegen
 * 6. UserProfile lokal persistieren
 * (Verifikations-E-Mail wird automatisch durch den IdP-Adapter versendet)
 */
@Service
public class UserRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);

    private final RegistrationService registrationService;
    private final IdentityProvider identityProvider;
    private final UserProfileRepository userProfileRepository;
    private final RegistrationProperties registrationProperties;

    public UserRegistrationService(RegistrationService registrationService,
            IdentityProvider identityProvider,
            UserProfileRepository userProfileRepository,
            RegistrationProperties registrationProperties) {
        this.registrationService = registrationService;
        this.identityProvider = identityProvider;
        this.userProfileRepository = userProfileRepository;
        this.registrationProperties = registrationProperties;
    }

    /**
     * Führt den vollständigen Registrierungsfluss aus.
     *
     * @param command enthält alle Registrierungsdaten
     * @return DTO mit userId, E-Mail und Bestätigungsnachricht
     * @throws ApiException bei Validierungsfehlern oder bereits vorhandener E-Mail
     */
    @Transactional
    public RegistrationDtos.RegistrationResponseDto registerUser(RegisterUserCommand command) {
        List<AllowedDomain> allowedDomains = buildAllowedDomains();

        // 1. Domain-Validierung
        if (!registrationService.isRegistrationAllowed(command.email(), allowedDomains)) {
            log.warn("Registrierungsversuch mit nicht erlaubter Domain: {}", command.email());
            throw new ApiException(HttpStatus.BAD_REQUEST, "DOMAIN_NOT_ALLOWED",
                    "Diese E-Mail-Domain ist nicht für die Registrierung zugelassen. "
                            + "Bitte verwende deine Schul-E-Mail-Adresse.");
        }

        // 2. Duplikat-Check im IdP
        if (identityProvider.isEmailRegistered(command.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMAIL_EXISTS",
                    "Diese E-Mail-Adresse ist bereits registriert.");
        }

        // 3. Duplikat-Check in lokaler DB
        if (userProfileRepository.existsByEmail(command.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMAIL_EXISTS",
                    "Diese E-Mail-Adresse ist bereits registriert.");
        }

        // 4. Rollenbestimmung
        Role role = registrationService.determineInitialRole(command.email());
        log.info("Registrierung für {}: automatisch bestimmte Rolle = {}", command.email(), role);

        // 5. Benutzer im IdP anlegen (inkl. Verifikations-E-Mail)
        UUID keycloakUserId = identityProvider.createUser(
                command.email(), command.password(),
                command.firstName(), command.lastName(), role);

        // 6. Lokales UserProfile persistieren
        UserProfile profile = UserProfile.create(
                keycloakUserId, command.email(),
                command.firstName(), command.lastName(),
                role, command.classId());
        userProfileRepository.save(profile);

        log.info("Registrierung erfolgreich: userId={}, email={}, role={}", keycloakUserId, command.email(), role);

        return new RegistrationDtos.RegistrationResponseDto(
                keycloakUserId,
                command.email(),
                "Registrierung erfolgreich. Bitte prüfe dein E-Mail-Postfach und bestätige deine Adresse.");
    }

    /**
     * Prüft, ob eine E-Mail-Adresse noch für die Registrierung verfügbar ist.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return DTO mit Verfügbarkeitsstatus
     */
    public RegistrationDtos.EmailAvailabilityDto checkEmailAvailability(String email) {
        boolean inIdP = identityProvider.isEmailRegistered(email);
        boolean inDb = userProfileRepository.existsByEmail(email);
        boolean available = !inIdP && !inDb;
        return new RegistrationDtos.EmailAvailabilityDto(email, available);
    }

    private List<AllowedDomain> buildAllowedDomains() {
        return registrationProperties.allowedDomains().stream()
                .map(AllowedDomain::new)
                .toList();
    }
}
