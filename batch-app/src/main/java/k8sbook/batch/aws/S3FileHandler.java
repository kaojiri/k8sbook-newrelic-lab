package k8sbook.batch.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.newrelic.api.agent.Trace;

@Component
public class S3FileHandler {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ResourceLoader resourceLoader;

    private ResourcePatternResolver resolver;

    @Autowired
    public void setupResolver(ApplicationContext context) {
        resolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, context);
    }

    public Resource[] listFilesInFolder(String bucketName, String folderPath, String filePattern) {
        var s3FolderUrl = "s3://" + bucketName + "/" + folderPath;
        var searchPath = s3FolderUrl + "/" + filePattern;
        try {
            var files = resolver.getResources(searchPath);
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String bucketName, String filePath) {
        amazonS3.deleteObject(bucketName, filePath);
    }

    public void copyFile(String fromBucketName, String fromFilePath, String toBucketName, String toFilePath) {
        amazonS3.copyObject(fromBucketName, fromFilePath, toBucketName, toFilePath);
    }

}
