package dev.raffadevs.frota.alerta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByLidoFalseOrderByOcorridoEmDesc();
}
