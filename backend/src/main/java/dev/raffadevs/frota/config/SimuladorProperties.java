package dev.raffadevs.frota.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.simulador")
public class SimuladorProperties {

    private boolean habilitado = false;
    private int veiculos = 30;
    private long intervaloMs = 2000;

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public int getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(int veiculos) {
        this.veiculos = veiculos;
    }

    public long getIntervaloMs() {
        return intervaloMs;
    }

    public void setIntervaloMs(long intervaloMs) {
        this.intervaloMs = intervaloMs;
    }
}
