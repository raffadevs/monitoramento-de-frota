package dev.raffadevs.frota.veiculo;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 120)
    private String descricao;

    @Column(name = "dispositivo_id", nullable = false, unique = true, length = 60)
    private String dispositivoId;

    @Column(nullable = false)
    private boolean ativo;

    protected Veiculo() {
    }

    public Veiculo(String placa, String descricao, String dispositivoId) {
        this.placa = placa;
        this.descricao = descricao;
        this.dispositivoId = dispositivoId;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDispositivoId() {
        return dispositivoId;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
