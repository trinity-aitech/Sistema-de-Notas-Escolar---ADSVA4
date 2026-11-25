package br.com.escola.modulos.eventos;

/**
 * Papel no padrao Observer: Concrete Event (Evento Concreto).
 * Representa o evento disparado quando uma nota e lancada para uma matricula.
 */
public record NotaLancada(Long matriculaId, Long avaliacaoId, double valor) implements EventoEscolar {
}
