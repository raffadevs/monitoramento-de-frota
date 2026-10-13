package dev.raffadevs.frota;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Container compartilhado entre todas as subclasses de teste de integração
 * (padrão "singleton container" — ver os demais projetos do portfólio para o
 * porquê de não usar @Testcontainers/@Container aqui).
 *
 * Usa a imagem postgis/postgis (compatível com o protocolo do Postgres, só
 * com a extensão PostGIS pré-instalada) em vez da imagem postgres padrão.
 */
@Import(IntegrationTestBase.TestRestTemplateConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("frota")
            .withUsername("frota")
            .withPassword("frota");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        // Sem broker MQTT nem simulador nos testes: a ingestão é exercitada
        // diretamente pela API HTTP, caminho que compartilha toda a lógica
        // de negócio com o assinante MQTT (ver PosicaoService).
        registry.add("app.mqtt.habilitado", () -> "false");
        registry.add("app.simulador.habilitado", () -> "false");
    }

    @TestConfiguration
    static class TestRestTemplateConfig {

        @Bean
        RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder()
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        }
    }
}
