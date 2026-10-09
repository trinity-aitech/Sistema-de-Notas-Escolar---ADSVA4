package br.com.escola.modulos.presenca;

import java.util.UUID;

public record Aula(
        UUID id,
        UUID turmaDisciplinaId,
        String dataAula,
        int quantidadeHoras,
        String conteudoLecionado
) {}
