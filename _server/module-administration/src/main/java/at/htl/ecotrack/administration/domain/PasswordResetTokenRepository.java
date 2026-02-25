package at.htl.ecotrack.administration.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    long countByUserIdAndCreatedAtAfter(java.util.UUID userId, OffsetDateTime createdAfter);
}
