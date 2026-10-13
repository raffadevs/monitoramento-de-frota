package dev.raffadevs.frota.posicao;

import dev.raffadevs.frota.cerca.CercaAvaliadorService;
import dev.raffadevs.frota.posicao.dto.PosicaoResponse;
import dev.raffadevs.frota.veiculo.Veiculo;
import dev.raffadevs.frota.veiculo.VeiculoService;
import dev.raffadevs.frota.websocket.RealtimeBroadcaster;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Caminho único de ingestão de posição, usado tanto pela API HTTP quanto
 * pelo assinante MQTT e pelo simulador — cada um só precisa chamar
 * {@link #registrar}, sem duplicar a lógica de resolução de veículo,
 * persistência, avaliação de cercas e transmissão em tempo real.
 */
@Service
public class PosicaoService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final PosicaoRepository posicaoRepository;
    private final VeiculoService veiculoService;
    private final CercaAvaliadorService cercaAvaliadorService;
    private final RealtimeBroadcaster broadcaster;

    public PosicaoService(PosicaoRepository posicaoRepository, VeiculoService veiculoService,
                           CercaAvaliadorService cercaAvaliadorService, RealtimeBroadcaster broadcaster) {
        this.posicaoRepository = posicaoRepository;
        this.veiculoService = veiculoService;
        this.cercaAvaliadorService = cercaAvaliadorService;
        this.broadcaster = broadcaster;
    }

    @Transactional
    public PosicaoResponse registrar(String dispositivoId, BigDecimal latitude, BigDecimal longitude,
                                      BigDecimal velocidade) {
        Veiculo veiculo = veiculoService.buscarOuRegistrarPorDispositivo(dispositivoId);
        Point pontoAnterior = posicaoRepository.findFirstByVeiculoIdOrderByRegistradoEmDesc(veiculo.getId())
                .map(Posicao::getPonto)
                .orElse(null);

        Point pontoAtual = GEOMETRY_FACTORY.createPoint(
                new Coordinate(longitude.doubleValue(), latitude.doubleValue()));

        Posicao posicao = posicaoRepository.save(new Posicao(veiculo.getId(), pontoAtual, velocidade));

        cercaAvaliadorService.avaliar(veiculo.getId(), pontoAtual, velocidade, pontoAnterior);

        PosicaoResponse resposta = PosicaoResponse.from(posicao);
        broadcaster.transmitirPosicao(resposta);
        return resposta;
    }

    @Transactional(readOnly = true)
    public List<PosicaoResponse> buscarTrajeto(Long veiculoId, Instant inicio, Instant fim) {
        return posicaoRepository.findByVeiculoIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(veiculoId, inicio, fim)
                .stream()
                .map(PosicaoResponse::from)
                .toList();
    }
}
