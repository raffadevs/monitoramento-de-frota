package dev.raffadevs.frota.posicao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PosicaoRepository extends JpaRepository<Posicao, Long> {

    Optional<Posicao> findFirstByVeiculoIdOrderByRegistradoEmDesc(Long veiculoId);

    List<Posicao> findByVeiculoIdAndRegistradoEmBetweenOrderByRegistradoEmAsc(
            Long veiculoId, Instant inicio, Instant fim);

    @Query(value = "SELECT DISTINCT ON (veiculo_id) * FROM posicao ORDER BY veiculo_id, registrado_em DESC",
            nativeQuery = true)
    List<Posicao> buscarUltimaPosicaoDeCadaVeiculo();
}
