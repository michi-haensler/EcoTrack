package at.htl.ecotrack.shared.security;

import at.htl.ecotrack.shared.model.Role;

import java.util.UUID;

public record CurrentUser(UUID userId, String email, Role role) {
}
