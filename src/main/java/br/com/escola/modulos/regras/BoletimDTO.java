package br.com.escola.modulos.regras;

import java.util.List;
import java.util.UUID;

public record BoletimDTO(
        UUID alunoId,
        String alunoNome,
        String matricula,
        String turmaCodigo,
        String disciplinaNome,
        List<Double> notasPeriodos,
        double mediaAnual,
        Double notaRecuperacao,
        double mediaFinal,
        int totalAulas,
        int totalFaltas,
        double percentualFrequencia,
        String situacaoCodigo,
        String situacaoDescricao,
        boolean alertaInfrequenciaLdb
) {}
