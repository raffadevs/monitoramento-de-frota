package dev.raffadevs.frota.posicao;

import dev.raffadevs.frota.posicao.dto.CriarPosicaoRequest;
import dev.raffadevs.frota.posicao.dto.PosicaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@Tag(name = "Posições", description = "Ingestão de posição via HTTP (ver também o assinante MQTT) e histórico de trajeto")
public class PosicaoController {

    private final PosicaoService posicaoService;

    public PosicaoController(PosicaoService posicaoService) {
        this.posicaoService = posicaoService;
    }

    @PostMapping("/api/posicoes")
    @Operation(summary = "Registra uma posição via HTTP (alternativa ao MQTT)")
    public ResponseEntity<PosicaoResponse> registrar(@Valid @RequestBody CriarPosicaoRequest request) {
        PosicaoResponse resposta = posicaoService.registrar(
                request.dispositivoId(), request.latitude(), request.longitude(), request.velocidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/api/veiculos/{veiculoId}/trajeto")
    @Operation(summary = "Histórico de posições de um veículo num intervalo, para reprodução no mapa")
    public ResponseEntity<List<PosicaoResponse>> trajeto(
            @PathVariable Long veiculoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fim) {
        return ResponseEntity.ok(posicaoService.buscarTrajeto(veiculoId, inicio, fim));
    }
}
