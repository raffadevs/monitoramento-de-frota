package dev.raffadevs.frota.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.frota.config.MqttProperties;
import dev.raffadevs.frota.posicao.PosicaoService;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Assina o tópico de posições e também serve de publicador para o simulador
 * (ver {@link dev.raffadevs.frota.simulador.SimuladorService}) — uma única
 * conexão MQTT cumpre os dois papéis.
 *
 * Nunca derruba a aplicação se o broker estiver indisponível: tanto a conexão
 * inicial quanto cada publicação apenas registram um aviso em log. Um painel
 * de monitoramento não deve cair por causa de uma dependência externa fora
 * do ar — deve continuar servindo o que já tem (API HTTP, histórico) e
 * voltar a receber dados assim que o broker normalizar.
 */
@Component
@EnableConfigurationProperties(MqttProperties.class)
public class MqttClientManager {

    private static final Logger log = LoggerFactory.getLogger(MqttClientManager.class);

    private final MqttProperties properties;
    private final PosicaoService posicaoService;
    private final ObjectMapper objectMapper;
    private MqttClient client;

    public MqttClientManager(MqttProperties properties, PosicaoService posicaoService, ObjectMapper objectMapper) {
        this.properties = properties;
        this.posicaoService = posicaoService;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void conectar() {
        if (!properties.isHabilitado()) {
            return;
        }
        try {
            String clientId = properties.getClientIdPrefix() + "-" + UUID.randomUUID();
            client = new MqttClient(properties.getBrokerUrl(), clientId, new MemoryPersistence());
            client.setCallback(new CallbackDeMensagens());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(5);
            client.connect(options);
            client.subscribe(properties.getTopicoPosicoes(), 1);

            log.info("Conectado ao broker MQTT em {} e assinando {}", properties.getBrokerUrl(),
                    properties.getTopicoPosicoes());
        } catch (MqttException e) {
            log.warn("Não foi possível conectar ao broker MQTT em {}: {}. "
                    + "A ingestão continua disponível via API HTTP.", properties.getBrokerUrl(), e.getMessage());
        }
    }

    /** Usado pelo simulador para publicar posições simuladas pelo mesmo canal que um dispositivo real usaria. */
    public void publicar(String topico, String payloadJson) {
        if (client == null || !client.isConnected()) {
            return;
        }
        try {
            client.publish(topico, new MqttMessage(payloadJson.getBytes()));
        } catch (MqttException e) {
            log.warn("Falha ao publicar no MQTT: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void desconectar() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                log.warn("Falha ao desconectar do MQTT: {}", e.getMessage());
            }
        }
    }

    private class CallbackDeMensagens implements MqttCallback {

        // Processa cada mensagem numa virtual thread: o callback do Paho é
        // chamado numa única thread de rede interna da biblioteca, então
        // nunca deve bloquear nela por muito tempo — delega o trabalho
        // (desserializar + persistir + avaliar cercas) para uma virtual
        // thread e devolve o controle imediatamente.
        private final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        @Override
        public void connectionLost(Throwable cause) {
            log.warn("Conexão MQTT perdida: {}", cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            byte[] payload = message.getPayload();
            executor.submit(() -> {
                try {
                    PosicaoMqttPayload corpo = objectMapper.readValue(payload, PosicaoMqttPayload.class);
                    posicaoService.registrar(corpo.dispositivoId(), corpo.latitude(), corpo.longitude(),
                            corpo.velocidade());
                } catch (Exception e) {
                    log.warn("Mensagem MQTT inválida no tópico {}: {}", topic, e.getMessage());
                }
            });
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // não usado: publicações do simulador são fire-and-forget (QoS 0 implícito)
        }
    }
}
