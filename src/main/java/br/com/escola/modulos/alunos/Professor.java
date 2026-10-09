package br.com.escola.modulos.alunos;

import java.util.UUID;

public record Professor(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String matriculaFuncional,
        String formacao
) {}
