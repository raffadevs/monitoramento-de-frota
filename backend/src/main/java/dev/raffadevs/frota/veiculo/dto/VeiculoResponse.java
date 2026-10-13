package dev.raffadevs.frota.veiculo.dto;

import dev.raffadevs.frota.veiculo.Veiculo;

public record VeiculoResponse(Long id, String placa, String descricao, boolean ativo) {

    public static VeiculoResponse from(Veiculo veiculo) {
        return new VeiculoResponse(veiculo.getId(), veiculo.getPlaca(), veiculo.getDescricao(), veiculo.isAtivo());
    }
}
