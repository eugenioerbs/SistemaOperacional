package modulo2;

import java.util.List;

public class RelatorioEscalonamento {

    public static void imprimir(
            String algoritmo,
            List<Processo> processos) {

        System.out.println();
        System.out.println("==================================");
        System.out.println("RELATORIO: " + algoritmo);
        System.out.println("==================================");

        if (processos.isEmpty()) {
            System.out.println("Nenhum processo informado.");
            return;
        }

        double somaEspera = 0;
        double somaTurnaround = 0;

        System.out.printf(
                "%-8s %8s %8s %10s %10s%n",
                "Processo",
                "CPU",
                "Espera",
                "Conclusao",
                "Turnaround"
        );

        for (Processo p : processos) {
            int turnaround = p.getTurnaround();

            System.out.printf(
                    "%-8s %8d %8d %10d %10d%n",
                    p.getId(),
                    p.getCpuTotal(),
                    p.getTempoEspera(),
                    p.getTempoConclusao(),
                    turnaround
            );

            somaEspera += p.getTempoEspera();
            somaTurnaround += turnaround;
        }

        System.out.printf(
                "%nTME - Tempo Medio de Espera: %.2f%n",
                somaEspera / processos.size()
        );

        System.out.printf(
                "TMT - Tempo Medio de Turnaround: %.2f%n",
                somaTurnaround / processos.size()
        );
    }
}