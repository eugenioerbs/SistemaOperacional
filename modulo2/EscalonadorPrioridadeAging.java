package modulo2;

import java.util.ArrayList;
import java.util.List;

public class EscalonadorPrioridadeAging {

    public static void executar(
            List<Processo> processos,
            int intervaloAging) {

        if (intervaloAging <= 0) {
            throw new IllegalArgumentException(
                    "O intervalo de aging deve ser positivo."
            );
        }

        if (processos.isEmpty()) {
            System.out.println("Nenhum processo informado.");
            return;
        }

        List<Processo> prontos = new ArrayList<>();

        int tempo = 0;
        int finalizados = 0;

        System.out.println();
        System.out.println("===== PRIORIDADES COM AGING =====");
        System.out.println(
                "Intervalo de envelhecimento: " + intervaloAging
        );

        while (finalizados < processos.size()) {

            atualizarFila(processos, prontos, tempo);

            if (prontos.isEmpty()) {
                // Nenhum processo está pronto para usar a CPU.
                tempo++;
                continue;
            }

            int indice = selecionarMaiorPrioridade(prontos);
            Processo atual = prontos.remove(indice);

            int prioridadeSelecionada =
                    atual.getPrioridadeEfetiva();

            atual.prepararParaExecutar();

            System.out.printf(
                    "[t=%d] %s -> EXECUTANDO (prioridade %d)%n",
                    tempo,
                    atual.getId(),
                    prioridadeSelecionada
            );

            // Os processos que continuam na fila esperam
            // uma unidade de tempo e podem envelhecer.
            for (Processo p : prontos) {
                p.registrarEspera();

                int aumentos = p.aplicarAging(intervaloAging);

                if (aumentos > 0) {
                    System.out.printf(
                            "[t=%d] %s: prioridade efetiva = %d%n",
                            tempo + 1,
                            p.getId(),
                            p.getPrioridadeEfetiva()
                    );
                }
            }

            // Simulação de uma unidade de CPU por vez.
            atual.executarUmaUnidade();
            tempo++;

            // Novos processos entram antes de devolver
            // o processo atual à fila de prontos.
            atualizarFila(processos, prontos, tempo);

            if (atual.getCpuRestante() == 0) {

                atual.marcarFinalizado(tempo);
                finalizados++;

                System.out.printf(
                        "[t=%d] %s -> FINALIZADO%n",
                        tempo, atual.getId()
                );

            } else if (atual.deveBloquearAgora()) {

                atual.marcarBloqueado(tempo);

                System.out.printf(
                        "[t=%d] %s -> BLOQUEADO ate t=%d%n",
                        tempo,
                        atual.getId(),
                        atual.getTempoDesbloqueio()
                );

            } else {

                // Preempcao: a prioridade será reavaliada
                // antes da próxima unidade de CPU.
                atual.marcarPronto();
                prontos.add(atual);
            }
        }

        RelatorioEscalonamento.imprimir(
                "Prioridades com Aging",
                processos
        );
    }

    private static int selecionarMaiorPrioridade(
            List<Processo> prontos) {

        int melhorIndice = 0;

        for (int i = 1; i < prontos.size(); i++) {
            if (prontos.get(i).getPrioridadeEfetiva()
                    > prontos.get(melhorIndice)
                            .getPrioridadeEfetiva()) {

                melhorIndice = i;
            }
        }

        // Em caso de empate, vence o primeiro da lista.
        return melhorIndice;
    }

    private static void atualizarFila(
            List<Processo> processos,
            List<Processo> prontos,
            int tempo) {

        for (Processo p : processos) {

            if (p.getEstado() == Processo.Estado.NOVO
                    && p.getChegada() <= tempo) {

                p.marcarPronto();
                prontos.add(p);

                System.out.printf(
                        "[t=%d] %s -> PRONTO (chegada)%n",
                        tempo, p.getId()
                );

            } else if (p.deveDesbloquear(tempo)) {

                p.marcarPronto();
                prontos.add(p);

                System.out.printf(
                        "[t=%d] %s -> PRONTO (fim da E/S)%n",
                        tempo, p.getId()
                );
            }
        }
    }
}