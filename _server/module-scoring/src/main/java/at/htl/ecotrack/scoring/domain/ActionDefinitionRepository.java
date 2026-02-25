package at.htl.ecotrack.scoring.domain;

import at.htl.ecotrack.shared.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActionDefinitionRepository extends JpaRepository<ActionDefinition, UUID> {
    List<ActionDefinition> findByActiveTrue();
    List<ActionDefinition> findByActiveTrueAndCategory(Category category);
}
