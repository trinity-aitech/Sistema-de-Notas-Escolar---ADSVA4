package br.com.escola.modulos.eventos;

/**
 * Papel no padrao Observer: Observer (Observador).
 * Contrato implementado pelos observadores que reagem aos eventos escolares.
 */
@FunctionalInterface
public interface Observador {

    void atualizar(EventoEscolar e);
}