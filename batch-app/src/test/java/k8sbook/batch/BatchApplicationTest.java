package k8sbook.batch;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import k8sbook.batch.annotation.AwsRequired;
import k8sbook.batch.annotation.DbRequired;
import k8sbook.batch.aws.S3FileHandler;
import org.assertj.db.type.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchApplicationTest {

    @Autowired
    private BatchApplication batchApp;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @MockBean
    private S3FileHandler handler;

    @Value("${sample.app.batch.bucket.name}")
    private String bucketName;

    @Value("${sample.app.batch.folder.name}")
    private String folderName;

    @Category({DbRequired.class, AwsRequired.class})
    @Test
    public void testRun() throws Exception {
        given(handler.listFilesInFolder(bucketName, folderName, "*"))
                .willReturn(new Resource[] {
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location1.csv"),
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location2.csv")
                });
        willDoNothing().given(handler).deleteFile(anyString(), anyString());

        batchApp.run();

        var locationTable = new Table(dataSource, "location");
        assertThat(locationTable).hasNumberOfRows(8); // original 4 + file1 3 + file2 1
    }

    @Before
    public void setProperty() {
        // アプリケーション実稼働時は環境変数で設定する想定だが、Spring Bootの
        // プロパティ値はシステムプロパティ、環境変数のいずれでも設定可能であり、
        // 環境変数はテスト内で変更できないことから、システムプロパティを使用する。
        System.setProperty("sample.app.batch.run", "true");
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
                        .build(),
                insertInto("location")
                        .columns("location_id", "location_name", "region_id", "note")
                        .values(1, "地点1", 1, "地点1の詳細です。")
                        .values(2, "地点2", 1, "地点2の詳細です。")
                        .values(3, "地点3", 1, "地点3の詳細です。")
                        .values(4, "地点4", 1, "地点4の詳細です。")
                        .build()
        );
        var dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();

    }

}
