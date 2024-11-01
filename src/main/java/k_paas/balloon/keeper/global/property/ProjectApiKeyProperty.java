package k_paas.balloon.keeper.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "project.api")
public record ProjectApiKeyProperty(String key) {

}
