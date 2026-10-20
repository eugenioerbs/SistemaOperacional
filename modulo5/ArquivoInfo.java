package modulo5;

public class ArquivoInfo {

    private final String nome;
    private final int tamanhoBytes;
    private final int blocoInicial;

    public ArquivoInfo(
            String nome,
            int tamanhoBytes,
            int blocoInicial) {

        this.nome = nome;
        this.tamanhoBytes = tamanhoBytes;
        this.blocoInicial = blocoInicial;
    }

    public String getNome() {
        return nome;
    }

    public int getTamanhoBytes() {
        return tamanhoBytes;
    }

    public int getBlocoInicial() {
        return blocoInicial;
    }
}