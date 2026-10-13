package dev.raffadevs.frota.alerta;

import dev.raffadevs.frota.alerta.dto.AlertaResponse;
import dev.raffadevs.frota.common.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarNaoLidos() {
        return alertaRepository.findByLidoFalseOrderByOcorridoEmDesc().stream().map(AlertaResponse::from).toList();
    }

    @Transactional
    public void marcarLido(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado"));
        alerta.marcarLido();
    }
}
