package at.htl.ecotrack;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import at.htl.ecotrack.shared.model.Role;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test-Konfiguration, die einen eigenen JwtDecoder mit In-Memory RSA-Schlüsseln
 * bereitstellt.
 * Damit können Integration-Tests JWT-Tokens erzeugen und validieren, ohne
 * Keycloak zu benötigen.
 */
@TestConfiguration
public class TestJwtConfig {

    private final RSAKey rsaKey;

    public TestJwtConfig() {
        try {
            this.rsaKey = new RSAKeyGenerator(2048).keyID("test-key").generate();
        } catch (JOSEException e) {
            throw new IllegalStateException("RSA-Key Generierung fehlgeschlagen", e);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        } catch (JOSEException e) {
            throw new IllegalStateException("JwtDecoder Erstellung fehlgeschlagen", e);
        }
    }

    @Bean
    public TestJwtHelper testJwtHelper() {
        return new TestJwtHelper(rsaKey);
    }

    /**
     * Hilfsklasse zum Erzeugen von signierten JWT-Tokens für Tests.
     */
    public static class TestJwtHelper {
        private final RSAKey rsaKey;

        public TestJwtHelper(RSAKey rsaKey) {
            this.rsaKey = rsaKey;
        }

        public String createJwt(UUID userId, String email, Role role) {
            try {
                Instant now = Instant.now();
                JWTClaimsSet claims = new JWTClaimsSet.Builder()
                        .subject(userId.toString())
                        .issuer("http://localhost:0/realms/test")
                        .claim("email", email)
                        .claim("realm_access", Map.of("roles", List.of(role.name())))
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(now.plusSeconds(900)))
                        .build();

                SignedJWT signedJWT = new SignedJWT(
                        new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                        claims);
                signedJWT.sign(new RSASSASigner(rsaKey));
                return signedJWT.serialize();
            } catch (JOSEException e) {
                throw new IllegalStateException("JWT Signierung fehlgeschlagen", e);
            }
        }
    }
}
