package dev.raffadevs.frota.veiculo;

import dev.raffadevs.frota.veiculo.dto.VeiculoComPosicaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@Tag(name = "Veículos", description = "Frota e a última posição conhecida de cada veículo")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    @Operation(summary = "Lista os veículos com sua última posição conhecida (para carregar o mapa)")
    public ResponseEntity<List<VeiculoComPosicaoResponse>> listar() {
        return ResponseEntity.ok(veiculoService.listarComUltimaPosicao());
    }
}
