package br.com.escola.modulos.eventos;

import br.com.escola.modulos.regras.BoletimService;
import br.com.escola.modulos.regras.MotorRegrasService.ResultadoAvaliacao;
import br.com.escola.modulos.regras.SituacaoFinal;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Papel no padrao GoF Observer: ConcreteObserver.
 * A cada nota lancada ou presenca registrada, recalcula a situacao do aluno
 * usando o MotorRegrasService (via BoletimService.calcularSituacao) e guarda
 * o resultado mais recente de cada matricula.
 */
public class ObservadorHistorico implements Observador {

    private static final Logger LOGGER = Logger.getLogger(ObservadorHistorico.class.getName());

    private final BoletimService boletimService;
    private final Map<UUID, ResultadoAvaliacao> situacaoAtual = new ConcurrentHashMap<>();

    public ObservadorHistorico() {
        this(new BoletimService());
    }

    public ObservadorHistorico(BoletimService boletimService) {
        this.boletimService = boletimService;
    }

    @Override
    public void atualizar(EventoEscolar e) {
        UUID matriculaId = switch (e) {
            case EventoEscolar.NotaLancada n -> n.matriculaId();
            case EventoEscolar.PresencaRegistrada p -> p.matriculaId();
        };

        ResultadoAvaliacao novo = boletimService.calcularSituacao(matriculaId).resultado();
        ResultadoAvaliacao anterior = situacaoAtual.put(matriculaId, novo);

        SituacaoFinal antes = anterior != null ? anterior.situacao() : null;
        if (antes != novo.situacao()) {
            LOGGER.info("Situacao da matricula " + matriculaId + " mudou de "
                    + (antes != null ? antes.name() : "SEM_REGISTRO") + " para " + novo.situacao().name());
        }
    }

    public Optional<ResultadoAvaliacao> getSituacao(UUID matriculaId) {
        return Optional.ofNullable(situacaoAtual.get(matriculaId));
    }
}
