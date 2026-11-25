package br.com.escola.modulos.eventos;

/**
 * Papel no padrao Observer: Event (Evento).
 * Define os eventos de dominio disparados pelo sistema escolar.
 */
public sealed interface EventoEscolar
        permits EventoEscolar.NotaLancada, EventoEscolar.PresencaRegistrada {

    record NotaLancada(int matriculaId, int avaliacaoId, double valor) implements EventoEscolar {
    }

    record PresencaRegistrada(int matriculaId, int aulaId, boolean presente) implements EventoEscolar {
    }
}