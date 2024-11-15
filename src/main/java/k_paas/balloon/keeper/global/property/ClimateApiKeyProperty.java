package k_paas.balloon.keeper.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "climate.api")
public record ClimateApiKeyProperty(
        String key
) {
}
