package at.htl.ecotrack.scoring.domain;

import at.htl.ecotrack.shared.model.ActivitySource;
import at.htl.ecotrack.shared.model.Category;
import at.htl.ecotrack.shared.model.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_entries")
public class ActivityEntry {

    @Id
    @Column(name = "activity_entry_id", nullable = false)
    private UUID activityEntryId;

    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @Column(name = "action_definition_id", nullable = false)
    private UUID actionDefinitionId;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private Unit unit;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private ActivitySource source;

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = OffsetDateTime.now();
        }
    }

    public UUID getActivityEntryId() {
        return activityEntryId;
    }

    public void setActivityEntryId(UUID activityEntryId) {
        this.activityEntryId = activityEntryId;
    }

    public UUID getEcoUserId() {
        return ecoUserId;
    }

    public void setEcoUserId(UUID ecoUserId) {
        this.ecoUserId = ecoUserId;
    }

    public UUID getActionDefinitionId() {
        return actionDefinitionId;
    }

    public void setActionDefinitionId(UUID actionDefinitionId) {
        this.actionDefinitionId = actionDefinitionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public ActivitySource getSource() {
        return source;
    }

    public void setSource(ActivitySource source) {
        this.source = source;
    }
}
