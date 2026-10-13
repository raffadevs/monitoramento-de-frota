package dev.raffadevs.frota.cerca.dto;

import dev.raffadevs.frota.cerca.TipoAlertaCerca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CriarCercaRequest(
        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotNull(message = "O tipo de alerta é obrigatório") TipoAlertaCerca tipoAlerta,

        BigDecimal velocidadeMaxima,

        @NotNull(message = "A área é obrigatória")
        @Size(min = 3, message = "A área precisa de ao menos 3 pontos")
        List<PontoRequest> pontos
) {
    public record PontoRequest(double latitude, double longitude) {
    }
}
