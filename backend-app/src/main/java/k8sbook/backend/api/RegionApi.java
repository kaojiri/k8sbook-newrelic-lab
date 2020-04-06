package k8sbook.backend.api;

import k8sbook.backend.dto.RegionDto;
import k8sbook.backend.dto.RegionsDto;
import k8sbook.backend.service.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import com.newrelic.api.agent.NewRelic;

@RestController
@RequestMapping("region")
@CrossOrigin(origins = "*")
public class RegionApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionApi.class);

    @Autowired
    private RegionService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public RegionsDto getAllRegions() {
        LOGGER.info("REGION GET ALL API");

        // 処理対象のファイル名をCustom Attributesとして送信
        NewRelic.addCustomParameter("custom-region", "REGION");

        var allRegions = service.getAllRegions();
        var dtoList = new ArrayList<RegionDto>();
        allRegions.forEach(region -> {
            var dto = new RegionDto(region);
            dtoList.add(dto);
        });
        var regionsDto = new RegionsDto(dtoList);
        return regionsDto;
    }

}
