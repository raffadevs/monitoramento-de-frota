package dev.raffadevs.frota.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("monitoramento-de-frota API")
                .description("Rastreamento de veículos em tempo real, cercas virtuais e alertas")
                .version("v1"));
    }
}
