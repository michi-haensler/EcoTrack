package at.htl.ecotrack.administration.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.htl.ecotrack.administration.domain.AppUser;
import at.htl.ecotrack.administration.domain.AppUserRepository;
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

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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
    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest request) {
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
        // createUser() setzt emailVerified=false und sendet Verifikations-E-Mail
        UUID keycloakUserId = null;
        try {
            keycloakUserId = keycloakAdminService.createUser(
                    request.email(), request.password(),
                    request.firstName(), request.lastName(), request.role());

            // 2. Lokalen AppUser mit Keycloak-UUID anlegen
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
            profileService.createProfile(
                    savedUser.getUserId(),
                    savedUser.getEmail(),
                    request.firstName(),
                    request.lastName(),
                    request.role(),
                    sc == null ? null : sc.getClassId(),
                    sc == null ? null : sc.getName(),
                    sc == null ? null : sc.getSchoolId(),
                    sc == null ? null : sc.getSchoolName());

            // 4. Kein Auto-Login mehr — User muss zuerst E-Mail verifizieren
            return new AuthDtos.RegisterResponse(
                    savedUser.getUserId(),
                    savedUser.getEmail(),
                    "Registrierung erfolgreich. Bitte prüfe dein E-Mail-Postfach und bestätige deine E-Mail-Adresse.");

        } catch (Exception ex) {
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

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, Role targetRole) {
        // 1. Keycloak-Benutzerdaten abrufen für Vorabprüfungen
        Map<String, Object> kcUser = keycloakAdminService.getUserByEmail(request.email());

        if (kcUser != null) {
            // E-Mail-Verifikation prüfen (Keycloak ist Source of Truth)
            Boolean emailVerified = (Boolean) kcUser.get("emailVerified");
            if (emailVerified == null || !emailVerified) {
                throw new ApiException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED",
                        "E-Mail-Adresse wurde noch nicht verifiziert. Bitte prüfe dein Postfach.");
            }

            // Passwort-Änderungszwang prüfen (aus Keycloak requiredActions)
            @SuppressWarnings("unchecked")
            List<String> requiredActions = (List<String>) kcUser.getOrDefault("requiredActions", List.of());
            if (requiredActions.contains("UPDATE_PASSWORD")) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "PASSWORD_CHANGE_REQUIRED",
                        "Das Passwort muss vor dem ersten Login geändert werden");
            }
        }

        // 2. Keycloak übernimmt die Passwortprüfung (Single Source of Truth)
        KeycloakTokenService.KeycloakTokenResponse tokens;
        try {
            tokens = keycloakTokenService.login(request.email(), request.password());
        } catch (ApiException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                    "Ungültige Login-Daten");
        }

        // 3. Lokalen Benutzer laden — oder per JIT-Provisioning automatisch anlegen
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .orElseGet(() -> provisionLocalUser(request.email()));

        // 4. Admin-Dashboard: Nur ADMIN und LEHRER erlaubt
        if (targetRole == Role.ADMIN) {
            if (user.getRole() == Role.SCHUELER) {
                throw new ApiException(HttpStatus.FORBIDDEN, "INSUFFICIENT_ROLE",
                        "Schüler haben keinen Zugang zum Admin-Dashboard. Bitte die Mobile-App verwenden.");
            }
        }
        // Mobile-Login: Nur Schüler erlaubt
        if (targetRole == Role.SCHUELER && user.getRole() != Role.SCHUELER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_MISMATCH",
                    "Falscher Login-Endpunkt für diese Rolle");
        }

        // 5. Statusprüfungen
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USER_DISABLED", "Benutzer ist deaktiviert");
        }

        EcoUserProfile profile = profileService.getByUserId(user.getUserId());
        return toAuthResponse(tokens, user, profile);
    }

    /**
     * JIT (Just-In-Time) Provisioning: Legt einen lokalen AppUser + Profil an,
     * wenn der Benutzer in Keycloak existiert, aber noch nicht in der lokalen DB.
     * So funktionieren auch Benutzer, die über die Keycloak Admin-Konsole angelegt
     * wurden.
     */
    private AppUser provisionLocalUser(String email) {
        Map<String, Object> kcUser = keycloakAdminService.getUserByEmail(email);
        if (kcUser == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Ungültige Login-Daten");
        }

        UUID keycloakUserId = UUID.fromString((String) kcUser.get("id"));
        String firstName = (String) kcUser.getOrDefault("firstName", "");
        String lastName = (String) kcUser.getOrDefault("lastName", "");

        // Rolle aus Keycloak-Realm-Rollen ableiten
        Role role = resolveRoleFromKeycloak(keycloakUserId);

        AppUser user = new AppUser();
        user.setUserId(keycloakUserId);
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(null);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setMustChangePassword(false);
        user.setFailedLoginAttempts(0);
        AppUser savedUser = appUserRepository.save(user);

        // Profil anlegen
        profileService.createProfile(
                savedUser.getUserId(), savedUser.getEmail(),
                firstName, lastName, role,
                null, null, null, null);

        log.info("JIT-Provisioning: Lokaler Benutzer für {} angelegt (Keycloak-ID: {}, Rolle: {})",
                email, keycloakUserId, role);

        return savedUser;
    }

    /**
     * Leitet die App-Rolle aus den Keycloak-Realm-Rollen ab.
     * Priorität: ADMIN > LEHRER > SCHUELER
     */
    private Role resolveRoleFromKeycloak(UUID keycloakUserId) {
        List<String> roles = keycloakAdminService.getUserRealmRoles(keycloakUserId);
        if (roles.contains("ADMIN"))
            return Role.ADMIN;
        if (roles.contains("LEHRER"))
            return Role.LEHRER;
        if (roles.contains("SCHUELER"))
            return Role.SCHUELER;
        // Fallback: Wenn keine App-Rolle zugewiesen ist, Default-Rolle
        return Role.SCHUELER;
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

    /**
     * Ändert das Passwort eines Benutzers.
     * Verifiziert zuerst das aktuelle Passwort via ROPC, setzt dann das neue
     * Passwort über die Keycloak Admin API und entfernt UPDATE_PASSWORD.
     */
    public AuthDtos.PasswordChangeResponse changePassword(AuthDtos.PasswordChangeRequest request) {
        // 1. Aktuelles Passwort verifizieren via ROPC
        try {
            keycloakTokenService.login(request.email(), request.currentPassword());
        } catch (ApiException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                    "Das aktuelle Passwort ist falsch");
        }

        // 2. Keycloak-Benutzer ermitteln
        Map<String, Object> kcUser = keycloakAdminService.getUserByEmail(request.email());
        if (kcUser == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Benutzer nicht gefunden");
        }
        UUID keycloakUserId = UUID.fromString((String) kcUser.get("id"));

        // 3. Neues Passwort setzen + UPDATE_PASSWORD entfernen
        keycloakAdminService.resetUserPassword(keycloakUserId, request.newPassword());

        log.info("Passwort erfolgreich geändert für Benutzer {}", request.email());
        return new AuthDtos.PasswordChangeResponse("Passwort erfolgreich geändert. Du kannst dich jetzt anmelden.");
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
