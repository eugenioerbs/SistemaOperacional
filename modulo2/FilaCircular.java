package modulo2;

import java.util.ArrayList;
import java.util.List;

public class FilaCircular {

    private final Processo[] dados;

    private int inicio = 0;
    private int fim = 0;
    private int tamanho = 0;

    public FilaCircular(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException(
                    "A capacidade deve ser positiva."
            );
        }

        dados = new Processo[capacidade];
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void enfileirar(Processo processo) {
        if (tamanho == dados.length) {
            throw new IllegalStateException(
                    "Fila circular cheia."
            );
        }

        dados[fim] = processo;
        fim = (fim + 1) % dados.length;
        tamanho++;
    }

    public Processo desenfileirar() {
        if (estaVazia()) {
            return null;
        }

        Processo processo = dados[inicio];

        dados[inicio] = null;
        inicio = (inicio + 1) % dados.length;
        tamanho--;

        return processo;
    }

    public List<Processo> snapshot() {
        List<Processo> copia = new ArrayList<>();

        for (int i = 0; i < tamanho; i++) {
            int posicao = (inicio + i) % dados.length;
            copia.add(dados[posicao]);
        }

        return copia;
    }
}