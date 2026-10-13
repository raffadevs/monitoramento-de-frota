package dev.raffadevs.frota.cerca;

import dev.raffadevs.frota.alerta.Alerta;
import dev.raffadevs.frota.alerta.AlertaRepository;
import dev.raffadevs.frota.alerta.TipoAlerta;
import dev.raffadevs.frota.websocket.RealtimeBroadcaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CercaAvaliadorServiceTest {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private final CercaRepository cercaRepository = mock(CercaRepository.class);
    private final AlertaRepository alertaRepository = mock(AlertaRepository.class);
    private final RealtimeBroadcaster broadcaster = mock(RealtimeBroadcaster.class);
    private final CercaAvaliadorService service =
            new CercaAvaliadorService(cercaRepository, alertaRepository, broadcaster);

    private Cerca cercaAmbos;
    private Point dentro;
    private Point fora;

    @BeforeEach
    void setUp() {
        Polygon quadrado = quadrado(0, 0, 10, 10);
        cercaAmbos = new Cerca("Teste", quadrado, TipoAlertaCerca.AMBOS, BigDecimal.valueOf(60));
        dentro = ponto(5, 5);
        fora = ponto(50, 50);

        when(alertaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void geraAlertaEntrouQuandoVeiculoCruzaParaDentroDaCerca() {
        when(cercaRepository.findByAtivaTrue()).thenReturn(List.of(cercaAmbos));

        service.avaliar(1L, dentro, BigDecimal.valueOf(30), fora);

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoAlerta.ENTROU);
        verify(broadcaster).transmitirAlerta(any());
    }

    @Test
    void geraAlertaSaiuQuandoVeiculoCruzaParaForaDaCerca() {
        when(cercaRepository.findByAtivaTrue()).thenReturn(List.of(cercaAmbos));

        service.avaliar(1L, fora, BigDecimal.valueOf(30), dentro);

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoAlerta.SAIU);
    }

    @Test
    void naoGeraAlertaDeSaidaQuandoCercaSoMonitoraEntrada() {
        Cerca cercaSoEntrada = new Cerca("Teste", quadrado(0, 0, 10, 10), TipoAlertaCerca.ENTRADA, null);
        when(cercaRepository.findByAtivaTrue()).thenReturn(List.of(cercaSoEntrada));

        service.avaliar(1L, fora, BigDecimal.valueOf(30), dentro);

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void naoGeraAlertaQuandoPermaneceDentroDaCercaSemExcesso() {
        when(cercaRepository.findByAtivaTrue()).thenReturn(List.of(cercaAmbos));

        service.avaliar(1L, ponto(5, 6), BigDecimal.valueOf(30), ponto(5, 5));

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void geraAlertaDeExcessoDeVelocidadeQuandoAcimaDoLimiteDentroDaCerca() {
        when(cercaRepository.findByAtivaTrue()).thenReturn(List.of(cercaAmbos));

        service.avaliar(1L, dentro, BigDecimal.valueOf(90), dentro);

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoAlerta.EXCESSO_VELOCIDADE);
    }

    private Point ponto(double x, double y) {
        return GF.createPoint(new Coordinate(x, y));
    }

    private Polygon quadrado(double x0, double y0, double x1, double y1) {
        return GF.createPolygon(new Coordinate[]{
                new Coordinate(x0, y0), new Coordinate(x1, y0),
                new Coordinate(x1, y1), new Coordinate(x0, y1),
                new Coordinate(x0, y0)
        });
    }
}
