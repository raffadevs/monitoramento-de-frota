package dev.raffadevs.frota.simulador;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.frota.config.SimuladorProperties;
import dev.raffadevs.frota.mqtt.MqttClientManager;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gera veículos simulados que se movem por rotas em loop e publicam sua
 * posição via MQTT — o mesmo canal que um rastreador de verdade usaria. Serve
 * tanto para o mapa não começar vazio quanto para provar, numa demonstração
 * ao vivo, que o caminho MQTT → PosicaoService → cercas → WebSocket funciona
 * de ponta a ponta sem precisar de hardware real.
 */
@Service
@EnableConfigurationProperties(SimuladorProperties.class)
public class SimuladorService {

    private static final double AVANCO_POR_TICK = 0.12;

    private final SimuladorProperties properties;
    private final MqttClientManager mqttClientManager;
    private final ObjectMapper objectMapper;
    private final List<EstadoVeiculoSimulado> veiculos = new ArrayList<>();

    public SimuladorService(SimuladorProperties properties, MqttClientManager mqttClientManager,
                             ObjectMapper objectMapper) {
        this.properties = properties;
        this.mqttClientManager = mqttClientManager;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void inicializar() {
        if (!properties.isHabilitado()) {
            return;
        }
        for (int i = 0; i < properties.getVeiculos(); i++) {
            int rota = i % Rotas.TODAS.size();
            veiculos.add(new EstadoVeiculoSimulado("SIM-" + (i + 1), rota, i % Rotas.TODAS.get(rota).size(), 0));
        }
    }

    @Scheduled(fixedDelayString = "${app.simulador.intervalo-ms}")
    void avancarTodos() {
        if (!properties.isHabilitado()) {
            return;
        }
        for (EstadoVeiculoSimulado veiculo : veiculos) {
            avancar(veiculo);
        }
    }

    private void avancar(EstadoVeiculoSimulado veiculo) {
        List<double[]> rota = Rotas.TODAS.get(veiculo.rotaIndex);
        double[] atual = rota.get(veiculo.segmento);
        double[] proximo = rota.get((veiculo.segmento + 1) % rota.size());

        double lat = atual[0] + (proximo[0] - atual[0]) * veiculo.progresso;
        double lon = atual[1] + (proximo[1] - atual[1]) * veiculo.progresso;

        veiculo.progresso += AVANCO_POR_TICK;
        if (veiculo.progresso >= 1.0) {
            veiculo.progresso = 0.0;
            veiculo.segmento = (veiculo.segmento + 1) % rota.size();
        }

        double velocidade = velocidadeSimulada(veiculo.rotaIndex);
        publicar(veiculo.dispositivoId, lat, lon, velocidade);
    }

    /** Rota 0 cruza a cerca com limite de 60 km/h; ocasionalmente ultrapassa, para demonstrar o alerta. */
    private double velocidadeSimulada(int rotaIndex) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        boolean excesso = rotaIndex == 0 && random.nextInt(10) == 0;
        return excesso ? random.nextDouble(65, 90) : random.nextDouble(20, 55);
    }

    private void publicar(String dispositivoId, double lat, double lon, double velocidade) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "dispositivoId", dispositivoId,
                    "latitude", lat,
                    "longitude", lon,
                    "velocidade", velocidade
            ));
            mqttClientManager.publicar("frota/" + dispositivoId + "/posicao", payload);
        } catch (Exception ignored) {
            // serialização de um Map simples não falha na prática; nada a tratar
        }
    }

    private static final class EstadoVeiculoSimulado {
        final String dispositivoId;
        final int rotaIndex;
        int segmento;
        double progresso;

        EstadoVeiculoSimulado(String dispositivoId, int rotaIndex, int segmento, double progresso) {
            this.dispositivoId = dispositivoId;
            this.rotaIndex = rotaIndex;
            this.segmento = segmento;
            this.progresso = progresso;
        }
    }
}
