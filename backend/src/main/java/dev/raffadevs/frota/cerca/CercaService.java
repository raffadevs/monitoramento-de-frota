package dev.raffadevs.frota.cerca;

import dev.raffadevs.frota.cerca.dto.CercaResponse;
import dev.raffadevs.frota.cerca.dto.CriarCercaRequest;
import dev.raffadevs.frota.common.RecursoNaoEncontradoException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CercaService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final CercaRepository cercaRepository;

    public CercaService(CercaRepository cercaRepository) {
        this.cercaRepository = cercaRepository;
    }

    @Transactional
    public CercaResponse criar(CriarCercaRequest request) {
        Polygon area = construirPoligono(request.pontos());
        Cerca cerca = cercaRepository.save(new Cerca(request.nome(), area, request.tipoAlerta(),
                request.velocidadeMaxima()));
        return CercaResponse.from(cerca);
    }

    @Transactional(readOnly = true)
    public List<CercaResponse> listar() {
        return cercaRepository.findAll().stream().map(CercaResponse::from).toList();
    }

    @Transactional
    public void excluir(Long id) {
        if (!cercaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cerca não encontrada");
        }
        cercaRepository.deleteById(id);
    }

    private Polygon construirPoligono(List<CriarCercaRequest.PontoRequest> pontos) {
        List<Coordinate> coordenadas = new ArrayList<>();
        for (CriarCercaRequest.PontoRequest ponto : pontos) {
            coordenadas.add(new Coordinate(ponto.longitude(), ponto.latitude()));
        }
        Coordinate primeiro = coordenadas.get(0);
        Coordinate ultimo = coordenadas.get(coordenadas.size() - 1);
        if (!primeiro.equals2D(ultimo)) {
            coordenadas.add(new Coordinate(primeiro.x, primeiro.y)); // fecha o anel, exigido pelo tipo Polygon
        }
        return GEOMETRY_FACTORY.createPolygon(coordenadas.toArray(new Coordinate[0]));
    }
}
