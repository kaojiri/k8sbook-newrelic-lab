package k8sbook.backend.api;

import k8sbook.backend.entity.RegionEntity;
import k8sbook.backend.repository.RegionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegionApiMockTest {

    @MockBean
    private RegionRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllRegions() throws Exception {
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
        mockMvc.perform(get("/region").accept(MediaType.ALL_VALUE))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(readJsonFromFile("RegionApiMockTest_testGetAllRegions.json")));
    }

    private String readJsonFromFile(String fileName) {
        try (var bis = new BufferedInputStream(getClass().getResourceAsStream(fileName))) {
            String jsonString = new String(bis.readAllBytes(), StandardCharsets.UTF_8);
            return jsonString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
