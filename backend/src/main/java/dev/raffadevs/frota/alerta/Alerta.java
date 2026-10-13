package dev.raffadevs.frota.alerta;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "veiculo_id", nullable = false)
    private Long veiculoId;

    @Column(name = "cerca_id", nullable = false)
    private Long cercaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlerta tipo;

    @Column(name = "ocorrido_em", nullable = false)
    private Instant ocorridoEm;

    @Column(nullable = false)
    private boolean lido;

    protected Alerta() {
    }

    public Alerta(Long veiculoId, Long cercaId, TipoAlerta tipo) {
        this.veiculoId = veiculoId;
        this.cercaId = cercaId;
        this.tipo = tipo;
        this.ocorridoEm = Instant.now();
        this.lido = false;
    }

    public void marcarLido() {
        this.lido = true;
    }

    public Long getId() {
        return id;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public Long getCercaId() {
        return cercaId;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public Instant getOcorridoEm() {
        return ocorridoEm;
    }

    public boolean isLido() {
        return lido;
    }
}
