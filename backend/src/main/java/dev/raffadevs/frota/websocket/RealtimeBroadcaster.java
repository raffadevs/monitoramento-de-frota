package dev.raffadevs.frota.websocket;

import dev.raffadevs.frota.alerta.dto.AlertaResponse;
import dev.raffadevs.frota.posicao.dto.PosicaoResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Ponto único de transmissão em tempo real via STOMP. Mantido separado dos
 * serviços de negócio para que PosicaoService e o avaliador de cercas não
 * precisem conhecer detalhes de WebSocket.
 */
@Component
public class RealtimeBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void transmitirPosicao(PosicaoResponse posicao) {
        messagingTemplate.convertAndSend("/topic/posicoes", posicao);
    }

    public void transmitirAlerta(AlertaResponse alerta) {
        messagingTemplate.convertAndSend("/topic/alertas", alerta);
    }
}
