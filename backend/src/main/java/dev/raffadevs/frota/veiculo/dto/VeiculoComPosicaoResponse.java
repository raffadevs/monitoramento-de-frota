package dev.raffadevs.frota.veiculo.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record VeiculoComPosicaoResponse(
        Long id, String placa, String descricao, boolean ativo,
        Double latitude, Double longitude, BigDecimal velocidade, Instant registradoEm
) {
}
