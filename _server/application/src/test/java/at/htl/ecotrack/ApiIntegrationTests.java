package at.htl.ecotrack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.htl.ecotrack.TestJwtConfig.TestJwtHelper;
import at.htl.ecotrack.administration.security.KeycloakAdminService;
import at.htl.ecotrack.administration.security.KeycloakTokenService;
import at.htl.ecotrack.administration.security.KeycloakTokenService.KeycloakTokenResponse;
import at.htl.ecotrack.shared.model.Role;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJwtConfig.class)
class ApiIntegrationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private TestJwtHelper testJwtHelper;

        @MockBean
        private KeycloakAdminService keycloakAdminService;

        @MockBean
        private KeycloakTokenService keycloakTokenService;

        /** Speichert email → (userId, role) für koordinierte Mocks */
        private record UserInfo(UUID userId, Role role) {
        }

        private final Map<String, UserInfo> registeredUsers = new ConcurrentHashMap<>();

        @BeforeEach
        void setupMocks() {
                registeredUsers.clear();

                // createUser: generiert UUID, speichert sie für spätere login-Aufrufe
                when(keycloakAdminService.createUser(anyString(), anyString(), anyString(), anyString(),
                                any(Role.class)))
                                .thenAnswer(inv -> {
                                        String email = ((String) inv.getArgument(0)).toLowerCase();
                                        Role role = inv.getArgument(4);
                                        UUID userId = UUID.randomUUID();
                                        registeredUsers.put(email, new UserInfo(userId, role));
                                        return userId;
                                });

                // login: erzeugt ein gültiges JWT mit den Daten des registrierten Users
                when(keycloakTokenService.login(anyString(), anyString()))
                                .thenAnswer(inv -> {
                                        String email = ((String) inv.getArgument(0)).toLowerCase();
                                        UserInfo info = registeredUsers.get(email);
                                        if (info == null) {
                                                throw new IllegalStateException(
                                                                "Test-User " + email + " nicht registriert");
                                        }
                                        String jwt = testJwtHelper.createJwt(info.userId(), email, info.role());
                                        return new KeycloakTokenResponse(jwt, "test-refresh-token", 900);
                                });
        }

        @Test
        void registerStudentCreateActivityAndReadProgress() throws Exception {
                String adminToken = registerAndExtractToken("admin-a@ecotrack.test", "ADMIN", null);

                String classPayload = """
                                {
                                  "name": "1AHIT",
                                  "schoolId": "00000000-0000-0000-0000-000000000001",
                                  "schoolName": "HTL Leoben"
                                }
                                """;

                MvcResult classResult = mockMvc.perform(post("/api/admin/classes")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(classPayload))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.classId").exists())
                                .andReturn();

                String classId = read(classResult, "classId");

                String studentPayload = """
                                {
                                  "email": "student-a@ecotrack.test",
                                  "password": "Passwort123!",
                                  "firstName": "Anna",
                                  "lastName": "Muster",
                                  "role": "SCHUELER",
                                  "classId": "%s"
                                }
                                """.formatted(classId);

                MvcResult studentResult = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(studentPayload))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andReturn();

                String studentToken = read(studentResult, "accessToken");

                String activityPayload = """
                                {
                                  "actionDefinitionId": "11111111-1111-1111-1111-111111111111",
                                  "quantity": 2
                                }
                                """;

                mockMvc.perform(post("/api/activities")
                                .header("Authorization", "Bearer " + studentToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(activityPayload))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.points").value(20));

                mockMvc.perform(get("/api/progress")
                                .header("Authorization", "Bearer " + studentToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalPoints").value(20))
                                .andExpect(jsonPath("$.currentLevel").value("SETZLING"));
        }

        @Test
        void teacherLoginRequiresPasswordChange() throws Exception {
                registerAndExtractToken("teacher-a@ecotrack.test", "LEHRER", null);

                String loginPayload = """
                                {
                                  "email": "teacher-a@ecotrack.test",
                                  "password": "Passwort123!"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginPayload))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value("PASSWORD_CHANGE_REQUIRED"));
        }

        @Test
        void teacherCanCreateChallengeForClass() throws Exception {
                String adminToken = registerAndExtractToken("admin-b@ecotrack.test", "ADMIN", null);
                String classId = createClass(adminToken, "2BHIT");
                String teacherToken = registerAndExtractToken("teacher-b@ecotrack.test", "LEHRER", null);

                String challengePayload = """
                                {
                                  "title": "Klassen-Challenge %s",
                                  "description": "Mehr Fahrradfahrten",
                                  "goalValue": 100,
                                  "goalUnit": "POINTS",
                                  "startDate": "2026-02-01",
                                  "endDate": "2026-03-01",
                                  "classId": "%s"
                                }
                                """.formatted(UUID.randomUUID(), classId);

                mockMvc.perform(post("/api/challenges")
                                .header("Authorization", "Bearer " + teacherToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(challengePayload))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").exists())
                                .andExpect(jsonPath("$.goal.unit").value("POINTS"));
        }

        private String registerAndExtractToken(String email, String role, String classId) throws Exception {
                String classJson = classId == null ? "null" : "\"" + classId + "\"";
                String payload = """
                                {
                                  "email": "%s",
                                  "password": "Passwort123!",
                                  "firstName": "Max",
                                  "lastName": "Tester",
                                  "role": "%s",
                                  "classId": %s
                                }
                                """.formatted(email, role, classJson);

                MvcResult result = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                                .andExpect(status().isCreated())
                                .andReturn();

                String token = read(result, "accessToken");
                assertThat(token).isNotBlank();
                return token;
        }

        private String createClass(String adminToken, String className) throws Exception {
                String payload = """
                                {
                                  "name": "%s",
                                  "schoolId": "00000000-0000-0000-0000-000000000001",
                                  "schoolName": "HTL Leoben"
                                }
                                """.formatted(className);

                MvcResult result = mockMvc.perform(post("/api/admin/classes")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                                .andExpect(status().isOk())
                                .andReturn();
                return read(result, "classId");
        }

        private String read(MvcResult result, String field) throws Exception {
                JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
                return root.path(field).asText();
        }
}
