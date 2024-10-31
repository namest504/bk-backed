package k_paas.balloon.keeper.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("api_key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("BK-API-KEY"))
                        .addParameters("BK-API-KEY", new Parameter()
                                .in("header")
                                .name("BK-API-KEY")
                                .description("API Key")
                                .required(true)
                                .schema(new StringSchema())))
                .info(apiInfo())
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList("api_key"));
    }

    private Info apiInfo() {
        return new Info()
                .title("BK API Specification")
                .description("Specification")
                .version("1.0.0");
    }
}
