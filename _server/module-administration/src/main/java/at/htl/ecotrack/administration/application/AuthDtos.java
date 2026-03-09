package at.htl.ecotrack.administration.application;

import java.util.List;
import java.util.UUID;

import at.htl.ecotrack.shared.model.Role;
import at.htl.ecotrack.shared.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @Email String email,
            @Size(min = 8) String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull Role role,
            UUID classId) {
    }

    public record LoginRequest(@Email String email, @NotBlank String password) {
    }

    public record AuthResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
    }

    public record RegisterResponse(UUID userId, String email, String message) {
    }

    public record UserInfo(UUID userId, UUID ecoUserId, String email, String firstName, String lastName, Role role) {
    }

    public record PasswordResetRequest(@Email String email) {
    }

    public record PasswordChangeRequest(
            @Email String email,
            @NotBlank String currentPassword,
            @Size(min = 8) String newPassword) {
    }

    public record PasswordChangeResponse(String message) {
    }

    public record LogoutRequest(@NotBlank String refreshToken) {
    }

    public record UserAdminView(UUID userId, String email, Role role, UserStatus status, boolean mustChangePassword) {
    }

    public record UserPage(List<UserAdminView> content, int page, int size, long totalElements, int totalPages) {
    }

    public record CreateClassRequest(@NotBlank String name, @NotNull UUID schoolId, @NotBlank String schoolName) {
    }

    public record ClassResponse(UUID classId, String name, UUID schoolId, String schoolName, int studentCount,
            List<UUID> teacherIds) {
    }

    private AuthDtos() {
    }
}
