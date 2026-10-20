package modulo2;

import java.util.ArrayList;
import java.util.List;

public class MainModulo2 {

    private static List<Processo> criarProcessos() {
        List<Processo> processos = new ArrayList<>();

        // ID, chegada, CPU total, prioridade,
        // CPU antes do bloqueio, duração do bloqueio.

        processos.add(new Processo("P1", 0, 7, 2, 3, 2));
        processos.add(new Processo("P2", 1, 4, 4, -1, 0));
        processos.add(new Processo("P3", 2, 5, 1, 2, 3));
        processos.add(new Processo("P4", 3, 6, 3, -1, 0));

        return processos;
    }

    private static List<Processo> copiarProcessos(
            List<Processo> origem) {

        List<Processo> copia = new ArrayList<>();

        for (Processo p : origem) {
            copia.add(p.copiar());
        }

        return copia;
    }

    public static void main(String[] args) {

        List<Processo> configuracao = criarProcessos();

        // Cada algoritmo recebe cópias novas dos processos.
        List<Processo> processosRR =
                copiarProcessos(configuracao);

        List<Processo> processosPrioridade =
                copiarProcessos(configuracao);

        // Primeiro algoritmo: Round-Robin com quantum 3.
        EscalonadorRoundRobin.executar(
                processosRR, 3
        );

        // Segundo algoritmo: Aging a cada 2 unidades de espera.
        EscalonadorPrioridadeAging.executar(
                processosPrioridade, 2
        );
    }
}