package dev.raffadevs.frota.cerca;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Polygon;

import java.math.BigDecimal;

@Entity
@Table(name = "cerca")
public class Cerca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private Polygon area;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 10)
    private TipoAlertaCerca tipoAlerta;

    @Column(name = "velocidade_maxima", precision = 6, scale = 2)
    private BigDecimal velocidadeMaxima;

    @Column(nullable = false)
    private boolean ativa;

    protected Cerca() {
    }

    public Cerca(String nome, Polygon area, TipoAlertaCerca tipoAlerta, BigDecimal velocidadeMaxima) {
        this.nome = nome;
        this.area = area;
        this.tipoAlerta = tipoAlerta;
        this.velocidadeMaxima = velocidadeMaxima;
        this.ativa = true;
    }

    public boolean alertaEm(boolean entrando) {
        return tipoAlerta == TipoAlertaCerca.AMBOS
                || (entrando && tipoAlerta == TipoAlertaCerca.ENTRADA)
                || (!entrando && tipoAlerta == TipoAlertaCerca.SAIDA);
    }

    public boolean temLimiteDeVelocidade() {
        return velocidadeMaxima != null;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Polygon getArea() {
        return area;
    }

    public TipoAlertaCerca getTipoAlerta() {
        return tipoAlerta;
    }

    public BigDecimal getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
