package dev.raffadevs.frota.alerta;

import dev.raffadevs.frota.alerta.dto.AlertaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Alertas de entrada/saída de cerca e excesso de velocidade")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    @Operation(summary = "Lista os alertas não lidos, mais recentes primeiro")
    public ResponseEntity<List<AlertaResponse>> listarNaoLidos() {
        return ResponseEntity.ok(alertaService.listarNaoLidos());
    }

    @PatchMapping("/{id}/lido")
    @Operation(summary = "Marca um alerta como lido")
    public ResponseEntity<Void> marcarLido(@PathVariable Long id) {
        alertaService.marcarLido(id);
        return ResponseEntity.noContent().build();
    }
}
