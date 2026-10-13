package dev.raffadevs.frota.cerca;

import dev.raffadevs.frota.alerta.Alerta;
import dev.raffadevs.frota.alerta.AlertaRepository;
import dev.raffadevs.frota.alerta.TipoAlerta;
import dev.raffadevs.frota.alerta.dto.AlertaResponse;
import dev.raffadevs.frota.websocket.RealtimeBroadcaster;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Avalia cada nova posição contra todas as cercas ativas, comparando se o
 * veículo estava dentro da cerca na posição ANTERIOR e se está na posição
 * ATUAL — um alerta de entrada/saída só é gerado na transição, não a cada
 * posição recebida dentro da cerca.
 *
 * O teste de contenção usa JTS em memória (cerca.getArea().contains(ponto)),
 * não uma consulta espacial ao banco: como as cercas ativas já foram
 * carregadas como objetos Polygon pelo Hibernate Spatial, repetir a checagem
 * em Java evita uma ida ao PostGIS por cerca a cada posição — o PostGIS
 * continua sendo a fonte de verdade da geometria (armazenamento + índice
 * GiST), só a checagem "quente" é feita localmente.
 */
@Service
public class CercaAvaliadorService {

    private final CercaRepository cercaRepository;
    private final AlertaRepository alertaRepository;
    private final RealtimeBroadcaster broadcaster;

    public CercaAvaliadorService(CercaRepository cercaRepository, AlertaRepository alertaRepository,
                                  RealtimeBroadcaster broadcaster) {
        this.cercaRepository = cercaRepository;
        this.alertaRepository = alertaRepository;
        this.broadcaster = broadcaster;
    }

    @Transactional
    public void avaliar(Long veiculoId, Point pontoAtual, BigDecimal velocidadeAtual, Point pontoAnterior) {
        for (Cerca cerca : cercaRepository.findByAtivaTrue()) {
            boolean dentroAgora = cerca.getArea().contains(pontoAtual);
            boolean dentroAntes = pontoAnterior != null && cerca.getArea().contains(pontoAnterior);

            if (dentroAgora && !dentroAntes && cerca.alertaEm(true)) {
                registrarAlerta(veiculoId, cerca.getId(), TipoAlerta.ENTROU);
            } else if (!dentroAgora && dentroAntes && cerca.alertaEm(false)) {
                registrarAlerta(veiculoId, cerca.getId(), TipoAlerta.SAIU);
            }

            if (dentroAgora && cerca.temLimiteDeVelocidade()
                    && velocidadeAtual.compareTo(cerca.getVelocidadeMaxima()) > 0) {
                registrarAlerta(veiculoId, cerca.getId(), TipoAlerta.EXCESSO_VELOCIDADE);
            }
        }
    }

    private void registrarAlerta(Long veiculoId, Long cercaId, TipoAlerta tipo) {
        Alerta alerta = alertaRepository.save(new Alerta(veiculoId, cercaId, tipo));
        broadcaster.transmitirAlerta(AlertaResponse.from(alerta));
    }
}
