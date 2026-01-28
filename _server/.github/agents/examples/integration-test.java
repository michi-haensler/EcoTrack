// ============================================================
// Integration Test Beispiele
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung von
// Integration Tests mit Spring Boot Test und MockMvc.
// ============================================================

// -----------------------------
// Controller Integration Test
// -----------------------------

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoringControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityEntryJpaRepository activityRepository;

    @Autowired
    private ActionDefinitionJpaRepository actionRepository;

    private ActionDefinitionJpaEntity testAction;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();

        // Test-Daten vorbereiten
        testAction = actionRepository.save(ActionDefinitionJpaEntity.builder()
                .name("Radfahren")
                .description("Mit dem Fahrrad fahren")
                .points(10)
                .category("Transport")
                .build());
    }

    // -------------------------
    // POST Tests
    // -------------------------

    @Test
    void should_return201_when_activityLogged() throws Exception {
        // Arrange
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                testAction.getId(),
                5,
                "Test Activity");

        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.points").value(50)); // 5 * 10

        // Verify persistence
        assertThat(activityRepository.findAll()).hasSize(1);
    }

    @Test
    void should_return404_when_actionNotFound() throws Exception {
        // Arrange
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                5,
                null);

        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void should_return400_when_invalidQuantity() throws Exception {
        // Arrange
        LogActivityRequest request = new LogActivityRequest(
                UUID.randomUUID(),
                testAction.getId(),
                -1, // Invalid!
                null);

        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void should_return400_when_missingRequiredField() throws Exception {
        // Arrange - Missing ecoUserId
        String invalidJson = """
                {
                    "actionDefinitionId": "%s",
                    "quantity": 5
                }
                """.formatted(testAction.getId());

        // Act & Assert
        mockMvc.perform(post("/api/scoring/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // -------------------------
    // GET Tests
    // -------------------------

    @Test
    void should_returnActivity_when_exists() throws Exception {
        // Arrange
        ActivityEntryJpaEntity entity = activityRepository.save(
                ActivityEntryJpaEntity.builder()
                        .ecoUserId(UUID.randomUUID())
                        .actionDefinitionId(testAction.getId())
                        .quantity(3)
                        .loggedAt(OffsetDateTime.now())
                        .build());

        // Act & Assert
        mockMvc.perform(get("/api/scoring/activities/{id}", entity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId().toString()))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void should_return404_when_activityNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/scoring/activities/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}

// -----------------------------
// Repository Integration Test
// -----------------------------
@DataJpaTest
@ActiveProfiles("test")
class ActivityEntryRepositoryTest {

    @Autowired
    private ActivityEntryJpaRepository repository;

    @Test
    void should_saveActivity_when_validEntity() {
        // Arrange
        ActivityEntryJpaEntity entity = ActivityEntryJpaEntity.builder()
                .ecoUserId(UUID.randomUUID())
                .actionDefinitionId(UUID.randomUUID())
                .quantity(5)
                .loggedAt(OffsetDateTime.now())
                .build();

        // Act
        ActivityEntryJpaEntity saved = repository.save(entity);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getEcoUserId()).isEqualTo(entity.getEcoUserId());
    }

    @Test
    void should_findActivities_when_searchByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        repository.saveAll(List.of(
                createEntity(userId),
                createEntity(userId),
                createEntity(otherUserId) // Different user
        ));

        // Act
        List<ActivityEntryJpaEntity> found = repository
                .findByEcoUserIdOrderByLoggedAtDesc(userId);

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(e -> e.getEcoUserId().equals(userId));
    }

    @Test
    void should_returnEmpty_when_noActivitiesForUser() {
        // Arrange
        repository.save(createEntity(UUID.randomUUID()));

        // Act
        List<ActivityEntryJpaEntity> found = repository
                .findByEcoUserIdOrderByLoggedAtDesc(UUID.randomUUID());

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void should_orderByLoggedAtDesc_when_findByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        repository.saveAll(List.of(
                createEntityWithTime(userId, now.minusDays(2)),
                createEntityWithTime(userId, now),
                createEntityWithTime(userId, now.minusDays(1))));

        // Act
        List<ActivityEntryJpaEntity> found = repository
                .findByEcoUserIdOrderByLoggedAtDesc(userId);

        // Assert
        assertThat(found).hasSize(3);
        assertThat(found.get(0).getLoggedAt()).isAfterOrEqualTo(found.get(1).getLoggedAt());
        assertThat(found.get(1).getLoggedAt()).isAfterOrEqualTo(found.get(2).getLoggedAt());
    }

    // -------------------------
    // Helper Methods
    // -------------------------

    private ActivityEntryJpaEntity createEntity(UUID userId) {
        return ActivityEntryJpaEntity.builder()
                .ecoUserId(userId)
                .actionDefinitionId(UUID.randomUUID())
                .quantity(1)
                .loggedAt(OffsetDateTime.now())
                .build();
    }

    private ActivityEntryJpaEntity createEntityWithTime(UUID userId, OffsetDateTime time) {
        return ActivityEntryJpaEntity.builder()
                .ecoUserId(userId)
                .actionDefinitionId(UUID.randomUUID())
                .quantity(1)
                .loggedAt(time)
                .build();
    }
}
