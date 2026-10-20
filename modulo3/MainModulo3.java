package modulo3;

public class MainModulo3 {

    private static final int PAGINAS_VIRTUAIS = 8;
    private static final int TAMANHO_PAGINA = 4;
    private static final int MOLDURAS = 3;

    // Sequencia de paginas virtuais solicitadas.
    private static final int[] REFERENCIAS = {
        7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2
    };

    private static void executarSimulacao(
            PoliticaSubstituicao politica) {

        GerenciadorMemoria memoria =
                new GerenciadorMemoria(
                        PAGINAS_VIRTUAIS,
                        TAMANHO_PAGINA,
                        MOLDURAS,
                        politica
                );

        System.out.println();
        System.out.println("=================================");
        System.out.println("SIMULACAO " + politica);
        System.out.println("=================================");

        for (int i = 0; i < REFERENCIAS.length; i++) {
            int pagina = REFERENCIAS[i];

            // Variar o deslocamento facilita visualizar
            // a traducao completa do endereco.
            int deslocamento = i % TAMANHO_PAGINA;

            int enderecoVirtual =
                    pagina * TAMANHO_PAGINA + deslocamento;

            ResultadoAcesso resultado =
                    memoria.ler(enderecoVirtual);

            System.out.println(resultado);
        }

        memoria.imprimirMetricas();
        memoria.imprimirTabelaPaginas();
        memoria.imprimirMolduras();
    }

    private static void demonstrarEscritaESwap() {
        System.out.println();
        System.out.println("=================================");
        System.out.println("TESTE DE ESCRITA E PERSISTENCIA");
        System.out.println("=================================");

        GerenciadorMemoria memoria =
                new GerenciadorMemoria(
                        4,
                        4,
                        2,
                        PoliticaSubstituicao.FIFO
                );

        // Escreve 999 na pagina 2, deslocamento 1.
        System.out.println(memoria.escrever(9, 999));

        // Ocupa as molduras e forca substituicoes.
        System.out.println(memoria.ler(0));
        System.out.println(memoria.ler(4));

        // A pagina 2 precisa ser carregada novamente.
        ResultadoAcesso resultado = memoria.ler(9);
        System.out.println(resultado);

        System.out.println(
                "Valor recuperado esperado: 999"
        );

        System.out.println(
                "Valor recuperado obtido: "
                + resultado.getValor()
        );
    }

    public static void main(String[] args) {
        executarSimulacao(PoliticaSubstituicao.FIFO);
        executarSimulacao(PoliticaSubstituicao.LRU);

        demonstrarEscritaESwap();
    }
}