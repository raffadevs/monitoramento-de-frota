package dev.raffadevs.frota.veiculo;

import dev.raffadevs.frota.common.RecursoNaoEncontradoException;
import dev.raffadevs.frota.posicao.Posicao;
import dev.raffadevs.frota.posicao.PosicaoRepository;
import dev.raffadevs.frota.veiculo.dto.VeiculoComPosicaoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final PosicaoRepository posicaoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository, PosicaoRepository posicaoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.posicaoRepository = posicaoRepository;
    }

    @Transactional(readOnly = true)
    public List<VeiculoComPosicaoResponse> listarComUltimaPosicao() {
        Map<Long, Posicao> ultimaPosicaoPorVeiculo = new HashMap<>();
        for (Posicao posicao : posicaoRepository.buscarUltimaPosicaoDeCadaVeiculo()) {
            ultimaPosicaoPorVeiculo.put(posicao.getVeiculoId(), posicao);
        }

        return veiculoRepository.findAll().stream()
                .map(veiculo -> {
                    Posicao ultima = ultimaPosicaoPorVeiculo.get(veiculo.getId());
                    return new VeiculoComPosicaoResponse(
                            veiculo.getId(), veiculo.getPlaca(), veiculo.getDescricao(), veiculo.isAtivo(),
                            ultima != null ? ultima.getPonto().getY() : null,
                            ultima != null ? ultima.getPonto().getX() : null,
                            ultima != null ? ultima.getVelocidade() : null,
                            ultima != null ? ultima.getRegistradoEm() : null);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Veiculo buscarOuFalhar(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado"));
    }

    /**
     * Ingestão por dispositivo (MQTT, simulador): um rastreador telemetra sua
     * posição sem que alguém tenha previamente cadastrado o veículo na tela
     * administrativa — o primeiro envio já o registra, como um dispositivo
     * de verdade faria.
     */
    @Transactional
    public Veiculo buscarOuRegistrarPorDispositivo(String dispositivoId) {
        return veiculoRepository.findByDispositivoId(dispositivoId)
                .orElseGet(() -> veiculoRepository.save(
                        new Veiculo(gerarPlacaProvisoria(dispositivoId), "Veículo " + dispositivoId, dispositivoId)));
    }

    /**
     * dispositivo_id pode ter até 60 caracteres, mas placa é limitada a 10 —
     * deriva um identificador curto e determinístico (mesmo dispositivo
     * sempre gera a mesma "placa provisória").
     */
    private String gerarPlacaProvisoria(String dispositivoId) {
        String hash = UUID.nameUUIDFromBytes(dispositivoId.getBytes(StandardCharsets.UTF_8))
                .toString().replace("-", "").toUpperCase();
        return "SIM" + hash.substring(0, 7);
    }
}
