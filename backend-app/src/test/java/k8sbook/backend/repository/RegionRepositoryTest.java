package k8sbook.backend.repository;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import k8sbook.backend.annotation.DbRequired;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegionRepositoryTest {

    @Autowired
    private RegionRepository repository;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Category(DbRequired.class)
    @Test
    public void testFindAll() {
        prepareDatabase();

        var result = repository.findAll();
        assertThat(result).hasSize(4);
    }

    @Category(DbRequired.class)
    @Test
    public void testFindByRegionName() {
        prepareDatabase();

        var result = repository.findByRegionName("地域1");
        assertThat(result.get().getRegionId()).isEqualTo(1);
    }

    @Before
    public void prepareDatabase() {
        var operations = sequenceOf(
                deleteAllFrom("location"),
                deleteAllFrom("region"),
                insertInto("region")
                        .columns("region_id", "region_name", "creation_timestamp")
                        .values(1, "地域1", LocalDateTime.now())
                        .values(2, "地域2", LocalDateTime.now())
                        .values(3, "地域3", LocalDateTime.now())
                        .values(4, "地域4", LocalDateTime.now())
                        .build()
        );
        var dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

}
