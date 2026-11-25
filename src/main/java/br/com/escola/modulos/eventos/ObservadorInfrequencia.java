package br.com.escola.modulos.eventos;

import br.com.escola.modulos.presenca.PresencaDAO;
import br.com.escola.modulos.regras.MotorRegrasService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Papel no padrao GoF Observer: ConcreteObserver.
 * Ao receber PresencaRegistrada, recalcula a frequencia do aluno e gera um alerta
 * quando ela fica abaixo do limite preventivo (80%). Usa a mesma regra do boletim
 * (MotorRegrasService.emAlertaInfrequencia), sem duplicar a logica.
 * Ignora os demais eventos.
 */
public class ObservadorInfrequencia implements Observador {

    private static final Logger LOGGER = Logger.getLogger(ObservadorInfrequencia.class.getName());

    private final PresencaDAO presencaDAO;
    private final MotorRegrasService motorRegras;
    private final List<AlertaInfrequencia> alertas = new CopyOnWriteArrayList<>();

    public ObservadorInfrequencia() {
        this(new PresencaDAO(), new MotorRegrasService());
    }

    public ObservadorInfrequencia(PresencaDAO presencaDAO, MotorRegrasService motorRegras) {
        this.presencaDAO = presencaDAO;
        this.motorRegras = motorRegras;
    }

    @Override
    public void atualizar(EventoEscolar e) {
        if (!(e instanceof EventoEscolar.PresencaRegistrada p)) {
            return;
        }

        int[] horas = presencaDAO.obterTotalAulasEFaltas(p.matriculaId());
        double frequencia = motorRegras.calcularPercentualFrequencia(horas[0], horas[1]);

        if (motorRegras.emAlertaInfrequencia(frequencia)) {
            AlertaInfrequencia alerta = new AlertaInfrequencia(p.matriculaId(), frequencia, LocalDateTime.now());
            alertas.add(alerta);
            LOGGER.warning("Alerta de infrequencia: matricula " + p.matriculaId()
                    + " com frequencia de " + frequencia + "%");
        }
    }

    public List<AlertaInfrequencia> getAlertas() {
        return List.copyOf(alertas);
    }

    public record AlertaInfrequencia(UUID matriculaId, double percentualFrequencia, LocalDateTime dataHora) {
    }
}
