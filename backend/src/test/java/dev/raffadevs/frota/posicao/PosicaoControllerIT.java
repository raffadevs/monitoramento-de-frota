package dev.raffadevs.frota.posicao;

import com.fasterxml.jackson.databind.JsonNode;
import dev.raffadevs.frota.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A cerca "Região Central", seedada pela migração V3, cobre aproximadamente
 * latitude -23.5600 a -23.5670 e longitude -46.6563 a -46.6450.
 */
class PosicaoControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void entrarNaCercaSeedadaGeraAlertaEntrou() {
        String dispositivoId = "device-" + System.nanoTime();

        // fora da cerca (ao norte)
        registrarPosicao(dispositivoId, "-23.5500", "-46.6500", "30");
        // dentro da cerca — deve gerar o alerta de transição
        registrarPosicao(dispositivoId, "-23.5630", "-46.6500", "30");

        ResponseEntity<JsonNode> alertas = restTemplate.getForEntity("/api/alertas", JsonNode.class);
        assertThat(alertas.getStatusCode()).isEqualTo(HttpStatus.OK);

        boolean temAlertaEntrou = false;
        for (JsonNode alerta : alertas.getBody()) {
            if (alerta.get("tipo").asText().equals("ENTROU")) {
                temAlertaEntrou = true;
            }
        }
        assertThat(temAlertaEntrou).isTrue();
    }

    @Test
    void registrarDuasVezesComMesmoDispositivoReutilizaOMesmoVeiculo() {
        String dispositivoId = "device-" + System.nanoTime();

        registrarPosicao(dispositivoId, "-23.5000", "-46.6000", "10");
        registrarPosicao(dispositivoId, "-23.5001", "-46.6001", "15");

        ResponseEntity<JsonNode> veiculos = restTemplate.getForEntity("/api/veiculos", JsonNode.class);
        long quantidadeComEsteDispositivo = 0;
        for (JsonNode veiculo : veiculos.getBody()) {
            if (veiculo.get("descricao").asText().contains(dispositivoId)) {
                quantidadeComEsteDispositivo++;
            }
        }
        assertThat(quantidadeComEsteDispositivo).isEqualTo(1);
    }

    @Test
    void buscaTrajetoRetornaPosicoesNoIntervalo() {
        String dispositivoId = "device-" + System.nanoTime();
        registrarPosicao(dispositivoId, "-23.4000", "-46.5000", "20");

        ResponseEntity<JsonNode> veiculos = restTemplate.getForEntity("/api/veiculos", JsonNode.class);
        Long veiculoId = null;
        for (JsonNode veiculo : veiculos.getBody()) {
            if (veiculo.get("descricao").asText().contains(dispositivoId)) {
                veiculoId = veiculo.get("id").asLong();
            }
        }
        assertThat(veiculoId).isNotNull();

        Instant inicio = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant fim = Instant.now().plus(1, ChronoUnit.HOURS);
        ResponseEntity<JsonNode> trajeto = restTemplate.getForEntity(
                "/api/veiculos/" + veiculoId + "/trajeto?inicio=" + inicio + "&fim=" + fim, JsonNode.class);

        assertThat(trajeto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(trajeto.getBody()).hasSize(1);
    }

    private void registrarPosicao(String dispositivoId, String lat, String lon, String velocidade) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("""
                {"dispositivoId":"%s","latitude":%s,"longitude":%s,"velocidade":%s}
                """.formatted(dispositivoId, lat, lon, velocidade), headers);
        ResponseEntity<JsonNode> resposta = restTemplate.postForEntity("/api/posicoes", request, JsonNode.class);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
