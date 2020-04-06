package k8sbook.backend.service;

import k8sbook.backend.domain.Location;
import k8sbook.backend.domain.Region;
import k8sbook.backend.entity.LocationEntity;
import k8sbook.backend.entity.RegionEntity;
import k8sbook.backend.repository.LocationRepository;
import k8sbook.backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RegionRepository regionRepository;

    public List<Location> getLocationListByRegionId(Integer regionId) {
        var region = regionRepository.findById(regionId);
        var locationList = new ArrayList<Location>();
        region.ifPresent(r -> {
            var locationEntityList = locationRepository.findByRegion(r);
            locationEntityList.forEach(entity -> locationList.add(new Location(entity)));
        });

        return locationList;
    }

    @Transactional
    public void registerLocations(List<Location> locationList) {
        var regionMap = new HashMap<Integer, RegionEntity>();
        locationList.forEach(l -> {
            var entity = new LocationEntity().apply(e -> {
                e.setLocationName(l.getLocationName());
                e.setRegion(getRegionEntity(l.getRegion(), regionMap));
                e.setNote(l.getNote());
            });
            locationRepository.save(entity);
        });
    }

    private RegionEntity getRegionEntity(Region region, Map<Integer, RegionEntity> regionMap) {
        if (regionMap.get(region.getRegionId()) == null) {
            regionRepository.findById(region.getRegionId()).ifPresent(e -> regionMap.put(region.getRegionId(), e));
        }
        return regionMap.get(region.getRegionId());
    }

}
