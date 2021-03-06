package k8sbook.backend.api;

import k8sbook.backend.dto.LocationDto;
import k8sbook.backend.dto.LocationsDto;
import k8sbook.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import com.newrelic.api.agent.NewRelic;

@RestController
@RequestMapping("location")
@CrossOrigin("*")
public class LocationApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationApi.class);

    @Autowired
    private LocationService service;

    @GetMapping(value = "region/{regionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationsDto getLocationListByRegion(@PathVariable("regionId") Integer regionId) {
        LOGGER.info("LOCATION LIST BY REGION ID API");

        // 処理対象のファイル名をCustom Attributesとして送信
        NewRelic.addCustomParameter("custom-location", regionId);

        var locationList = service.getLocationListByRegionId(regionId);
        var dtoList = new ArrayList<LocationDto>();
        locationList.forEach(location -> {
            var dto = new LocationDto(location);
            dtoList.add(dto);
        });
        var locationsDto = new LocationsDto(dtoList);
        return locationsDto;
    }

}
