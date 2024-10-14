package k_paas.balloon.keeper.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "project.api")
public record ApiKeyProperty(String key) {

}
