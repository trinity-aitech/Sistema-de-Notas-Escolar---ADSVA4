package br.com.escola.modulos.eventos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Papel no padrao GoF Observer: ConcreteObserver.
 * Gera um aviso para o responsavel do aluno quando uma nota e lancada
 * ou quando uma falta e registrada. Os avisos ficam em memoria e no log.
 */
public class ObservadorResponsavel implements Observador {

    private static final Logger LOGGER = Logger.getLogger(ObservadorResponsavel.class.getName());

    private final List<Aviso> avisos = new CopyOnWriteArrayList<>();

    @Override
    public void atualizar(EventoEscolar e) {
        switch (e) {
            case EventoEscolar.NotaLancada n ->
                    registrarAviso(n.matriculaId(), "Nova nota lancada para o aluno: " + n.valor());
            case EventoEscolar.PresencaRegistrada p -> {
                // Presenca normal nao gera aviso; so a falta interessa ao responsavel
                if (!p.presente()) {
                    registrarAviso(p.matriculaId(), "O aluno teve uma falta registrada na aula " + p.aulaId());
                }
            }
        }
    }

    private void registrarAviso(UUID matriculaId, String mensagem) {
        Aviso aviso = new Aviso(matriculaId, mensagem, LocalDateTime.now());
        avisos.add(aviso);
        LOGGER.info("Aviso ao responsavel da matricula " + matriculaId + ": " + mensagem);
    }

    public List<Aviso> getAvisos() {
        return List.copyOf(avisos);
    }

    public List<Aviso> getAvisosPorMatricula(UUID matriculaId) {
        return avisos.stream()
                .filter(a -> a.matriculaId().equals(matriculaId))
                .toList();
    }

    public record Aviso(UUID matriculaId, String mensagem, LocalDateTime dataHora) {
    }
}
