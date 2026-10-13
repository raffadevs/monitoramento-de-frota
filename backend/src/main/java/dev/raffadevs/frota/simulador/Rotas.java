package dev.raffadevs.frota.simulador;

import java.util.List;

/**
 * Rotas de demonstração (não são dados reais de mapa) em torno da região das
 * cercas virtuais seedadas, para que o simulador gere alertas de
 * entrada/saída observáveis numa demonstração ao vivo.
 */
final class Rotas {

    static final List<List<double[]>> TODAS = List.of(
            List.of( // cruza a "Região Central"
                    new double[]{-23.5550, -46.6600},
                    new double[]{-23.5620, -46.6600},
                    new double[]{-23.5620, -46.6480},
                    new double[]{-23.5550, -46.6480},
                    new double[]{-23.5550, -46.6600}
            ),
            List.of( // cruza a "Zona de Entrega"
                    new double[]{-23.5530, -46.6720},
                    new double[]{-23.5600, -46.6720},
                    new double[]{-23.5600, -46.6580},
                    new double[]{-23.5530, -46.6580},
                    new double[]{-23.5530, -46.6720}
            ),
            List.of( // rota externa, fora de qualquer cerca
                    new double[]{-23.5450, -46.6400},
                    new double[]{-23.5450, -46.6300},
                    new double[]{-23.5380, -46.6300},
                    new double[]{-23.5380, -46.6400},
                    new double[]{-23.5450, -46.6400}
            )
    );

    private Rotas() {
    }
}
