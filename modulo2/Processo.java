package modulo2;

public class Processo {

    public enum Estado {
        NOVO, PRONTO, EXECUTANDO, BLOQUEADO, FINALIZADO
    }

    private final String id;
    private final int chegada;
    private final int cpuTotal;
    private final int prioridadeBase;

    // Um único evento opcional de E/S por processo.
    private final int cpuAntesDoBloqueio;
    private final int duracaoBloqueio;

    private int cpuRestante;
    private int cpuExecutada;
    private int tempoEspera;
    private int tempoDesbloqueio = -1;
    private int tempoConclusao = -1;

    private int prioridadeEfetiva;
    private int esperaParaAging;
    private boolean bloqueioRealizado = false;

    private Estado estado = Estado.NOVO;

    public Processo(
            String id,
            int chegada,
            int cpuTotal,
            int prioridade,
            int cpuAntesDoBloqueio,
            int duracaoBloqueio) {

        if (id == null || id.trim().isEmpty()
                || chegada < 0 || cpuTotal <= 0) {
            throw new IllegalArgumentException(
                    "Dados invalidos para o processo."
            );
        }

        this.id = id;
        this.chegada = chegada;
        this.cpuTotal = cpuTotal;
        this.prioridadeBase = prioridade;

        // O bloqueio só é válido se ocorrer antes
        // do término da execução e tiver duração positiva.
        if (cpuAntesDoBloqueio > 0
                && cpuAntesDoBloqueio < cpuTotal
                && duracaoBloqueio > 0) {

            this.cpuAntesDoBloqueio = cpuAntesDoBloqueio;
            this.duracaoBloqueio = duracaoBloqueio;

        } else {
            this.cpuAntesDoBloqueio = -1;
            this.duracaoBloqueio = 0;
        }

        this.cpuRestante = cpuTotal;
        this.prioridadeEfetiva = prioridade;
    }

    public Processo copiar() {
        return new Processo(
                id,
                chegada,
                cpuTotal,
                prioridadeBase,
                cpuAntesDoBloqueio,
                duracaoBloqueio
        );
    }

    public void prepararParaExecutar() {
        estado = Estado.EXECUTANDO;

        // Ao receber CPU, a prioridade retorna à base.
        prioridadeEfetiva = prioridadeBase;
        esperaParaAging = 0;
    }

    public void marcarPronto() {
        estado = Estado.PRONTO;
    }

    public void executarUmaUnidade() {
        if (cpuRestante <= 0) {
            throw new IllegalStateException(
                    "O processo nao possui CPU restante."
            );
        }

        cpuRestante--;
        cpuExecutada++;
    }

    public boolean deveBloquearAgora() {
        return !bloqueioRealizado
                && cpuAntesDoBloqueio > 0
                && cpuExecutada == cpuAntesDoBloqueio
                && cpuRestante > 0;
    }

    public void marcarBloqueado(int tempoAtual) {
        bloqueioRealizado = true;
        tempoDesbloqueio = tempoAtual + duracaoBloqueio;
        estado = Estado.BLOQUEADO;
    }

    public boolean deveDesbloquear(int tempoAtual) {
        return estado == Estado.BLOQUEADO
                && tempoDesbloqueio <= tempoAtual;
    }

    public void marcarFinalizado(int tempoAtual) {
        estado = Estado.FINALIZADO;
        tempoConclusao = tempoAtual;
    }

    public void registrarEspera() {
        tempoEspera++;
        esperaParaAging++;
    }

    public int aplicarAging(int intervalo) {
        int aumentos = 0;

        while (esperaParaAging >= intervalo) {
            prioridadeEfetiva++;
            esperaParaAging -= intervalo;
            aumentos++;
        }

        return aumentos;
    }

    public String getId() {
        return id;
    }

    public int getChegada() {
        return chegada;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }

    public int getCpuRestante() {
        return cpuRestante;
    }

    public int getPrioridadeBase() {
        return prioridadeBase;
    }

    public int getPrioridadeEfetiva() {
        return prioridadeEfetiva;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public int getTempoConclusao() {
        return tempoConclusao;
    }

    public int getTempoDesbloqueio() {
        return tempoDesbloqueio;
    }

    public int getTurnaround() {
        return tempoConclusao - chegada;
    }

    public Estado getEstado() {
        return estado;
    }
}