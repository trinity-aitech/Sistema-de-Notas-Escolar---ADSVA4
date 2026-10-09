package br.com.escola.modulos.alunos;

import java.util.UUID;

public record Turma(
        UUID id,
        String codigo,
        int anoLetivo,
        String serie,
        String turno,
        UUID turmaDisciplinaId,
        String disciplinaNome,
        String disciplinaCodigo,
        String professorNome
) {}
