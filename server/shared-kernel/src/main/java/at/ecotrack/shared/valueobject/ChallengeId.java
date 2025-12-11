package at.ecotrack.shared.valueobject;

import java.util.UUID;

/**
 * Typisierte Challenge-ID für typsichere Referenzen zwischen Modulen.
 */
public record ChallengeId(UUID value) {

    public static ChallengeId of(UUID value) {
        return new ChallengeId(value);
    }

    public static ChallengeId generate() {
        return new ChallengeId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
