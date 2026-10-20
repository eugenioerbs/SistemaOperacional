package modulo5;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaArquivosSimulado {

    private static final int EOF = -1;
    private static final int LIVRE = -2;

    private final int quantidadeBlocos;
    private final int tamanhoBloco;

    // FAT: indice = bloco; valor = proximo bloco.
    private final int[] fat;

    // true = livre; false = ocupado.
    private final boolean[] bitmap;

    // Conteudo de cada bloco do disco simulado.
    private final byte[][] disco;

    // Diretório que associa nomes aos metadados.
    private final Map<String, ArquivoInfo> diretorio =
            new LinkedHashMap<>();

    public SistemaArquivosSimulado(
            int quantidadeBlocos,
            int tamanhoBloco) {

        if (quantidadeBlocos <= 0 || tamanhoBloco <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade e o tamanho dos blocos "
                    + "devem ser positivos."
            );
        }

        this.quantidadeBlocos = quantidadeBlocos;
        this.tamanhoBloco = tamanhoBloco;

        fat = new int[quantidadeBlocos];
        bitmap = new boolean[quantidadeBlocos];
        disco = new byte[quantidadeBlocos][tamanhoBloco];

        Arrays.fill(fat, LIVRE);
        Arrays.fill(bitmap, true);
    }

    // Cria um arquivo ou substitui o conteudo de um existente.
    public void escreverArquivo(String nome, String conteudo) {

        validarNome(nome);

        if (conteudo == null) {
            throw new IllegalArgumentException(
                    "O conteudo nao pode ser nulo."
            );
        }

        byte[] dados = conteudo.getBytes(StandardCharsets.UTF_8);

        int blocosNecessarios = (int) (
                (dados.length + (long) tamanhoBloco - 1)
                / tamanhoBloco
        );

        ArquivoInfo anterior = diretorio.get(nome);

        List<Integer> cadeiaAntiga =
                anterior == null
                ? new ArrayList<>()
                : obterCadeia(anterior.getBlocoInicial());

        // Considera também os blocos que serão liberados
        // caso o arquivo já exista.
        if (contarBlocosLivres() + cadeiaAntiga.size()
                < blocosNecessarios) {

            throw new IllegalStateException(
                    "Espaco insuficiente. Arquivo nao gravado."
            );
        }

        // Só altera o arquivo depois de verificar a capacidade.
        if (anterior != null) {
            liberarCadeia(anterior.getBlocoInicial());
        }

        List<Integer> novaCadeia =
                alocarCadeia(blocosNecessarios);

        int blocoInicial = novaCadeia.isEmpty()
                ? EOF
                : novaCadeia.get(0);

        int copiados = 0;

        for (int bloco : novaCadeia) {
            int quantidade = Math.min(
                    tamanhoBloco,
                    dados.length - copiados
            );

            if (quantidade > 0) {
                System.arraycopy(
                        dados,
                        copiados,
                        disco[bloco],
                        0,
                        quantidade
                );

                copiados += quantidade;
            }
        }

        diretorio.put(
                nome,
                new ArquivoInfo(
                        nome,
                        dados.length,
                        blocoInicial
                )
        );

        System.out.println(
                "Arquivo gravado: " + nome
                + " (" + dados.length + " bytes)"
        );
    }

    // Percorre a FAT e recupera o conteúdo do arquivo.
    public String lerArquivo(String nome) {

        ArquivoInfo arquivo = diretorio.get(nome);

        if (arquivo == null) {
            throw new IllegalArgumentException(
                    "Arquivo nao encontrado: " + nome
            );
        }

        byte[] dados = new byte[arquivo.getTamanhoBytes()];

        List<Integer> cadeia =
                obterCadeia(arquivo.getBlocoInicial());

        int copiados = 0;

        for (int bloco : cadeia) {
            int quantidade = Math.min(
                    tamanhoBloco,
                    dados.length - copiados
            );

            if (quantidade <= 0) {
                break;
            }

            System.arraycopy(
                    disco[bloco],
                    0,
                    dados,
                    copiados,
                    quantidade
            );

            copiados += quantidade;
        }

        if (copiados != dados.length) {
            throw new IllegalStateException(
                    "A cadeia FAT nao contem todos os dados."
            );
        }

        return new String(dados, StandardCharsets.UTF_8);
    }

    public boolean excluirArquivo(String nome) {

        ArquivoInfo arquivo = diretorio.get(nome);

        if (arquivo == null) {
            System.out.println(
                    "Arquivo nao encontrado: " + nome
            );

            return false;
        }

        liberarCadeia(arquivo.getBlocoInicial());
        diretorio.remove(nome);

        System.out.println("Arquivo excluido: " + nome);

        return true;
    }

    private List<Integer> alocarCadeia(int quantidade) {

        List<Integer> cadeia = new ArrayList<>();

        for (int i = 0; i < quantidadeBlocos
                && cadeia.size() < quantidade; i++) {

            if (bitmap[i]) {
                bitmap[i] = false;
                fat[i] = EOF;

                cadeia.add(i);
            }
        }

        if (cadeia.size() != quantidade) {
            throw new IllegalStateException(
                    "Falha ao alocar a cadeia FAT."
            );
        }

        // Liga os blocos e marca o final com EOF.
        for (int i = 0; i < cadeia.size(); i++) {

            if (i + 1 < cadeia.size()) {
                fat[cadeia.get(i)] = cadeia.get(i + 1);
            } else {
                fat[cadeia.get(i)] = EOF;
            }
        }

        return cadeia;
    }

    private List<Integer> obterCadeia(int blocoInicial) {

        List<Integer> cadeia = new ArrayList<>();

        if (blocoInicial == EOF) {
            return cadeia;
        }

        boolean[] visitados = new boolean[quantidadeBlocos];
        int atual = blocoInicial;

        while (atual != EOF) {

            if (atual < 0 || atual >= quantidadeBlocos) {
                throw new IllegalStateException(
                        "Indice de bloco invalido na FAT."
                );
            }

            if (visitados[atual] || bitmap[atual]) {
                throw new IllegalStateException(
                        "Cadeia FAT invalida ou com ciclo."
                );
            }

            visitados[atual] = true;
            cadeia.add(atual);

            atual = fat[atual];

            if (atual == LIVRE) {
                throw new IllegalStateException(
                        "A FAT aponta para um bloco livre."
                );
            }
        }

        return cadeia;
    }

    private void liberarCadeia(int blocoInicial) {

        List<Integer> cadeia = obterCadeia(blocoInicial);

        for (int bloco : cadeia) {
            fat[bloco] = LIVRE;
            bitmap[bloco] = true;

            Arrays.fill(disco[bloco], (byte) 0);
        }
    }

    private void validarNome(String nome) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O nome do arquivo nao pode ser vazio."
            );
        }
    }

    public int contarBlocosLivres() {

        int livres = 0;

        for (boolean livre : bitmap) {
            if (livre) {
                livres++;
            }
        }

        return livres;
    }

    public void listarArquivos() {

        System.out.println("\n=== DIRETORIO ===");

        if (diretorio.isEmpty()) {
            System.out.println("Nenhum arquivo cadastrado.");
            return;
        }

        for (ArquivoInfo arquivo : diretorio.values()) {

            List<Integer> cadeia =
                    obterCadeia(arquivo.getBlocoInicial());

            System.out.printf(
                    "%s | %d bytes | Bloco inicial: %s"
                    + " | Cadeia: %s -> EOF%n",
                    arquivo.getNome(),
                    arquivo.getTamanhoBytes(),
                    arquivo.getBlocoInicial() == EOF
                            ? "nenhum"
                            : arquivo.getBlocoInicial(),
                    cadeia
            );
        }
    }

    public void imprimirFAT() {

        System.out.println("\n=== TABELA FAT ===");

        System.out.printf("%-10s %-15s%n", "Bloco", "Proximo");

        for (int i = 0; i < quantidadeBlocos; i++) {

            String proximo;

            if (fat[i] == LIVRE) {
                proximo = "LIVRE";
            } else if (fat[i] == EOF) {
                proximo = "EOF";
            } else {
                proximo = String.valueOf(fat[i]);
            }

            System.out.printf(
                    "%-10d %-15s%n",
                    i,
                    proximo
            );
        }
    }

    public void imprimirBitmap() {

        System.out.println("\n=== BITMAP ===");
        System.out.println("1 = livre; 0 = ocupado");

        for (int i = 0; i < quantidadeBlocos; i++) {
            System.out.printf(
                    "Bloco %02d: %d%n",
                    i,
                    bitmap[i] ? 1 : 0
            );
        }

        System.out.println(
                "Total de blocos livres: " + contarBlocosLivres()
        );
    }
}