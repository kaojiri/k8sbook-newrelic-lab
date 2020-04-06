package k8sbook.backend.api;

import k8sbook.backend.entity.LocationEntity;
import k8sbook.backend.entity.RegionEntity;
import k8sbook.backend.repository.LocationRepository;
import k8sbook.backend.repository.RegionRepository;
import k8sbook.backend.service.LocationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocationApi.class, LocationService.class, LocationRepository.class, RegionRepository.class})
public class LocationApiTest {

    @Autowired
    private LocationApi api;

    @MockBean
    private RegionRepository regionRepository;

    @MockBean
    private LocationRepository locationRepository;

    @Test
    public void testGetLocationListByRegion() {
        var regionEntity = new RegionEntity().apply(region -> {
            region.setRegionId(1);
            region.setRegionName("地域1");
        });

        given(regionRepository.findById(1)).willReturn(Optional.of(regionEntity));
        given(locationRepository.findByRegion(regionEntity)).willReturn(List.of(
                new LocationEntity().apply(location -> {
                    location.setLocationId(1L);
                    location.setLocationName("地点1");
                    location.setRegion(regionEntity);
                    location.setNote("地点1の詳細です。");
                }),
                new LocationEntity().apply(location -> {
                    location.setLocationId(1L);
                    location.setLocationName("地点2");
                    location.setRegion(regionEntity);
                    location.setNote("地点2の詳細です。");
                })
        ));

        var result = api.getLocationListByRegion(1);
        assertThat(result.getLocationList()).hasSize(2);
    }

}
