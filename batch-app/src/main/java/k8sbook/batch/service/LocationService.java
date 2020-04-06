package k8sbook.batch.service;

import k8sbook.batch.domain.Location;
import k8sbook.batch.domain.Region;
import k8sbook.batch.entity.LocationEntity;
import k8sbook.batch.entity.RegionEntity;
import k8sbook.batch.repository.LocationRepository;
import k8sbook.batch.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newrelic.api.agent.Trace;

@Controller
public class
LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

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
                e.setRegion(getRegionEntityById(l.getRegion(), regionMap));
                e.setNote(l.getNote());
            });
            locationRepository.save(entity);
        });
    }

    @Transactional
    public void registerLocationsFromFile(List<String[]> dataList) {
        var regionMap = new HashMap<String, RegionEntity>();
        dataList.forEach(data -> {
            try {
                var regionName = data[0];
                var locationName = data[1];
                var note = data[2];

                var region = getRegionEntityByName(regionName, regionMap);
                var location = new LocationEntity().apply(l -> {
                    l.setLocationName(locationName);
                    l.setRegion(region);
                    l.setNote(note);
                });
                locationRepository.save(location);
            } catch (Exception e) {
                LOGGER.warn("Skipped data. Error occurred in: " + Arrays.toString(data), e);
            }
        });

    }

    private RegionEntity getRegionEntityById(Region region, Map<Integer, RegionEntity> regionMap) {
        if (regionMap.get(region.getRegionId()) == null) {
            regionRepository.findById(region.getRegionId()).ifPresentOrElse(
                    e -> regionMap.put(region.getRegionId(), e),
                    () -> {
                        throw new RuntimeException("No such region for ID: " + region.getRegionId());
                    });
        }
        return regionMap.get(region.getRegionId());
    }

    private RegionEntity getRegionEntityByName(String regionName, Map<String, RegionEntity> regionMap) {
        if (regionMap.get(regionName) == null) {
            regionRepository.findByRegionName(regionName).ifPresentOrElse(
                    entity -> regionMap.put(entity.getRegionName(), entity),
                    () -> {
                        throw new RuntimeException("No such region for name: " + regionName);
                    });
        }
        return regionMap.get(regionName);
    }

}
