package dev.raffadevs.frota.mqtt;

import java.math.BigDecimal;

/** Formato do payload JSON publicado pelos dispositivos no tópico MQTT. */
public record PosicaoMqttPayload(String dispositivoId, BigDecimal latitude, BigDecimal longitude,
                                  BigDecimal velocidade) {
}
