package at.htl.ecotrack.scoring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "points_ledgers")
public class PointsLedger {

    @Id
    @Column(name = "eco_user_id", nullable = false)
    private UUID ecoUserId;

    @Column(name = "total_points", nullable = false)
    private int totalPoints;

    @Column(name = "last_updated", nullable = false)
    private OffsetDateTime lastUpdated;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @PrePersist
    @PreUpdate
    void onWrite() {
        this.lastUpdated = OffsetDateTime.now();
    }

    public UUID getEcoUserId() {
        return ecoUserId;
    }

    public void setEcoUserId(UUID ecoUserId) {
        this.ecoUserId = ecoUserId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public long getVersion() {
        return version;
    }
}
