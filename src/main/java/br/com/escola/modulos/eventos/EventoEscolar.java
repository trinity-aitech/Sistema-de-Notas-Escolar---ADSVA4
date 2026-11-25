package br.com.escola.modulos.eventos;

import java.util.UUID;

/**
 * Papel no padrao GoF Observer: Event.
 * Contrato selado que encapsula os eventos ocorridos no dominio escolar.
 */
public sealed interface EventoEscolar permits EventoEscolar.NotaLancada, EventoEscolar.PresencaRegistrada {

    record NotaLancada(
            UUID matriculaId,
            UUID avaliacaoId,
            double valor
    ) implements EventoEscolar {}

    record PresencaRegistrada(
            UUID matriculaId,
            UUID aulaId,
            boolean presente
    ) implements EventoEscolar {}
}
