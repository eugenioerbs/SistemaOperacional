package modulo3;

import java.util.Arrays;

public class GerenciadorMemoria {

    private final int totalPaginasVirtuais;
    private final int tamanhoPagina;
    private final int totalMolduras;
    private final PoliticaSubstituicao politica;

    private final EntradaPagina[] tabelaPaginas;
    private final int[][] memoriaFisica;
    private final int[][] swap;
    private final int[] paginaNaMoldura;

    private long relogio = 0;

    private int faltasPagina = 0;
    private int substituicoes = 0;
    private int leituras = 0;
    private int escritas = 0;

    public GerenciadorMemoria(
            int totalPaginasVirtuais,
            int tamanhoPagina,
            int totalMolduras,
            PoliticaSubstituicao politica) {

        if (totalPaginasVirtuais <= 0
                || tamanhoPagina <= 0
                || totalMolduras <= 0
                || politica == null) {
            throw new IllegalArgumentException(
                    "Parametros de memoria invalidos."
            );
        }

        this.totalPaginasVirtuais = totalPaginasVirtuais;
        this.tamanhoPagina = tamanhoPagina;
        this.totalMolduras = totalMolduras;
        this.politica = politica;

        tabelaPaginas = new EntradaPagina[totalPaginasVirtuais];
        memoriaFisica = new int[totalMolduras][tamanhoPagina];
        swap = new int[totalPaginasVirtuais][tamanhoPagina];
        paginaNaMoldura = new int[totalMolduras];

        Arrays.fill(paginaNaMoldura, -1);

        for (int p = 0; p < totalPaginasVirtuais; p++) {
            tabelaPaginas[p] = new EntradaPagina();

            // Cada pagina comeca no swap, com conteudo
            // previsivel para facilitar a demonstracao.
            for (int d = 0; d < tamanhoPagina; d++) {
                swap[p][d] = p * 100 + d;
            }
        }
    }

    public ResultadoAcesso ler(int enderecoVirtual) {
        return acessar(enderecoVirtual, false, 0);
    }

    public ResultadoAcesso escrever(
            int enderecoVirtual,
            int valor) {

        return acessar(enderecoVirtual, true, valor);
    }

    private ResultadoAcesso acessar(
            int enderecoVirtual,
            boolean escrita,
            int valorEscrito) {

        int tamanhoVirtual =
                totalPaginasVirtuais * tamanhoPagina;

        if (enderecoVirtual < 0
                || enderecoVirtual >= tamanhoVirtual) {
            throw new IllegalArgumentException(
                    "Endereco virtual fora dos limites: "
                    + enderecoVirtual
            );
        }

        long instante = ++relogio;

        int pagina = enderecoVirtual / tamanhoPagina;
        int deslocamento = enderecoVirtual % tamanhoPagina;

        EntradaPagina entrada = tabelaPaginas[pagina];

        boolean faltaPagina = !entrada.estaPresente();
        int paginaSubstituida = -1;

        if (faltaPagina) {
            faltasPagina++;

            int moldura = encontrarMolduraLivre();

            if (moldura == -1) {
                moldura = selecionarVitima();

                paginaSubstituida = paginaNaMoldura[moldura];

                EntradaPagina vitima =
                        tabelaPaginas[paginaSubstituida];

                // Se a pagina foi alterada, salva seu conteudo
                // de volta no swap antes da substituicao.
                if (vitima.estaModificada()) {
                    swap[paginaSubstituida] =
                            memoriaFisica[moldura].clone();
                }

                vitima.retirarDaMemoria();
                substituicoes++;
            }

            // Carrega a pagina solicitada do swap para a RAM.
            memoriaFisica[moldura] = swap[pagina].clone();
            paginaNaMoldura[moldura] = pagina;

            entrada.carregar(moldura, instante);
        }

        entrada = tabelaPaginas[pagina];

        int moldura = entrada.getMoldura();
        int enderecoFisico =
                moldura * tamanhoPagina + deslocamento;

        int valor;

        if (escrita) {
            memoriaFisica[moldura][deslocamento] = valorEscrito;
            entrada.marcarModificada();
            escritas++;

            valor = valorEscrito;
        } else {
            valor = memoriaFisica[moldura][deslocamento];
            leituras++;
        }

        entrada.registrarAcesso(instante);

        return new ResultadoAcesso(
                enderecoVirtual,
                pagina,
                deslocamento,
                moldura,
                enderecoFisico,
                valor,
                faltaPagina,
                paginaSubstituida,
                escrita
        );
    }

    private int encontrarMolduraLivre() {
        for (int i = 0; i < totalMolduras; i++) {
            if (paginaNaMoldura[i] == -1) {
                return i;
            }
        }

        return -1;
    }

    private int selecionarVitima() {
        int melhorMoldura = -1;
        long menorInstante = Long.MAX_VALUE;

        for (int moldura = 0;
                moldura < totalMolduras;
                moldura++) {

            int pagina = paginaNaMoldura[moldura];

            EntradaPagina entrada = tabelaPaginas[pagina];

            long instante;

            if (politica == PoliticaSubstituicao.FIFO) {
                instante = entrada.getInstanteCarga();
            } else {
                instante = entrada.getUltimoAcesso();
            }

            if (instante < menorInstante) {
                menorInstante = instante;
                melhorMoldura = moldura;
            }
        }

        if (melhorMoldura == -1) {
            throw new IllegalStateException(
                    "Nao foi possivel selecionar uma vitima."
            );
        }

        return melhorMoldura;
    }

    public void imprimirTabelaPaginas() {
        System.out.println();
        System.out.println("Tabela de paginas:");

        System.out.printf(
                "%-8s %-10s %-12s %-10s %-14s%n",
                "Pagina",
                "Moldura",
                "Presente",
                "Modificada",
                "Ultimo acesso"
        );

        for (int p = 0; p < totalPaginasVirtuais; p++) {
            EntradaPagina entrada = tabelaPaginas[p];

            String moldura = entrada.estaPresente()
                    ? String.valueOf(entrada.getMoldura())
                    : "-";

            System.out.printf(
                    "%-8d %-10s %-12s %-10s %-14d%n",
                    p,
                    moldura,
                    entrada.estaPresente() ? "Sim" : "Nao",
                    entrada.estaModificada() ? "Sim" : "Nao",
                    entrada.getUltimoAcesso()
            );
        }
    }

    public void imprimirMolduras() {
        System.out.println();
        System.out.println("Memoria fisica:");

        for (int i = 0; i < totalMolduras; i++) {
            if (paginaNaMoldura[i] == -1) {
                System.out.println("Moldura " + i + ": vazia");
            } else {
                System.out.println(
                        "Moldura " + i
                        + " -> pagina " + paginaNaMoldura[i]
                        + " | dados "
                        + Arrays.toString(memoriaFisica[i])
                );
            }
        }
    }

    public void imprimirMetricas() {
        int totalAcessos = leituras + escritas;
        int acertos = totalAcessos - faltasPagina;

        double taxaAcertos = totalAcessos == 0
                ? 0
                : (double) acertos / totalAcessos * 100;

        System.out.println();
        System.out.println("Metricas - " + politica);
        System.out.println("Total de acessos: " + totalAcessos);
        System.out.println("Leituras: " + leituras);
        System.out.println("Escritas: " + escritas);
        System.out.println("Page faults: " + faltasPagina);
        System.out.println("Substituicoes: " + substituicoes);

        System.out.printf(
                "Taxa de acertos: %.2f%%%n",
                taxaAcertos
        );
    }
}