package modulo4;

public class MainModulo4 {

    private static GerenciadorBanqueiro criarBanqueiro() {

        String[] processos = {
            "P0", "P1", "P2", "P3", "P4"
        };

        String[] recursos = {
            "A", "B", "C"
        };

        int[] disponiveis = {
            3, 3, 2
        };

        int[][] alocacao = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 2},
            {2, 1, 1},
            {0, 0, 2}
        };

        int[][] maximo = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 2},
            {2, 2, 2},
            {4, 3, 3}
        };

        return new GerenciadorBanqueiro(
                processos,
                recursos,
                disponiveis,
                alocacao,
                maximo
        );
    }

    private static void demonstrarBanqueiro() {

        System.out.println("\n==================================");
        System.out.println("ALGORITMO DO BANQUEIRO");
        System.out.println("==================================");

        GerenciadorBanqueiro inicial = criarBanqueiro();

        System.out.println("\nEstado inicial:");
        inicial.imprimirEstado();
        inicial.imprimirVerificacaoSeguranca();

        // Cenário 1: solicitação que preserva a segurança.
        System.out.println("\n--- Solicitacao segura ---");

        GerenciadorBanqueiro seguro = criarBanqueiro();

        // P1 solicita A=1, B=0, C=2.
        seguro.solicitarRecursos(
                1,
                new int[]{1, 0, 2}
        );

        seguro.imprimirEstado();
        seguro.imprimirVerificacaoSeguranca();

        // Cenário 2: a solicitação tem recursos disponíveis,
        // mas deixaria o estado inseguro.
        System.out.println("\n--- Solicitacao que torna o estado inseguro ---");

        GerenciadorBanqueiro inseguro = criarBanqueiro();

        // P4 solicita A=3, B=3, C=0.
        inseguro.solicitarRecursos(
                4,
                new int[]{3, 3, 0}
        );

        // O estado deve permanecer igual ao estado inicial,
        // porque a solicitação insegura foi desfeita.
        System.out.println("\nEstado depois da tentativa:");
        inseguro.imprimirEstado();
        inseguro.imprimirVerificacaoSeguranca();
    }

    private static void demonstrarGrafo() {

        System.out.println("\n==================================");
        System.out.println("GRAFO DE ALOCACAO DE RECURSOS");
        System.out.println("==================================");

        String[] processos = {
            "P0", "P1", "P2"
        };

        String[] recursos = {
            "R0", "R1", "R2"
        };

        int[][] alocacao = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };

        int[][] solicitacao = {
            {0, 1, 0},
            {0, 0, 1},
            {1, 0, 0}
        };

        GrafoAlocacaoRecursos grafo =
                new GrafoAlocacaoRecursos(
                        processos,
                        recursos,
                        alocacao,
                        solicitacao
                );

        grafo.imprimirArestas();

        System.out.println();

        var ciclo = grafo.encontrarCiclo();

        if (ciclo == null) {
            System.out.println("Nenhum ciclo encontrado.");
        } else {
            System.out.println("Ciclo encontrado: " + ciclo);
            System.out.println(
                    "Com uma instancia por recurso, "
                    + "o ciclo indica deadlock."
            );
        }
    }

    public static void main(String[] args) {
        demonstrarBanqueiro();
        demonstrarGrafo();
    }
}