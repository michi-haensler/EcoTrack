package at.htl.ecotrack.administration.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.htl.ecotrack.administration.application.AuthDtos;
import at.htl.ecotrack.administration.application.AuthService;
import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.security.CurrentUser;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthDtos.RegisterResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/auth/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request, null);
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody(required = false) AuthDtos.LogoutRequest request) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            authService.logout(request);
        } else {
            authService.logoutAll(currentUser.userId());
        }
    }

    @GetMapping("/users/me")
    public AuthDtos.UserInfo me(@AuthenticationPrincipal CurrentUser currentUser) {
        return authService.getCurrentUserInfo(currentUser);
    }

    @PostMapping("/auth/password/reset-request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestReset(@Valid @RequestBody AuthDtos.PasswordResetRequest request) {
        authService.requestPasswordReset(request);
    }

    @PostMapping("/auth/password/change")
    public AuthDtos.PasswordChangeResponse changePassword(
            @Valid @RequestBody AuthDtos.PasswordChangeRequest request) {
        return authService.changePassword(request);
    }

    @PostMapping("/v1/auth/mobile/login")
    public AuthDtos.AuthResponse mobileLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request, Role.SCHUELER);
    }

    @PostMapping("/v1/auth/admin/login")
    public AuthDtos.AuthResponse adminLogin(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request, Role.ADMIN);
    }

    @PostMapping("/v1/registration")
    public ResponseEntity<AuthDtos.RegisterResponse> legacyRegistration(
            @Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/v1/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void legacyLogout(@AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody(required = false) AuthDtos.LogoutRequest request) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            authService.logout(request);
        } else {
            authService.logoutAll(currentUser.userId());
        }
    }

    @PostMapping("/v1/auth/password/reset-request")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void legacyResetRequest(@Valid @RequestBody AuthDtos.PasswordResetRequest request) {
        authService.requestPasswordReset(request);
    }

    @PostMapping("/v1/auth/password/change")
    public AuthDtos.PasswordChangeResponse legacyChangePassword(
            @Valid @RequestBody AuthDtos.PasswordChangeRequest request) {
        return authService.changePassword(request);
    }

    @GetMapping("/v1/users/me")
    public AuthDtos.UserInfo legacyMe(@AuthenticationPrincipal CurrentUser currentUser) {
        return authService.getCurrentUserInfo(currentUser);
    }
}
