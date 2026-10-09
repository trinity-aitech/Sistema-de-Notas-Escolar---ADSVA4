package br.com.escola.modulos.presenca;

import java.util.UUID;

public record Presenca(
        UUID id,
        UUID aulaId,
        UUID matriculaId,
        String alunoNome,
        int numeroChamada,
        boolean presente,
        String justificativa
) {}
