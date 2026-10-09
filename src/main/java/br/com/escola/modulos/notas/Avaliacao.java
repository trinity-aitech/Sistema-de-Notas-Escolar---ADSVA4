package br.com.escola.modulos.notas;

import java.util.UUID;

public record Avaliacao(
        UUID id,
        UUID turmaDisciplinaId,
        int periodo,
        String nome,
        String tipo,
        double peso,
        double notaMaxima,
        String dataAvaliacao
) {}
