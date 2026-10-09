package br.com.escola.modulos.alunos;

import java.util.UUID;

public record Matricula(
        UUID id,
        UUID alunoId,
        String alunoNome,
        String matriculaNumero,
        UUID turmaId,
        int numeroChamada,
        String status
) {}
