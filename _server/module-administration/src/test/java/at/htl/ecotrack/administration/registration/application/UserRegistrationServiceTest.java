package at.htl.ecotrack.administration.registration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import at.htl.ecotrack.administration.registration.domain.IdentityProvider;
import at.htl.ecotrack.administration.registration.domain.RegistrationService;
import at.htl.ecotrack.administration.registration.domain.UserProfile;
import at.htl.ecotrack.administration.registration.domain.UserProfileRepository;
import at.htl.ecotrack.administration.registration.infrastructure.RegistrationProperties;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;

/**
 * Unit Tests für {@link UserRegistrationService}.
 *
 * Alle Abhängigkeiten werden gemockt – kein Spring-Kontext nötig.
 */
@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private IdentityProvider identityProvider;

    @Mock
    private UserProfileRepository userProfileRepository;

    private UserRegistrationService userRegistrationService;

    private final UUID keycloakId = UUID.randomUUID();
    private final List<String> allowedDomainsConfig = List.of("htl-leoben.at", "schueler.htl-leoben.at",
            "lehrer.htl-leoben.at");

    @BeforeEach
    void setUp() {
        RegistrationProperties properties = new RegistrationProperties(allowedDomainsConfig);
        userRegistrationService = new UserRegistrationService(
                registrationService, identityProvider, userProfileRepository, properties);
    }

    // --- Erfolgspfad ---

    @Test
    void should_registerUser_when_allConditionsAreMet() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "max@htl-leoben.at", "password123", "Max", "Mustermann", null);

        when(registrationService.isRegistrationAllowed(eq("max@htl-leoben.at"), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered("max@htl-leoben.at")).thenReturn(false);
        when(userProfileRepository.existsByEmail("max@htl-leoben.at")).thenReturn(false);
        when(registrationService.determineInitialRole("max@htl-leoben.at")).thenReturn(Role.SCHUELER);
        when(identityProvider.createUser(anyString(), anyString(), anyString(), anyString(), any(Role.class)))
                .thenReturn(keycloakId);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        RegistrationDtos.RegistrationResponseDto result = userRegistrationService.registerUser(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(keycloakId);
        assertThat(result.email()).isEqualTo("max@htl-leoben.at");
        assertThat(result.message()).contains("Registrierung erfolgreich");

        // Verify: UserProfile wurde mit korrekten Daten angelegt
        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(profileCaptor.capture());
        UserProfile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getEmail()).isEqualTo("max@htl-leoben.at");
        assertThat(savedProfile.getRole()).isEqualTo(Role.SCHUELER);
        assertThat(savedProfile.getExternalUserId()).isEqualTo(keycloakId);
    }

    // --- Domain-Validierung ---

    @Test
    void should_throwException_when_emailDomainNotAllowed() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "max@gmail.com", "password123", "Max", "Mustermann", null);

        when(registrationService.isRegistrationAllowed(eq("max@gmail.com"), anyList())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userRegistrationService.registerUser(command))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getCode())
                .isEqualTo("DOMAIN_NOT_ALLOWED");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), any());
        verify(userProfileRepository, never()).save(any());
    }

    // --- Duplikat-Prüfung im IdP ---

    @Test
    void should_throwException_when_emailAlreadyRegisteredInIdP() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "ana@htl-leoben.at", "password123", "Ana", "Huber", null);

        when(registrationService.isRegistrationAllowed(anyString(), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered("ana@htl-leoben.at")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userRegistrationService.registerUser(command))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getCode())
                .isEqualTo("EMAIL_EXISTS");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), any());
    }

    // --- Duplikat-Prüfung in lokaler DB ---

    @Test
    void should_throwException_when_emailAlreadyExistsInLocalDb() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "peter@htl-leoben.at", "password123", "Peter", "Klein", null);

        when(registrationService.isRegistrationAllowed(anyString(), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered("peter@htl-leoben.at")).thenReturn(false);
        when(userProfileRepository.existsByEmail("peter@htl-leoben.at")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userRegistrationService.registerUser(command))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getCode())
                .isEqualTo("EMAIL_EXISTS");

        verify(identityProvider, never()).createUser(anyString(), anyString(), anyString(), anyString(), any());
    }

    // --- E-Mail-Verfügbarkeitsprüfung ---

    @Test
    void should_returnAvailable_when_emailIsNotRegisteredAnywhere() {
        // Arrange
        when(identityProvider.isEmailRegistered("new@htl-leoben.at")).thenReturn(false);
        when(userProfileRepository.existsByEmail("new@htl-leoben.at")).thenReturn(false);

        // Act
        RegistrationDtos.EmailAvailabilityDto result = userRegistrationService
                .checkEmailAvailability("new@htl-leoben.at");

        // Assert
        assertThat(result.available()).isTrue();
        assertThat(result.email()).isEqualTo("new@htl-leoben.at");
    }

    @Test
    void should_returnNotAvailable_when_emailExistsInIdP() {
        // Arrange
        when(identityProvider.isEmailRegistered("taken@htl-leoben.at")).thenReturn(true);

        // Act
        RegistrationDtos.EmailAvailabilityDto result = userRegistrationService
                .checkEmailAvailability("taken@htl-leoben.at");

        // Assert
        assertThat(result.available()).isFalse();
    }

    // --- Rollenbestimmung ---

    @Test
    void should_assignLehrerRole_when_emailContainsLehrerDomain() {
        // Arrange
        RegisterUserCommand command = new RegisterUserCommand(
                "frau.huber@lehrer.htl-leoben.at", "password123", "Eva", "Huber", null);

        when(registrationService.isRegistrationAllowed(anyString(), anyList())).thenReturn(true);
        when(identityProvider.isEmailRegistered(anyString())).thenReturn(false);
        when(userProfileRepository.existsByEmail(anyString())).thenReturn(false);
        when(registrationService.determineInitialRole("frau.huber@lehrer.htl-leoben.at")).thenReturn(Role.LEHRER);
        when(identityProvider.createUser(anyString(), anyString(), anyString(), anyString(), eq(Role.LEHRER)))
                .thenReturn(keycloakId);
        when(userProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        userRegistrationService.registerUser(command);

        // Assert: createUser wurde mit LEHRER aufgerufen
        verify(identityProvider).createUser(anyString(), anyString(), anyString(), anyString(), eq(Role.LEHRER));
    }
}
