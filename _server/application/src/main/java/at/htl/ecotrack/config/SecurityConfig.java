package at.htl.ecotrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import at.htl.ecotrack.administration.security.KeycloakJwtConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final KeycloakJwtConverter keycloakJwtConverter;

        public SecurityConfig(KeycloakJwtConverter keycloakJwtConverter) {
                this.keycloakJwtConverter = keycloakJwtConverter;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/password/reset-request",
                                                                "/api/auth/password/change",
                                                                "/api/v1/auth/mobile/login",
                                                                "/api/v1/auth/admin/login",
                                                                "/api/v1/registration",
                                                                "/api/v1/auth/password/reset-request",
                                                                "/api/v1/auth/password/change",
                                                                "/api/v2/registration") // Self-Registration (hexagonal)
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v2/registration/email-check") // E-Mail-Verfügbarkeitsprüfung
                                                .permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/actuator/health")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                // Keycloak-JWTs werden als Bearer-Token validiert.
                                // Der KeycloakJwtConverter extrahiert CurrentUser-Principal + Rolle.
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter)));

                return http.build();
        }
}
