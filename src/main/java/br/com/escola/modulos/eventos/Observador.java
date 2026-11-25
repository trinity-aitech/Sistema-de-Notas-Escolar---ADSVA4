package br.com.escola.modulos.eventos;

/**
 * Papel no padrao GoF Observer: Observer.
 * Interface comum para todos os ouvintes de eventos da instituicao escolar.
 */
@FunctionalInterface
public interface Observador {
    void atualizar(EventoEscolar e);
}
