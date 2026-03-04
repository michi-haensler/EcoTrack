package at.htl.ecotrack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MS-01: Auth / Registration / Password-Reset — Integrationstests
 *
 * <p>
 * Testet alle in MS-01 definierten Flows:
 * <ul>
 * <li>Registrierung (Schüler, Lehrer, Admin)</li>
 * <li>Mobile-Login / Admin-Login</li>
 * <li>Logout (Token-Invalidierung)</li>
 * <li>Passwort-Reset-Request</li>
 * <li>Profil-Abruf (/users/me)</li>
 * <li>Fehlerzustände (EMAIL_EXISTS, CLASS_REQUIRED, INVALID_CREDENTIALS)</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class MS01AuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -------------------------------------------------------------------------
    // Registrierung
    // -------------------------------------------------------------------------

    @Test
    void should_registerStudent_when_validPayloadWithClassId() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-ms01-a@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "3CHIT-MS01A");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegisterPayload(
                        "student-ms01-a@ecotrack.test", "SCHUELER", classId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.role").value("SCHUELER"))
                .andReturn();

        String token = readField(result, "accessToken");
        assertThat(token).isNotBlank();
    }

    @Test
    void should_returnBadRequest_when_studentRegistersWithoutClassId() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "student-noclassid@ecotrack.test",
                          "password": "Passwort123!",
                          "firstName": "No",
                          "lastName": "Class",
                          "role": "SCHUELER",
                          "classId": null
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CLASS_REQUIRED"));
    }

    @Test
    void should_returnBadRequest_when_emailAlreadyExists() throws Exception {
        // Arrange — ersten User registrieren
        registerAndGetToken("duplicate@ecotrack.test", "ADMIN", null);

        // Act & Assert — nochmals registrieren → EMAIL_EXISTS
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegisterPayload("duplicate@ecotrack.test", "ADMIN", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EMAIL_EXISTS"));
    }

    @Test
    void should_registerViaV1Endpoint_when_validPayload() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-ms01-v1@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "4DHIT-MS01V1");

        // Act & Assert
        mockMvc.perform(post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegisterPayload(
                        "student-v1@ecotrack.test", "SCHUELER", classId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    @Test
    void should_loginViaMobileEndpoint_when_validStudentCredentials() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-mobile-login@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "5AHIT-MOBILE");
        registerAndGetToken("student-mobile@ecotrack.test", "SCHUELER", classId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/mobile/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "student-mobile@ecotrack.test",
                          "password": "Passwort123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.role").value("SCHUELER"));
    }

    @Test
    void should_returnPasswordChangeRequired_when_adminLoginWithTemporaryPassword() throws Exception {
        // Arrange — Lehrer/Temp-Passwort-User registrieren
        registerAndGetToken("teacher-update-pw@ecotrack.test", "LEHRER", null);

        // Act & Assert — Admin-Login-Endpunkt erkennt update_password
        mockMvc.perform(post("/api/v1/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "teacher-update-pw@ecotrack.test",
                          "password": "Passwort123!"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("PASSWORD_CHANGE_REQUIRED"));
    }

    @Test
    void should_returnUnauthorized_when_invalidCredentials() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "nonexistent@ecotrack.test",
                          "password": "WrongPassword1!"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Profil abrufen (/users/me)
    // -------------------------------------------------------------------------

    @Test
    void should_returnUserProfile_when_authenticatedRequest() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-me@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "6BHIT-ME");
        String studentToken = registerAndGetToken("student-me@ecotrack.test", "SCHUELER", classId);

        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student-me@ecotrack.test"))
                .andExpect(jsonPath("$.role").value("SCHUELER"));
    }

    @Test
    void should_returnUserProfile_viaV1Endpoint_when_authenticated() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-me-v1@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "6CHIT-ME-V1");
        String studentToken = registerAndGetToken("student-me-v1@ecotrack.test", "SCHUELER", classId);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student-me-v1@ecotrack.test"));
    }

    @Test
    void should_returnUnauthorized_when_unauthenticatedUserCallsMe() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    @Test
    void should_logoutSuccessfully_when_validRefreshToken() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-logout@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "7AHIT-LOGOUT");

        MvcResult loginResult = registerAndGetFullAuthResponse(
                "student-logout@ecotrack.test", "SCHUELER", classId);
        String refreshToken = readField(loginResult, "refreshToken");
        String accessToken = readField(loginResult, "accessToken");

        // Act — Logout mit Refresh-Token
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_logoutSuccessfully_viaV1Endpoint() throws Exception {
        // Arrange
        String adminToken = registerAndGetToken("admin-logout-v1@ecotrack.test", "ADMIN", null);
        String classId = createClass(adminToken, "7BHIT-LOGOUT-V1");

        MvcResult loginResult = registerAndGetFullAuthResponse(
                "student-logout-v1@ecotrack.test", "SCHUELER", classId);
        String refreshToken = readField(loginResult, "refreshToken");
        String accessToken = readField(loginResult, "accessToken");

        // Act
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------------------------------
    // Passwort-Reset
    // -------------------------------------------------------------------------

    @Test
    void should_acceptPasswordResetRequest_when_emailExists() throws Exception {
        // Arrange
        registerAndGetToken("student-reset@ecotrack.test", "ADMIN", null);

        // Act & Assert — kein Fehler, immer HTTP 202 (auch wenn E-Mail nicht existiert)
        mockMvc.perform(post("/api/auth/password/reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"student-reset@ecotrack.test\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void should_acceptPasswordResetRequest_viaV1Endpoint() throws Exception {
        // Arrange
        registerAndGetToken("student-reset-v1@ecotrack.test", "ADMIN", null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/password/reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"student-reset-v1@ecotrack.test\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void should_acceptPasswordResetRequest_evenWhenEmailNotFound() throws Exception {
        // Act & Assert — Sicherheit: kein 404, um User-Enumeration zu verhindern
        mockMvc.perform(post("/api/auth/password/reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"unknown@ecotrack.test\"}"))
                .andExpect(status().isAccepted());
    }

    // -------------------------------------------------------------------------
    // Hilfsmethoden
    // -------------------------------------------------------------------------

    private String registerAndGetToken(String email, String role, String classId) throws Exception {
        MvcResult result = registerAndGetFullAuthResponse(email, role, classId);
        return readField(result, "accessToken");
    }

    private MvcResult registerAndGetFullAuthResponse(String email, String role, String classId)
            throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegisterPayload(email, role, classId)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String buildRegisterPayload(String email, String role, String classId) {
        String classJson = classId == null ? "null" : "\"" + classId + "\"";
        return """
                {
                  "email": "%s",
                  "password": "Passwort123!",
                  "firstName": "Test",
                  "lastName": "User",
                  "role": "%s",
                  "classId": %s
                }
                """.formatted(email, role, classJson);
    }

    private String createClass(String adminToken, String className) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "%s",
                          "schoolId": "00000000-0000-0000-0000-000000000001",
                          "schoolName": "HTL Leoben"
                        }
                        """.formatted(className)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classId").exists())
                .andReturn();
        return readField(result, "classId");
    }

    private String readField(MvcResult result, String field) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path(field).asText();
    }
}
