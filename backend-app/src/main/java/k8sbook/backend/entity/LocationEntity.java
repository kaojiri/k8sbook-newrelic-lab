package k8sbook.backend.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LOCATION")
public class LocationEntity implements SampleAppEntity<LocationEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "LOCATION_NAME")
    private String locationName;

    @OneToOne
    @JoinColumn(name = "REGION_ID")
    private RegionEntity region;

    @Column(name = "NOTE")
    private String note;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public RegionEntity getRegion() {
        return region;
    }

    public void setRegion(RegionEntity region) {
        this.region = region;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
