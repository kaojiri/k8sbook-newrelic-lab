package k8sbook.batch.domain;

import k8sbook.batch.entity.LocationEntity;

public class Location {

    private Long locationId;

    private String locationName;

    private Region region;

    private String note;

    public Location(Long locationId, String locationName, Region region) {
        if (locationName == null) {
            throw new IllegalArgumentException("locationName cannot be null.");
        }
        if (region == null) {
            throw new IllegalArgumentException("region cannot be null.");
        }
        this.locationId = locationId;
        this.locationName = locationName;
        this.region = region;
    }

    public Location(String locationName, Region region) {
        this(null, locationName, region);
    }

    public Location(LocationEntity entity) {
        this(entity.getLocationId(), entity.getLocationName(), new Region(entity.getRegion()));
    }

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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
