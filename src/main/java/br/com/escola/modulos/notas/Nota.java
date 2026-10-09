package br.com.escola.modulos.notas;

import java.util.UUID;

public record Nota(
        UUID id,
        UUID avaliacaoId,
        UUID matriculaId,
        String alunoNome,
        int periodo,
        String avaliacaoNome,
        double valorNota,
        String observacao
) {}
