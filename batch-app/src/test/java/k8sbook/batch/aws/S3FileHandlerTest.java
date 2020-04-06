package k8sbook.batch.aws;

import k8sbook.batch.annotation.AwsRequired;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class S3FileHandlerTest {

    private static final String BUCKET_NAME = "eks-work-batch";

    @Autowired
    private S3FileHandler handler;

    @Category(AwsRequired.class)
    @Test
    public void testListFilesInForlder() {
        Resource[] files = handler.listFilesInFolder(BUCKET_NAME, "_unittest/forListFiles", "*");
        assertThat(files).filteredOn(resource -> resource.isReadable())
                .extracting(resource -> resource.getFilename())
                .hasSize(3)
                .contains("_unittest/forListFiles/dummyfile.txt")
                .contains("_unittest/forListFiles/testfile")
                .contains("_unittest/forListFiles/テストファイル.CSV");
    }

    @Category(AwsRequired.class)
    @Test
    public void testListFilesInFolderAndRead() throws IOException {
        Resource[] files = handler.listFilesInFolder(BUCKET_NAME, "_unittest/forListFiles", "*");
        Arrays.stream(files).filter(r -> r.getFilename().equals("_unittest/forListFiles/dummyfile.txt"))
                .forEach(r -> {
                    try (var br = new BufferedReader(new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8))) {
                        assertThat(br.readLine()).isEqualTo("dummy");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Category(AwsRequired.class)
    @Test
    public void testCopyFileAndDeleteFile() {
        Resource[] files = handler.listFilesInFolder(BUCKET_NAME, "_unittest/forListFiles", "*");
        Arrays.stream(files).filter(r -> r.getFilename().equals("_unittest/forListFiles/テストファイル.CSV"))
                .forEach(r -> {
                    handler.copyFile(BUCKET_NAME, "_unittest/forListFiles/テストファイル.CSV",
                            BUCKET_NAME, "_unittest/forCopyAndDelete/コピー_テストファイル.CSV");
                    Resource[] copies = handler.listFilesInFolder(BUCKET_NAME, "_unittest/forCopyAndDelete", "*");
                    assertThat(copies).extracting(copy -> copy.getFilename())
                            .contains("_unittest/forCopyAndDelete/コピー_テストファイル.CSV");
                    handler.deleteFile(BUCKET_NAME, "_unittest/forCopyAndDelete/コピー_テストファイル.CSV");
                });
    }

}
