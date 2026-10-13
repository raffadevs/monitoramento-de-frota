package dev.raffadevs.frota.posicao.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarPosicaoRequest(
        @NotBlank(message = "O dispositivoId é obrigatório") String dispositivoId,

        @NotNull @DecimalMin(value = "-90") @DecimalMax(value = "90")
        BigDecimal latitude,

        @NotNull @DecimalMin(value = "-180") @DecimalMax(value = "180")
        BigDecimal longitude,

        @NotNull @DecimalMin(value = "0") BigDecimal velocidade
) {
}
