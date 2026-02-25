package at.htl.ecotrack.administration.application;

import at.htl.ecotrack.administration.domain.AppUser;
import at.htl.ecotrack.administration.domain.AppUserRepository;
import at.htl.ecotrack.administration.domain.PasswordResetToken;
import at.htl.ecotrack.administration.domain.PasswordResetTokenRepository;
import at.htl.ecotrack.administration.domain.RefreshToken;
import at.htl.ecotrack.administration.domain.RefreshTokenRepository;
import at.htl.ecotrack.administration.domain.SchoolClass;
import at.htl.ecotrack.administration.domain.SchoolClassRepository;
import at.htl.ecotrack.shared.security.CurrentUser;
import at.htl.ecotrack.administration.security.JwtTokenService;
import at.htl.ecotrack.shared.error.ApiException;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.model.UserStatus;
import at.htl.ecotrack.userprofile.application.EcoUserProfileService;
import at.htl.ecotrack.userprofile.domain.EcoUserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AppUserRepository appUserRepository;
    private final SchoolClassRepository classRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EcoUserProfileService profileService;
    private final JwtTokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AppUserRepository appUserRepository,
                       SchoolClassRepository classRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EcoUserProfileService profileService,
                       JwtTokenService tokenService) {
        this.appUserRepository = appUserRepository;
        this.classRepository = classRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.profileService = profileService;
        this.tokenService = tokenService;
    }

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
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "CLASS_NOT_FOUND", "Klasse nicht gefunden"));
        }

        AppUser user = new AppUser();
        user.setUserId(UUID.randomUUID());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        user.setMustChangePassword(request.role() != Role.SCHUELER);
        user.setFailedLoginAttempts(0);
        AppUser savedUser = appUserRepository.save(user);

        EcoUserProfile profile = profileService.createProfile(
                savedUser.getUserId(),
                savedUser.getEmail(),
                request.firstName(),
                request.lastName(),
                request.role(),
                schoolClass == null ? null : schoolClass.getClassId(),
                schoolClass == null ? null : schoolClass.getName(),
                schoolClass == null ? null : schoolClass.getSchoolId(),
                schoolClass == null ? null : schoolClass.getSchoolName()
        );

        return issueTokens(savedUser, profile);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, Role targetRole) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Ungültige Login-Daten"));

        if (targetRole != null && user.getRole() != targetRole) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_MISMATCH", "Falscher Login-Endpunkt für diese Rolle");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USER_DISABLED", "Benutzer ist deaktiviert");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "ACCOUNT_LOCKED", "Konto temporär gesperrt");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(OffsetDateTime.now().plusMinutes(5));
                user.setFailedLoginAttempts(0);
            }
            appUserRepository.save(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Ungültige Login-Daten");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        if ((user.getRole() == Role.ADMIN || user.getRole() == Role.LEHRER) && user.isMustChangePassword()) {
            appUserRepository.save(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "PASSWORD_CHANGE_REQUIRED", "Passwortwechsel erforderlich");
        }

        appUserRepository.save(user);
        EcoUserProfile profile = profileService.getByUserId(user.getUserId());
        return issueTokens(user, profile);
    }

    @Transactional
    public void logout(AuthDtos.LogoutRequest request) {
        refreshTokenRepository.deleteById(request.refreshToken());
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void requestPasswordReset(AuthDtos.PasswordResetRequest request) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .orElse(null);
        if (user == null) {
            return;
        }

        long recentRequests = passwordResetTokenRepository.countByUserIdAndCreatedAtAfter(
                user.getUserId(),
                OffsetDateTime.now().minusMinutes(15)
        );
        if (recentRequests >= 3) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "RESET_RATE_LIMIT", "Zu viele Reset-Anfragen");
        }

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(generateToken());
        token.setUserId(user.getUserId());
        token.setExpiresAt(OffsetDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(token);
    }

    public AuthDtos.UserInfo getCurrentUserInfo(CurrentUser currentUser) {
        EcoUserProfile profile = profileService.getByUserId(currentUser.userId());
        return new AuthDtos.UserInfo(
                currentUser.userId(),
                profile.getEcoUserId(),
                currentUser.email(),
                profile.getFirstName(),
                profile.getLastName(),
                currentUser.role()
        );
    }

    public AuthDtos.UserPage getUsers(int page, int size, Role role) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<AppUser> result = role == null ? appUserRepository.findAll(pageable) : appUserRepository.findByRole(role, pageable);
        List<AuthDtos.UserAdminView> content = result.getContent().stream()
                .map(user -> new AuthDtos.UserAdminView(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus(),
                        user.isMustChangePassword()
                ))
                .toList();
        return new AuthDtos.UserPage(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional
    public AuthDtos.ClassResponse createClass(AuthDtos.CreateClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassId(UUID.randomUUID());
        schoolClass.setName(request.name());
        schoolClass.setSchoolId(request.schoolId());
        schoolClass.setSchoolName(request.schoolName());
        SchoolClass saved = classRepository.save(schoolClass);
        return new AuthDtos.ClassResponse(saved.getClassId(), saved.getName(), saved.getSchoolId(), saved.getSchoolName(), 0, List.of());
    }

    public List<AuthDtos.ClassResponse> getClasses() {
        return classRepository.findAll().stream()
                .map(c -> new AuthDtos.ClassResponse(
                        c.getClassId(),
                        c.getName(),
                        c.getSchoolId(),
                        c.getSchoolName(),
                        profileService.getByClassId(c.getClassId()).size(),
                        List.of()
                ))
                .toList();
    }

    private AuthDtos.AuthResponse issueTokens(AppUser user, EcoUserProfile profile) {
        String accessToken = tokenService.createAccessToken(user.getUserId(), user.getEmail(), user.getRole());
        String refreshToken = generateToken();

        RefreshToken refresh = new RefreshToken();
        refresh.setToken(refreshToken);
        refresh.setUserId(user.getUserId());
        refresh.setExpiresAt(OffsetDateTime.now().plus(14, ChronoUnit.DAYS));
        refreshTokenRepository.save(refresh);

        return new AuthDtos.AuthResponse(
                accessToken,
                refreshToken,
                ChronoUnit.SECONDS.between(OffsetDateTime.now(), OffsetDateTime.now().plusHours(1)),
                new AuthDtos.UserInfo(
                        user.getUserId(),
                        profile.getEcoUserId(),
                        user.getEmail(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        user.getRole()
                )
        );
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
