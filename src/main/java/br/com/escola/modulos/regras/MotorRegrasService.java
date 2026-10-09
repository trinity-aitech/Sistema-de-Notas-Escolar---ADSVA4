package br.com.escola.modulos.regras;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Motor de Regras de Negócio do Sistema Escolar.
 * Aplica:
 * 1. LDB (Lei 9.394/1996 - Art. 24, VI): Frequência mínima obrigatória de 75%.
 * 2. Normas do CEE: Arredondamento e critérios de recuperação de estudos.
 * 3. Regulamento da Escola: Média de aprovação direta, exame final e pesos.
 */
public class MotorRegrasService {

    private final RegrasEscolaConfig config;

    public MotorRegrasService() {
        this.config = new RegrasEscolaConfig();
    }

    public MotorRegrasService(RegrasEscolaConfig config) {
        this.config = config != null ? config : new RegrasEscolaConfig();
    }

    /**
     * Calcula a média aritmética ponderada ou simples das notas.
     */
    public double calcularMedia(List<Double> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        double soma = 0.0;
        for (Double n : notas) {
            if (n != null) {
                soma += n;
            }
        }
        double media = soma / notas.size();
        return arredondar(media);
    }

    /**
     * Calcula o percentual de frequência escolar com base nas aulas dadas e faltas.
     */
    public double calcularPercentualFrequencia(int totalAulas, int totalFaltas) {
        if (totalAulas <= 0) {
            return 100.0;
        }
        int presencas = Math.max(0, totalAulas - totalFaltas);
        double perc = ((double) presencas / totalAulas) * 100.0;
        return arredondar(perc);
    }

    /**
     * Avalia a situação final do aluno combinando notas, faltas e exame de recuperação.
     */
    public ResultadoAvaliacao avaliarSituacaoFinal(
            double mediaAnual,
            int totalAulas,
            int totalFaltas,
            Double notaRecuperacao
    ) {
        double freq = calcularPercentualFrequencia(totalAulas, totalFaltas);
        boolean infrequenteLdb = freq < config.getFrequenciaMinimaLdb();

        // 1. Cenário de Aprovação Direta
        if (mediaAnual >= config.getMediaAprovacaoDireta()) {
            if (infrequenteLdb) {
                return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaAnual, freq, SituacaoFinal.REPROVADO_POR_FALTA);
            }
            return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaAnual, freq, SituacaoFinal.APROVADO);
        }

        // 2. Cenário de Reprovação Direta por Nota (sem direito a recuperação)
        if (mediaAnual < config.getMediaMinimaRecuperacao()) {
            if (infrequenteLdb) {
                return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaAnual, freq, SituacaoFinal.REPROVADO_NOTA_E_FALTA);
            }
            return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaAnual, freq, SituacaoFinal.REPROVADO_POR_NOTA);
        }

        // 3. Cenário Elegível para Recuperação (4.0 <= media < 7.0)
        if (notaRecuperacao == null) {
            if (infrequenteLdb) {
                return new ResultadoAvaliacao(mediaAnual, null, mediaAnual, freq, SituacaoFinal.REPROVADO_POR_FALTA);
            }
            return new ResultadoAvaliacao(mediaAnual, null, mediaAnual, freq, SituacaoFinal.RECUPERACAO);
        }

        // 4. Cenário com Exame de Recuperação Realizado
        // Média final = (Média Anual + Nota do Exame) / 2
        double mediaFinal = arredondar((mediaAnual + notaRecuperacao) / 2.0);

        if (mediaFinal >= config.getMediaFinalComRecuperacao()) {
            if (infrequenteLdb) {
                return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaFinal, freq, SituacaoFinal.REPROVADO_POR_FALTA);
            }
            return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaFinal, freq, SituacaoFinal.APROVADO_RECUPERACAO);
        } else {
            if (infrequenteLdb) {
                return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaFinal, freq, SituacaoFinal.REPROVADO_NOTA_E_FALTA);
            }
            return new ResultadoAvaliacao(mediaAnual, notaRecuperacao, mediaFinal, freq, SituacaoFinal.REPROVADO_POR_NOTA);
        }
    }

    public static double arredondar(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public record ResultadoAvaliacao(
            double mediaAnual,
            Double notaRecuperacao,
            double mediaFinal,
            double percentualFrequencia,
            SituacaoFinal situacao
    ) {}

    public RegrasEscolaConfig getConfig() {
        return config;
    }
}
