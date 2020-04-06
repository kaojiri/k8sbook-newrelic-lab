package k8sbook.batch;

import com.opencsv.CSVReader;
import k8sbook.batch.aws.S3FileHandler;
import k8sbook.batch.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.NewRelic;


@SpringBootApplication
public class BatchApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchApplication.class);

    @Value("${sample.app.batch.bucket.name}")
    private String bucketName;

    @Value("${sample.app.batch.folder.name}")
    private String folderName;

    @Autowired
    private Environment env;

    @Autowired
    private LocationService service;

    @Autowired
    private S3FileHandler handler;

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Override
    @Trace(dispatcher = true) // New relicでのTransction取得開始ポイント
    public void run(String... args) throws Exception {
        
        // Transaction名を設定
        NewRelic.setTransactionName("","/k8sbook-batch-app");

        if (Boolean.parseBoolean(env.getProperty("sample.app.batch.run"))) {
            LOGGER.info("======== BATCH APPLICATION START ========");

            Resource[] files = handler.listFilesInFolder(bucketName, folderName, "*");

            var filenameList = new ArrayList<String>();
            Arrays.stream(files).filter(r-> r.isReadable()).forEach(r -> filenameList.add(r.getFilename()));
            LOGGER.info("Following files found to load: " + filenameList.toString());

            // 処理対象のファイル名をCustom Attributesとして送信
            NewRelic.addCustomParameter("test-custom-attribute", filenameList.toString());

            Arrays.stream(files).filter(r -> r.isReadable()).forEach(r -> {
                LOGGER.info("Start processing file: " + r.getFilename());
                try (var isr = new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8)) {
                    var csvReader = new CSVReader(isr);
                    String[] nextLine;
                    var dataList = new ArrayList<String[]>();
                    for (int i=0; (nextLine = csvReader.readNext()) != null; i++) {
                        if (nextLine.length != 3) {
                            LOGGER.warn("invalid format in line " + (i + 1) + " of " + r.getFilename()
                                    + ": " + Arrays.toString(nextLine));
                        } else if (StringUtils.isEmpty(nextLine[1])) {
                            LOGGER.warn("empty string in line " + (i + 1) + " of " + r.getFilename()
                                    + ": " + Arrays.toString(nextLine));
                        } else {
                            dataList.add(nextLine);
                        }
                    }

                    service.registerLocationsFromFile(dataList);

                    handler.deleteFile(bucketName, r.getFilename());
                    LOGGER.info("File deleted: " + r.getFilename());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("End processing file: " + r.getFilename());

            });

            // 一定時間Sleep
            Thread.sleep(15000);

            LOGGER.info("======== BATCH APPLICATION END ========");
        }
    }
}
