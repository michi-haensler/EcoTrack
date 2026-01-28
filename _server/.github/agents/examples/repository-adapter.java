// ============================================================
// Repository Adapter Beispiel
// ============================================================
// Dieses Beispiel zeigt die korrekte Implementierung eines
// Repository Adapters mit JPA Entity und Mapper.
// ============================================================

// -----------------------------
// JPA Entity (Adapter Layer!)
// -----------------------------

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "activity_entries", schema = "scoring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityEntryJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @Column(name = "action_definition_id", nullable = false)
    private UUID actionDefinitionId;

    @Column(nullable = false)
    private int quantity;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "logged_at", nullable = false)
    private OffsetDateTime loggedAt;
}

// -----------------------------
// Spring Data JPA Repository
// -----------------------------
@Repository
public interface ActivityEntryJpaRepository
        extends JpaRepository<ActivityEntryJpaEntity, UUID> {

    List<ActivityEntryJpaEntity> findByEcoUserIdOrderByLoggedAtDesc(UUID ecoUserId);

    @Query("""
                SELECT e FROM ActivityEntryJpaEntity e
                WHERE e.ecoUserId = :userId
                AND e.loggedAt >= :from
                AND e.loggedAt <= :to
                ORDER BY e.loggedAt DESC
            """)
    List<ActivityEntryJpaEntity> findByUserAndPeriod(
            @Param("userId") UUID userId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to);

    Page<ActivityEntryJpaEntity> findByEcoUserId(
            UUID ecoUserId,
            Pageable pageable);
}

// -----------------------------
// JPA ↔ Domain Mapper
// -----------------------------
@Mapper(componentModel = "spring")
public interface ActivityEntryJpaMapper {

    @Mapping(target = "id", expression = "java(ActivityEntryId.of(entity.getId()))")
    @Mapping(target = "ecoUserId", expression = "java(EcoUserId.of(entity.getEcoUserId()))")
    @Mapping(target = "actionDefinitionId", expression = "java(ActionDefinitionId.of(entity.getActionDefinitionId()))")
    ActivityEntry toDomain(ActivityEntryJpaEntity entity);

    @Mapping(target = "id", expression = "java(domain.getId().value())")
    @Mapping(target = "ecoUserId", expression = "java(domain.getEcoUserId().value())")
    @Mapping(target = "actionDefinitionId", expression = "java(domain.getActionDefinitionId().value())")
    ActivityEntryJpaEntity toJpaEntity(ActivityEntry domain);
}

// -----------------------------
// Repository Adapter
// -----------------------------
// Implementiert Domain Port, nutzt JPA Repository
@Component
@RequiredArgsConstructor
public class ActivityEntryRepositoryAdapter implements ActivityEntryRepository {

    private final ActivityEntryJpaRepository jpaRepository;
    private final ActivityEntryJpaMapper mapper;

    @Override
    public ActivityEntry save(ActivityEntry entry) {
        // Domain → JPA
        ActivityEntryJpaEntity entity = mapper.toJpaEntity(entry);
        // Persist
        ActivityEntryJpaEntity saved = jpaRepository.save(entity);
        // JPA → Domain
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ActivityEntry> findById(ActivityEntryId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<ActivityEntry> findByEcoUserId(EcoUserId userId) {
        return jpaRepository.findByEcoUserIdOrderByLoggedAtDesc(userId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<ActivityEntry> findByEcoUserId(EcoUserId userId, Pageable pageable) {
        return jpaRepository.findByEcoUserId(userId.value(), pageable)
                .map(mapper::toDomain);
    }
}
