package br.com.escola.modulos.alunos;

import java.util.UUID;

public record Aluno(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String matricula,
        String dataNascimento,
        UUID responsavelId,
        String nomeResponsavel,
        String telefoneResponsavel
) {}
