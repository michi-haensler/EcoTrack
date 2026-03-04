package at.htl.ecotrack.administration.application;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.htl.ecotrack.administration.domain.AppUser;
import at.htl.ecotrack.administration.domain.AppUserRepository;
import at.htl.ecotrack.administration.domain.PasswordResetToken;
import at.htl.ecotrack.administration.domain.RefreshToken;
import at.htl.ecotrack.administration.domain.SchoolClass;
import at.htl.ecotrack.administration.domain.SchoolClassRepository;
import at.htl.ecotrack.administration.security.KeycloakAdminService;
import at.htl.ecotrack.administration.security.KeycloakTokenService;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.model.UserStatus;
import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;

/**
 * Application-Service für Authentifizierung und Benutzerverwaltung.
 *
 * <p>
 * Passwörter und Tokens werden vollständig von Keycloak verwaltet.
 * Der lokale {@code app_users}-Eintrag dient nur noch zur Statusverwaltung
 * und Profilverknüpfung. Die {@code user_id} entspricht der Keycloak-UUID.
 */
@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final SchoolClassRepository classRepository;
    private final EcoUserProfileService profileService;
    private final KeycloakAdminService keycloakAdminService;
    private final KeycloakTokenService keycloakTokenService;

    public AuthService(AppUserRepository appUserRepository,
            SchoolClassRepository classRepository,
            EcoUserProfileService profileService,
            KeycloakAdminService keycloakAdminService,
            KeycloakTokenService keycloakTokenService) {
        this.appUserRepository = appUserRepository;
        this.classRepository = classRepository;
        this.profileService = profileService;
        this.keycloakAdminService = keycloakAdminService;
        this.keycloakTokenService = keycloakTokenService;
    }

    // ---------------------------------------------------------------------------
    // Registrierung
    // ---------------------------------------------------------------------------

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (appUserRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "EMAIL_EXISTS", "E-Mail ist bereits registriert");
        }
        if (request.role() == Role.SCHUELER && request.classId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CLASS_REQUIRED", "classId ist für Schüler erforderlich");
        }

        SchoolClass schoolClass = null;
        if (request.classId() != null) {
            schoolClass = classRepository.findById(request.classId())
                    .orElseThrow(
                            () -> new ApiException(HttpStatus.BAD_REQUEST, "CLASS_NOT_FOUND", "Klasse nicht gefunden"));
        }

        // 1. Benutzer in Keycloak anlegen → ergibt die Keycloak-UUID
        UUID keycloakUserId = null;
        try {
            keycloakUserId = keycloakAdminService.createUser(
                    request.email(), request.password(),
                    request.firstName(), request.lastName(), request.role());

            // 2. Lokalen AppUser mit Keycloak-UUID anlegen (kein Passwort-Hash!)
            AppUser user = new AppUser();
            user.setUserId(keycloakUserId);
            user.setEmail(request.email().toLowerCase());
            user.setPasswordHash(null);
            user.setRole(request.role());
            user.setStatus(UserStatus.ACTIVE);
            user.setMustChangePassword(false);
            user.setFailedLoginAttempts(0);
            AppUser savedUser = appUserRepository.save(user);

            // 3. Benutzerprofil erstellen
            final SchoolClass sc = schoolClass;
            EcoUserProfile profile = profileService.createProfile(
                    savedUser.getUserId(),
                    savedUser.getEmail(),
                    request.firstName(),
                    request.lastName(),
                    request.role(),
                    sc == null ? null : sc.getClassId(),
                    sc == null ? null : sc.getName(),
                    sc == null ? null : sc.getSchoolId(),
                    sc == null ? null : sc.getSchoolName());

            // 4. Direkt einloggen und Tokens zurückgeben
            KeycloakTokenService.KeycloakTokenResponse tokens = keycloakTokenService.login(request.email(),
                    request.password());

            return toAuthResponse(tokens, savedUser, profile);

        } catch (Exception ex) {
            // Kompensations-Transaktion: Keycloak-User löschen wenn lokale DB fehlschlägt
            if (keycloakUserId != null) {
                try {
                    keycloakAdminService.deleteUser(keycloakUserId);
                } catch (Exception ignored) {
                }
            }
            throw ex;
        }
    }

    // ---------------------------------------------------------------------------
    // Login / Logout
    // ---------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, Role targetRole) {
        // Lokalen Benutzer für Statusprüfung laden
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Ungültige Login-Daten"));

        if (targetRole != null && user.getRole() != targetRole) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_MISMATCH", "Falscher Login-Endpunkt für diese Rolle");
        }
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USER_DISABLED", "Benutzer ist deaktiviert");
        }

        // Keycloak übernimmt die Passwortprüfung und Brute-Force-Schutz
        KeycloakTokenService.KeycloakTokenResponse tokens = keycloakTokenService.login(request.email(),
                request.password());

        EcoUserProfile profile = profileService.getByUserId(user.getUserId());
        return toAuthResponse(tokens, user, profile);
    }

    public void logout(AuthDtos.LogoutRequest request) {
        keycloakTokenService.revokeRefreshToken(request.refreshToken());
    }

    public void logoutAll(UUID userId) {
        keycloakAdminService.logoutAllSessions(userId);
    }

    // ---------------------------------------------------------------------------
    // Passwort-Reset
    // ---------------------------------------------------------------------------

    public void requestPasswordReset(AuthDtos.PasswordResetRequest request) {
        // Keycloak sendet die Reset-E-Mail direkt; kein lokaler Token nötig
        keycloakAdminService.sendPasswordResetEmail(request.email());
    }

    // ---------------------------------------------------------------------------
    // Benutzerinfo
    // ---------------------------------------------------------------------------

    public AuthDtos.UserInfo getCurrentUserInfo(CurrentUser currentUser) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        return new AuthDtos.UserInfo(
                currentUser.userId(),
                profile.getEcoUserId(),
                currentUser.email(),
                profile.getFirstName(),
                profile.getLastName(),
                currentUser.role());
    }

    // ---------------------------------------------------------------------------
    // Admin-Benutzerverwaltung
    // ---------------------------------------------------------------------------

    public AuthDtos.UserPage getUsers(int page, int size, Role role) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<AppUser> result = role == null
                ? appUserRepository.findAll(pageable)
                : appUserRepository.findByRole(role, pageable);

        List<AuthDtos.UserAdminView> content = result.getContent().stream()
                .map(user -> new AuthDtos.UserAdminView(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus(),
                        user.isMustChangePassword()))
                .toList();
        return new AuthDtos.UserPage(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public AuthDtos.ClassResponse createClass(AuthDtos.CreateClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassId(UUID.randomUUID());
        schoolClass.setName(request.name());
        schoolClass.setSchoolId(request.schoolId());
        schoolClass.setSchoolName(request.schoolName());
        SchoolClass saved = classRepository.save(schoolClass);
        return new AuthDtos.ClassResponse(
                saved.getClassId(), saved.getName(), saved.getSchoolId(), saved.getSchoolName(), 0, List.of());
    }

    public List<AuthDtos.ClassResponse> getClasses() {
        return classRepository.findAll().stream()
                .map(c -> new AuthDtos.ClassResponse(
                        c.getClassId(),
                        c.getName(),
                        c.getSchoolId(),
                        c.getSchoolName(),
                        profileService.getByClassId(c.getClassId()).size(),
                        List.of()))
                .toList();
    }

    // ---------------------------------------------------------------------------
    // Hilfsmethoden
    // ---------------------------------------------------------------------------

    private AuthDtos.AuthResponse toAuthResponse(KeycloakTokenService.KeycloakTokenResponse tokens,
            AppUser user,
            EcoUserProfile profile) {
        return new AuthDtos.AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                new AuthDtos.UserInfo(
                        user.getUserId(),
                        profile.getEcoUserId(),
                        user.getEmail(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        user.getRole()));
    }

}
