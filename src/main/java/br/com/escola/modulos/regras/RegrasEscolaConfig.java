package br.com.escola.modulos.regras;

/**
 * Parâmetros de Regras: LDB (Lei 9.394/96), Normas do CEE e Regulamento da Escola.
 */
public class RegrasEscolaConfig {
    // LDB Art. 24, inciso VI: Frequência mínima de 75% do total de horas letivas
    private double frequenciaMinimaLdb = 75.0;

    // Regulamento da Escola
    private double mediaAprovacaoDireta = 7.0;
    private double mediaMinimaRecuperacao = 4.0;
    private double mediaFinalComRecuperacao = 5.0;
    private int quantidadePeriodos = 4; // Bimestral

    public RegrasEscolaConfig() {}

    public RegrasEscolaConfig(double frequenciaMinimaLdb, double mediaAprovacaoDireta, double mediaMinimaRecuperacao, double mediaFinalComRecuperacao, int quantidadePeriodos) {
        this.frequenciaMinimaLdb = frequenciaMinimaLdb;
        this.mediaAprovacaoDireta = mediaAprovacaoDireta;
        this.mediaMinimaRecuperacao = mediaMinimaRecuperacao;
        this.mediaFinalComRecuperacao = mediaFinalComRecuperacao;
        this.quantidadePeriodos = quantidadePeriodos;
    }

    public double getFrequenciaMinimaLdb() {
        return frequenciaMinimaLdb;
    }

    public void setFrequenciaMinimaLdb(double frequenciaMinimaLdb) {
        this.frequenciaMinimaLdb = frequenciaMinimaLdb;
    }

    public double getMediaAprovacaoDireta() {
        return mediaAprovacaoDireta;
    }

    public void setMediaAprovacaoDireta(double mediaAprovacaoDireta) {
        this.mediaAprovacaoDireta = mediaAprovacaoDireta;
    }

    public double getMediaMinimaRecuperacao() {
        return mediaMinimaRecuperacao;
    }

    public void setMediaMinimaRecuperacao(double mediaMinimaRecuperacao) {
        this.mediaMinimaRecuperacao = mediaMinimaRecuperacao;
    }

    public double getMediaFinalComRecuperacao() {
        return mediaFinalComRecuperacao;
    }

    public void setMediaFinalComRecuperacao(double mediaFinalComRecuperacao) {
        this.mediaFinalComRecuperacao = mediaFinalComRecuperacao;
    }

    public int getQuantidadePeriodos() {
        return quantidadePeriodos;
    }

    public void setQuantidadePeriodos(int quantidadePeriodos) {
        this.quantidadePeriodos = quantidadePeriodos;
    }
}
