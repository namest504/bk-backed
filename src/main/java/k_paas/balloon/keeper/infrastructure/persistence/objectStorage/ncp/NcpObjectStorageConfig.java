package k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import k_paas.balloon.keeper.global.property.NcpProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class NcpObjectStorageConfig {

    private final NcpProperty ncpProperty;

    final String endPoint = "https://kr.object.ncloudstorage.com";
    final String regionName = "kr-standard";

    @Bean
    public AmazonS3Client amazonS3Client() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ncpProperty.accessKey(), ncpProperty.secretKey())))
                .build();
    }
}
