package dev.raffadevs.frota.posicao;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "posicao")
public class Posicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veiculo_id", nullable = false)
    private Long veiculoId;

    @Column(nullable = false)
    private Point ponto;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal velocidade;

    @Column(name = "registrado_em", nullable = false)
    private Instant registradoEm;

    protected Posicao() {
    }

    public Posicao(Long veiculoId, Point ponto, BigDecimal velocidade) {
        this.veiculoId = veiculoId;
        this.ponto = ponto;
        this.velocidade = velocidade;
        this.registradoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public Point getPonto() {
        return ponto;
    }

    public BigDecimal getVelocidade() {
        return velocidade;
    }

    public Instant getRegistradoEm() {
        return registradoEm;
    }
}
