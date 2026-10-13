package dev.raffadevs.frota.posicao.dto;

import dev.raffadevs.frota.posicao.Posicao;

import java.math.BigDecimal;
import java.time.Instant;

public record PosicaoResponse(
        Long id, Long veiculoId, double latitude, double longitude, BigDecimal velocidade, Instant registradoEm
) {
    public static PosicaoResponse from(Posicao posicao) {
        return new PosicaoResponse(posicao.getId(), posicao.getVeiculoId(),
                posicao.getPonto().getY(), posicao.getPonto().getX(),
                posicao.getVelocidade(), posicao.getRegistradoEm());
    }
}
