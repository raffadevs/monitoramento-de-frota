package dev.raffadevs.frota.alerta.dto;

import dev.raffadevs.frota.alerta.Alerta;
import dev.raffadevs.frota.alerta.TipoAlerta;

import java.time.Instant;

public record AlertaResponse(
        Long id, Long veiculoId, Long cercaId, TipoAlerta tipo, Instant ocorridoEm, boolean lido
) {
    public static AlertaResponse from(Alerta alerta) {
        return new AlertaResponse(alerta.getId(), alerta.getVeiculoId(), alerta.getCercaId(), alerta.getTipo(),
                alerta.getOcorridoEm(), alerta.isLido());
    }
}
