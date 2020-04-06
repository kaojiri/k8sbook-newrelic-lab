package k8sbook.backend.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGION")
public class RegionEntity implements SampleAppEntity<RegionEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REGION_ID")
    private Integer regionId;

    @Column(name = "REGION_NAME")
    private String regionName;

    @Column(name = "CREATION_TIMESTAMP")
    private LocalDateTime creationTimestamp;

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("regionId", regionId)
                .append("regionName", regionName)
                .append("creationTimestamp", creationTimestamp)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        var target = (RegionEntity) obj;
        return new EqualsBuilder()
                .append(regionId, target.regionId)
                .append(regionName, target.regionName)
                .append(creationTimestamp, target.creationTimestamp)
                .build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 37)
                .append(regionId)
                .append(regionName)
                .append(creationTimestamp)
                .build();
    }
}
