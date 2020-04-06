package k8sbook.batch.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.accesskey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretkey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // Spring Cloud AWSのサポート外の機能を使うため、AmazonS3クラスをBeanとして登録しているが、
        // AmazonS3クラスにはSpring Cloudの環境変数で指定した認証情報・リージョンが自動では設定されないため、
        // 明示的に設定している。
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

}
