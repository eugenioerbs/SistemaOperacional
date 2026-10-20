package modulo4;

import java.util.ArrayList;
import java.util.List;

public class GrafoAlocacaoRecursos {

    private final String[] processos;
    private final String[] recursos;

    private final List<List<Integer>> adjacencia;
    private final int quantidadeProcessos;
    private final int totalNos;

    public GrafoAlocacaoRecursos(
            String[] processos,
            String[] recursos,
            int[][] alocacao,
            int[][] solicitacao) {

        if (processos == null || processos.length == 0
                || recursos == null || recursos.length == 0
                || alocacao == null || solicitacao == null) {
            throw new IllegalArgumentException(
                    "Dados invalidos para o grafo."
            );
        }

        this.processos = processos.clone();
        this.recursos = recursos.clone();

        quantidadeProcessos = processos.length;
        totalNos = processos.length + recursos.length;

        if (alocacao.length != processos.length
                || solicitacao.length != processos.length) {
            throw new IllegalArgumentException(
                    "Quantidade de linhas invalida."
            );
        }

        for (int i = 0; i < processos.length; i++) {
            if (alocacao[i] == null
                    || solicitacao[i] == null
                    || alocacao[i].length != recursos.length
                    || solicitacao[i].length != recursos.length) {
                throw new IllegalArgumentException(
                        "Dimensoes das matrizes invalidas."
                );
            }
        }

        adjacencia = new ArrayList<>();

        for (int i = 0; i < totalNos; i++) {
            adjacencia.add(new ArrayList<>());
        }

        int[] instanciasAlocadas = new int[recursos.length];

        for (int i = 0; i < processos.length; i++) {
            for (int j = 0; j < recursos.length; j++) {

                int quantidadeAlocada = alocacao[i][j];
                int quantidadeSolicitada = solicitacao[i][j];

                // Este grafo didatico trabalha com uma instancia
                // de cada tipo de recurso.
                if (quantidadeAlocada < 0
                        || quantidadeAlocada > 1
                        || quantidadeSolicitada < 0
                        || quantidadeSolicitada > 1) {
                    throw new IllegalArgumentException(
                            "Este grafo aceita apenas valores 0 ou 1."
                    );
                }

                instanciasAlocadas[j] += quantidadeAlocada;

                if (instanciasAlocadas[j] > 1) {
                    throw new IllegalArgumentException(
                            "Um recurso foi alocado mais de uma vez."
                    );
                }

                // Aresta de recurso para processo:
                // o recurso ja esta alocado.
                if (quantidadeAlocada == 1) {
                    adicionarAresta(
                            indiceRecurso(j),
                            indiceProcesso(i)
                    );
                }

                // Aresta de processo para recurso:
                // existe uma solicitacao pendente.
                if (quantidadeSolicitada == 1) {
                    adicionarAresta(
                            indiceProcesso(i),
                            indiceRecurso(j)
                    );
                }
            }
        }
    }

    private int indiceProcesso(int indice) {
        return indice;
    }

    private int indiceRecurso(int indice) {
        return quantidadeProcessos + indice;
    }

    private void adicionarAresta(int origem, int destino) {
        adjacencia.get(origem).add(destino);
    }

    private String nomeNo(int indice) {
        if (indice < quantidadeProcessos) {
            return processos[indice];
        }

        return recursos[indice - quantidadeProcessos];
    }

    public void imprimirArestas() {
        System.out.println();
        System.out.println("Arestas do grafo:");

        boolean encontrouAresta = false;

        for (int origem = 0; origem < totalNos; origem++) {
            for (int destino : adjacencia.get(origem)) {
                System.out.println(
                        nomeNo(origem) + " -> " + nomeNo(destino)
                );

                encontrouAresta = true;
            }
        }

        if (!encontrouAresta) {
            System.out.println("O grafo nao possui arestas.");
        }
    }

    public List<String> encontrarCiclo() {
        int[] cor = new int[totalNos];
        List<Integer> pilha = new ArrayList<>();

        for (int no = 0; no < totalNos; no++) {
            if (cor[no] == 0) {
                List<Integer> ciclo = buscarCiclo(
                        no, cor, pilha
                );

                if (ciclo != null) {
                    List<String> nomes = new ArrayList<>();

                    for (int indice : ciclo) {
                        nomes.add(nomeNo(indice));
                    }

                    return nomes;
                }
            }
        }

        return null;
    }

    private List<Integer> buscarCiclo(
            int atual,
            int[] cor,
            List<Integer> pilha) {

        // 0 = nao visitado
        // 1 = esta no caminho atual
        // 2 = visita concluida
        cor[atual] = 1;
        pilha.add(atual);

        for (int vizinho : adjacencia.get(atual)) {

            if (cor[vizinho] == 0) {
                List<Integer> ciclo = buscarCiclo(
                        vizinho, cor, pilha
                );

                if (ciclo != null) {
                    return ciclo;
                }

            } else if (cor[vizinho] == 1) {

                // Encontramos uma aresta de volta para um no
                // que ja esta no caminho atual: existe ciclo.
                int inicio = pilha.indexOf(vizinho);

                List<Integer> ciclo = new ArrayList<>(
                        pilha.subList(inicio, pilha.size())
                );

                ciclo.add(vizinho);

                return ciclo;
            }
        }

        pilha.remove(pilha.size() - 1);
        cor[atual] = 2;

        return null;
    }
}