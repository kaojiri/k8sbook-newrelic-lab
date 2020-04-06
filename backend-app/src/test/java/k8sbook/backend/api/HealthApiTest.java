package k8sbook.backend.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HealthApiTest {

    @Test
    public void testHealthOk() {
        var api = new HealthApi();
        var health = api.getHealth();
        assertThat(health.getStatus()).isEqualTo("OK");
    }

}
