package dev.raffadevs.frota.cerca.dto;

import dev.raffadevs.frota.cerca.Cerca;
import dev.raffadevs.frota.cerca.TipoAlertaCerca;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public record CercaResponse(
        Long id, String nome, TipoAlertaCerca tipoAlerta, BigDecimal velocidadeMaxima,
        List<PontoResponse> pontos
) {
    public record PontoResponse(double latitude, double longitude) {
    }

    public static CercaResponse from(Cerca cerca) {
        List<PontoResponse> pontos = Arrays.stream(cerca.getArea().getCoordinates())
                .map(c -> new PontoResponse(c.getY(), c.getX()))
                .toList();
        return new CercaResponse(cerca.getId(), cerca.getNome(), cerca.getTipoAlerta(),
                cerca.getVelocidadeMaxima(), pontos);
    }
}
