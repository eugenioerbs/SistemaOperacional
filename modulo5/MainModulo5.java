package modulo5;

public class MainModulo5 {

    public static void main(String[] args) {

        SistemaArquivosSimulado sistema =
                new SistemaArquivosSimulado(12, 8);

        System.out.println("=== CRIANDO ARQUIVOS ===");

        sistema.escreverArquivo(
                "arquivoA.txt",
                "Conteudo do arquivo A"
        );

        sistema.escreverArquivo(
                "arquivoB.txt",
                "Dados do arquivo B"
        );

        sistema.escreverArquivo(
                "arquivoC.txt",
                "Arquivo C"
        );

        sistema.listarArquivos();
        sistema.imprimirFAT();
        sistema.imprimirBitmap();

        System.out.println("\n=== LENDO ARQUIVO ===");

        System.out.println(
                "Conteudo: "
                + sistema.lerArquivo("arquivoA.txt")
        );

        System.out.println("\n=== ATUALIZANDO ARQUIVO B ===");

        sistema.escreverArquivo(
                "arquivoB.txt",
                "Arquivo B foi atualizado com novos dados!"
        );

        System.out.println(
                "Novo conteudo: "
                + sistema.lerArquivo("arquivoB.txt")
        );

        sistema.listarArquivos();
        sistema.imprimirFAT();
        sistema.imprimirBitmap();

        System.out.println("\n=== EXCLUINDO ARQUIVO C ===");

        sistema.excluirArquivo("arquivoC.txt");

        sistema.listarArquivos();
        sistema.imprimirFAT();
        sistema.imprimirBitmap();

        System.out.println("\nModulo 5 finalizado.");
    }
}