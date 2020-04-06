package k8sbook.backend.api;

import k8sbook.backend.entity.RegionEntity;
import k8sbook.backend.repository.RegionRepository;
import k8sbook.backend.service.RegionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RegionApi.class, RegionService.class, RegionRepository.class})
public class RegionApiTest {

    @Autowired
    private RegionApi api;

    @MockBean
    private RegionRepository repository;

    @Test
    public void testGetAllRegions() {
        given(repository.findAll()).willReturn(List.of(
                new RegionEntity().apply(entity -> {
                    entity.setRegionId(1);
                    entity.setRegionName("地域1");
                    entity.setCreationTimestamp(LocalDateTime.now());
                }),
                new RegionEntity().apply(entity -> {
                    entity.setRegionId(2);
                    entity.setRegionName("地域2");
                    entity.setCreationTimestamp(LocalDateTime.now());
                })
        ));
        var result = api.getAllRegions();
        assertThat(result.getRegionList()).hasSize(2);
    }

}
