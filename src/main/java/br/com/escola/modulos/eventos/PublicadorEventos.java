package br.com.escola.modulos.eventos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Papel no padrao GoF Observer: Subject.
 * Mantem a colecao de observadores e notifica-os sobre novos eventos de forma thread-safe para Virtual Threads.
 */
public class PublicadorEventos {

    private static final Logger LOGGER = Logger.getLogger(PublicadorEventos.class.getName());

    private final List<Observador> observadores = new CopyOnWriteArrayList<>();

    public void adicionarObservador(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(Observador observador) {
        if (observador != null) {
            observadores.remove(observador);
        }
    }

    public void notificar(EventoEscolar evento) {
        if (evento == null) {
            return;
        }
        for (Observador obs : observadores) {
            try {
                obs.atualizar(evento);
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Erro ao notificar observador: " + obs.getClass().getSimpleName(), ex);
            }
        }
    }

    public List<Observador> getObservadores() {
        return List.copyOf(observadores);
    }
}
