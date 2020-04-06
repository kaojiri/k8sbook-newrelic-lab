package k8sbook.backend.service;

import k8sbook.backend.domain.Region;
import k8sbook.backend.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RegionService {

    @Autowired
    private RegionRepository repository;

    public List<Region> getAllRegions() {
        var regionEntities = repository.findAll();
        var regionList = new ArrayList<Region>();
        regionEntities.forEach(entity -> regionList.add(new Region(entity)));

        return regionList;
    }

}
