package at.ecotrack.domain.repository;

import at.ecotrack.domain.entity.ActionDefinition;
import at.ecotrack.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActionDefinitionRepository extends JpaRepository<ActionDefinition, UUID> {

    List<ActionDefinition> findByActiveTrue();

    List<ActionDefinition> findByCategoryAndActiveTrue(Category category);
}
