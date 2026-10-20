package modulo4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerenciadorBanqueiro {

    private final String[] processos;
    private final String[] recursos;

    private final int[] disponiveis;
    private final int[][] alocacao;
    private final int[][] maximo;
    private final int[][] necessidade;

    public GerenciadorBanqueiro(
            String[] processos,
            String[] recursos,
            int[] disponiveis,
            int[][] alocacao,
            int[][] maximo) {

        if (processos == null || processos.length == 0
                || recursos == null || recursos.length == 0
                || disponiveis == null
                || alocacao == null || maximo == null) {
            throw new IllegalArgumentException(
                    "Dados do sistema invalidos."
            );
        }

        int n = processos.length;
        int m = recursos.length;

        if (disponiveis.length != m
                || alocacao.length != n
                || maximo.length != n) {
            throw new IllegalArgumentException(
                    "Dimensoes das matrizes invalidas."
            );
        }

        for (int i = 0; i < n; i++) {
            if (alocacao[i] == null
                    || maximo[i] == null
                    || alocacao[i].length != m
                    || maximo[i].length != m) {
                throw new IllegalArgumentException(
                        "Linha de matriz invalida."
                );
            }
        }

        this.processos = processos.clone();
        this.recursos = recursos.clone();
        this.disponiveis = disponiveis.clone();
        this.alocacao = copiarMatriz(alocacao);
        this.maximo = copiarMatriz(maximo);

        this.necessidade = new int[n][m];

        for (int j = 0; j < m; j++) {
            if (this.disponiveis[j] < 0) {
                throw new IllegalArgumentException(
                        "Recurso disponivel negativo."
                );
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                if (this.alocacao[i][j] < 0
                        || this.maximo[i][j]
                                < this.alocacao[i][j]) {
                    throw new IllegalArgumentException(
                            "Alocacao ou maximo invalido."
                    );
                }

                this.necessidade[i][j] =
                        this.maximo[i][j]
                        - this.alocacao[i][j];
            }
        }
    }

    private static int[][] copiarMatriz(int[][] origem) {
        int[][] copia = new int[origem.length][];

        for (int i = 0; i < origem.length; i++) {
            copia[i] = origem[i].clone();
        }

        return copia;
    }

    private boolean necessidadeCabeNoTrabalho(
            int processo,
            int[] trabalho) {

        for (int j = 0; j < recursos.length; j++) {
            if (necessidade[processo][j] > trabalho[j]) {
                return false;
            }
        }

        return true;
    }

    public List<String> encontrarSequenciaSegura() {

        int[] trabalho = disponiveis.clone();
        boolean[] finalizado = new boolean[processos.length];

        List<String> sequencia = new ArrayList<>();

        while (sequencia.size() < processos.length) {

            boolean encontrouProcesso = false;

            for (int i = 0; i < processos.length; i++) {

                if (finalizado[i]) {
                    continue;
                }

                if (necessidadeCabeNoTrabalho(i, trabalho)) {

                    // Simula o processo terminando e devolvendo
                    // todos os recursos que estavam alocados.
                    for (int j = 0; j < recursos.length; j++) {
                        trabalho[j] += alocacao[i][j];
                    }

                    finalizado[i] = true;
                    sequencia.add(processos[i]);

                    encontrouProcesso = true;
                    break;
                }
            }

            if (!encontrouProcesso) {
                return null;
            }
        }

        return sequencia;
    }

    public boolean solicitarRecursos(
            int indiceProcesso,
            int[] solicitacao) {

        if (indiceProcesso < 0
                || indiceProcesso >= processos.length
                || solicitacao == null
                || solicitacao.length != recursos.length) {
            throw new IllegalArgumentException(
                    "Solicitacao invalida."
            );
        }

        for (int j = 0; j < recursos.length; j++) {

            if (solicitacao[j] < 0) {
                throw new IllegalArgumentException(
                        "Solicitacao negativa."
                );
            }

            if (solicitacao[j] > necessidade[indiceProcesso][j]) {
                System.out.println(
                        "Solicitacao rejeitada: excede a necessidade "
                        + "declarada do processo."
                );
                return false;
            }

            if (solicitacao[j] > disponiveis[j]) {
                System.out.println(
                        "Solicitacao nao concedida: recursos "
                        + "insuficientes no momento."
                );
                return false;
            }
        }

        // Guarda os recursos para desfazer a operacao se
        // o novo estado nao for seguro.
        for (int j = 0; j < recursos.length; j++) {
            disponiveis[j] -= solicitacao[j];
            alocacao[indiceProcesso][j] += solicitacao[j];
            necessidade[indiceProcesso][j] -= solicitacao[j];
        }

        List<String> sequencia = encontrarSequenciaSegura();

        if (sequencia != null) {
            System.out.println(
                    "Solicitacao concedida para "
                    + processos[indiceProcesso] + "."
            );

            System.out.println(
                    "Sequencia segura: " + sequencia
            );

            return true;
        }

        // Rollback: o pedido e desfeito se tornar o estado inseguro.
        for (int j = 0; j < recursos.length; j++) {
            disponiveis[j] += solicitacao[j];
            alocacao[indiceProcesso][j] -= solicitacao[j];
            necessidade[indiceProcesso][j] += solicitacao[j];
        }

        System.out.println(
                "Solicitacao rejeitada: o estado resultante "
                + "nao seria seguro."
        );

        System.out.println(
                "A alocacao original foi restaurada."
        );

        return false;
    }

    public void imprimirVerificacaoSeguranca() {

        List<String> sequencia = encontrarSequenciaSegura();

        if (sequencia == null) {
            System.out.println(
                    "Estado inseguro: nao foi encontrada "
                    + "uma sequencia segura."
            );
        } else {
            System.out.println("Estado seguro.");
            System.out.println(
                    "Sequencia segura: " + sequencia
            );
        }
    }

    public void imprimirEstado() {

        System.out.println();
        System.out.println("Recursos disponiveis: "
                + Arrays.toString(disponiveis));

        System.out.println(
                "Ordem dos recursos: "
                + Arrays.toString(recursos)
        );

        System.out.println();
        System.out.printf(
                "%-8s %-20s %-20s %-20s%n",
                "Processo", "Alocacao", "Maximo", "Necessidade"
        );

        for (int i = 0; i < processos.length; i++) {
            System.out.printf(
                    "%-8s %-20s %-20s %-20s%n",
                    processos[i],
                    Arrays.toString(alocacao[i]),
                    Arrays.toString(maximo[i]),
                    Arrays.toString(necessidade[i])
            );
        }
    }
}