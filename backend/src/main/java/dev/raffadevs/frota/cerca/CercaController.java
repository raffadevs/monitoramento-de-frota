package dev.raffadevs.frota.cerca;

import dev.raffadevs.frota.cerca.dto.CercaResponse;
import dev.raffadevs.frota.cerca.dto.CriarCercaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cercas")
@Tag(name = "Cercas virtuais", description = "Polígonos que disparam alertas de entrada/saída e excesso de velocidade")
public class CercaController {

    private final CercaService cercaService;

    public CercaController(CercaService cercaService) {
        this.cercaService = cercaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma cerca virtual")
    public ResponseEntity<CercaResponse> criar(@Valid @RequestBody CriarCercaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cercaService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Lista todas as cercas")
    public ResponseEntity<List<CercaResponse>> listar() {
        return ResponseEntity.ok(cercaService.listar());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma cerca")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        cercaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
