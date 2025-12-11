package at.ecotrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * EcoTrack Application - Main Entry Point.
 * <p>
 * Modularer Monolith mit 4 Bounded Contexts:
 * - Scoring (Core Domain - Hexagonal Architecture)
 * - Challenge (Core Domain - Hexagonal Architecture)
 * - UserProfile (Supporting Domain - CRUD Architecture)
 * - Administration (Generic Domain - ACL to Keycloak)
 */
@SpringBootApplication
@EnableJpaAuditing
public class EcoTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoTrackApplication.class, args);
    }
}
