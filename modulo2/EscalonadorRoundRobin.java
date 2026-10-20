package modulo2;

import java.util.List;

public class EscalonadorRoundRobin {

    public static void executar(
            List<Processo> processos,
            int quantum) {

        if (quantum <= 0) {
            throw new IllegalArgumentException(
                    "O quantum deve ser maior que zero."
            );
        }

        if (processos.isEmpty()) {
            System.out.println("Nenhum processo informado.");
            return;
        }

        FilaCircular prontos =
                new FilaCircular(processos.size());

        int tempo = 0;
        int finalizados = 0;
        int usadoQuantum = 0;

        Processo atual = null;

        System.out.println();
        System.out.println("===== ROUND-ROBIN =====");
        System.out.println("Quantum: " + quantum);

        while (finalizados < processos.size()) {

            atualizarFila(processos, prontos, tempo);

            if (atual == null) {
                if (prontos.estaVazia()) {
                    // A CPU fica ociosa até o próximo evento.
                    tempo++;
                    continue;
                }

                atual = prontos.desenfileirar();
                atual.prepararParaExecutar();
                usadoQuantum = 0;

                System.out.printf(
                        "[t=%d] %s -> EXECUTANDO%n",
                        tempo, atual.getId()
                );
            }

            // Os demais processos prontos esperam uma unidade.
            for (Processo p : prontos.snapshot()) {
                p.registrarEspera();
            }

            atual.executarUmaUnidade();
            tempo++;
            usadoQuantum++;

            System.out.printf(
                    "[t=%d] %s executou; CPU restante: %d%n",
                    tempo,
                    atual.getId(),
                    atual.getCpuRestante()
            );

            // A conclusão tem prioridade sobre o bloqueio.
            if (atual.getCpuRestante() == 0) {
                atual.marcarFinalizado(tempo);
                finalizados++;

                System.out.printf(
                        "[t=%d] %s -> FINALIZADO%n",
                        tempo, atual.getId()
                );

                atual = null;
                continue;
            }

            // Um processo pode abandonar a CPU para esperar E/S.
            if (atual.deveBloquearAgora()) {
                atual.marcarBloqueado(tempo);

                System.out.printf(
                        "[t=%d] %s -> BLOQUEADO ate t=%d%n",
                        tempo,
                        atual.getId(),
                        atual.getTempoDesbloqueio()
                );

                atual = null;
                continue;
            }

            // Se o quantum terminar, aplicamos a preempcao.
            if (usadoQuantum >= quantum) {

                // Eventos no instante atual entram antes
                // do processo que acabou de perder a CPU.
                atualizarFila(processos, prontos, tempo);

                atual.marcarPronto();
                prontos.enfileirar(atual);

                System.out.printf(
                        "[t=%d] Quantum expirou: %s -> PRONTO%n",
                        tempo, atual.getId()
                );

                atual = null;
            }
        }

        RelatorioEscalonamento.imprimir(
                "Round-Robin",
                processos
        );
    }

    private static void atualizarFila(
            List<Processo> processos,
            FilaCircular prontos,
            int tempo) {

        for (Processo p : processos) {

            if (p.getEstado() == Processo.Estado.NOVO
                    && p.getChegada() <= tempo) {

                p.marcarPronto();
                prontos.enfileirar(p);

                System.out.printf(
                        "[t=%d] %s -> PRONTO (chegada)%n",
                        tempo, p.getId()
                );

            } else if (p.deveDesbloquear(tempo)) {

                p.marcarPronto();
                prontos.enfileirar(p);

                System.out.printf(
                        "[t=%d] %s -> PRONTO (fim da E/S)%n",
                        tempo, p.getId()
                );
            }
        }
    }
}