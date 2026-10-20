package modulo3;

public class EntradaPagina {

    private int moldura = -1;
    private long instanteCarga = -1;
    private long ultimoAcesso = -1;
    private boolean modificada = false;

    public boolean estaPresente() {
        return moldura != -1;
    }

    public void carregar(int moldura, long instante) {
        this.moldura = moldura;
        this.instanteCarga = instante;
        this.ultimoAcesso = instante;
        this.modificada = false;
    }

    public void registrarAcesso(long instante) {
        ultimoAcesso = instante;
    }

    public void marcarModificada() {
        modificada = true;
    }

    public void retirarDaMemoria() {
        moldura = -1;
        instanteCarga = -1;
        ultimoAcesso = -1;
        modificada = false;
    }

    public int getMoldura() {
        return moldura;
    }

    public long getInstanteCarga() {
        return instanteCarga;
    }

    public long getUltimoAcesso() {
        return ultimoAcesso;
    }

    public boolean estaModificada() {
        return modificada;
    }
}