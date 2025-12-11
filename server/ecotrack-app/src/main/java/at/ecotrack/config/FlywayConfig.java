package at.ecotrack.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Flyway-Konfiguration für Multi-Schema-Migrationen.
 * Jedes Modul hat sein eigenes Schema und eigene Migrations.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flywayScoring(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("scoring")
                .locations("classpath:db/migration/scoring")
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    public Flyway flywayChallenge(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("challenge")
                .locations("classpath:db/migration/challenge")
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    public Flyway flywayUserProfile(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("userprofile")
                .locations("classpath:db/migration/userprofile")
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    public Flyway flywayAdmin(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas("admin")
                .locations("classpath:db/migration/admin")
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    @DependsOn({ "flywayScoring", "flywayChallenge", "flywayUserProfile", "flywayAdmin" })
    public FlywayMigrationInitializer flywayInitializer(
            Flyway flywayScoring,
            Flyway flywayChallenge,
            Flyway flywayUserProfile,
            Flyway flywayAdmin) {
        return new FlywayMigrationInitializer(flywayScoring, flyway -> {
            flywayScoring.migrate();
            flywayChallenge.migrate();
            flywayUserProfile.migrate();
            flywayAdmin.migrate();
        });
    }
}
