package k_paas.balloon.keeper.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ncp")
public record NcpProperty(
        String accessKey,
        String secretKey,
        String bucketName
) {
}
