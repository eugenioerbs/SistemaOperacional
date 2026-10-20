package modulo3;

public class ResultadoAcesso {

    private final int enderecoVirtual;
    private final int pagina;
    private final int deslocamento;
    private final int moldura;
    private final int enderecoFisico;
    private final int valor;
    private final boolean faltaPagina;
    private final int paginaSubstituida;
    private final boolean escrita;

    public ResultadoAcesso(
            int enderecoVirtual,
            int pagina,
            int deslocamento,
            int moldura,
            int enderecoFisico,
            int valor,
            boolean faltaPagina,
            int paginaSubstituida,
            boolean escrita) {

        this.enderecoVirtual = enderecoVirtual;
        this.pagina = pagina;
        this.deslocamento = deslocamento;
        this.moldura = moldura;
        this.enderecoFisico = enderecoFisico;
        this.valor = valor;
        this.faltaPagina = faltaPagina;
        this.paginaSubstituida = paginaSubstituida;
        this.escrita = escrita;
    }

    public int getValor() {
        return valor;
    }

    public boolean ocorreuFaltaPagina() {
        return faltaPagina;
    }

    public int getEnderecoFisico() {
        return enderecoFisico;
    }

    @Override
    public String toString() {
        String operacao = escrita ? "ESCRITA" : "LEITURA";
        String resultado = faltaPagina
                ? "PAGE FAULT"
                : "HIT";

        String substituicao = paginaSubstituida >= 0
                ? " | substituiu pagina " + paginaSubstituida
                : "";

        return String.format(
                "%s | VA=%d | pagina=%d | deslocamento=%d"
                + " | moldura=%d | PA=%d | valor=%d"
                + " | %s%s",
                operacao,
                enderecoVirtual,
                pagina,
                deslocamento,
                moldura,
                enderecoFisico,
                valor,
                resultado,
                substituicao
        );
    }
}