package k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ncp")
public record NcpProperty(
        String accessKey,
        String secretKey,
        String bucketName
) {
}
