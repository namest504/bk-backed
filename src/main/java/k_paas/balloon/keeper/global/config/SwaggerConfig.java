package k_paas.balloon.keeper.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(
                        new Server().url("/")
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("BK API Specification")
                .description("Specification")
                .version("1.0.0");
    }
}
