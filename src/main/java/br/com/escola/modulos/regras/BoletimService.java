package br.com.escola.modulos.regras;

import br.com.escola.modulos.alunos.Aluno;
import br.com.escola.modulos.alunos.AlunoDAO;
import br.com.escola.modulos.alunos.Matricula;
import br.com.escola.modulos.alunos.Turma;
import br.com.escola.modulos.alunos.TurmaDAO;
import br.com.escola.modulos.notas.Nota;
import br.com.escola.modulos.notas.NotaDAO;
import br.com.escola.modulos.presenca.PresencaDAO;

import java.util.*;

public class BoletimService {

    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final TurmaDAO turmaDAO = new TurmaDAO();
    private final NotaDAO notaDAO = new NotaDAO();
    private final PresencaDAO presencaDAO = new PresencaDAO();
    private final MotorRegrasService motorRegras = new MotorRegrasService();

    public List<BoletimDTO> gerarBoletimPorAluno(UUID alunoId) {
        List<BoletimDTO> boletins = new ArrayList<>();
        List<Aluno> alunos = alunoDAO.listarTodos();
        Aluno aluno = alunos.stream()
                .filter(a -> a.id().equals(alunoId))
                .findFirst()
                .orElse(null);

        if (aluno == null) {
            return boletins;
        }

        List<Turma> turmas = turmaDAO.listarTodas();

        for (Turma t : turmas) {
            List<Matricula> matriculas = alunoDAO.listarMatriculasPorTurma(t.id());
            Matricula mat = matriculas.stream()
                    .filter(m -> m.alunoId().equals(alunoId))
                    .findFirst()
                    .orElse(null);

            if (mat == null) continue;

            SituacaoMatricula sit = calcularSituacao(mat.id());
            MotorRegrasService.ResultadoAvaliacao res = sit.resultado();

            boletins.add(new BoletimDTO(
                    aluno.id(),
                    aluno.nome(),
                    aluno.matricula(),
                    t.codigo(),
                    t.disciplinaNome(),
                    sit.notasPeriodos(),
                    res.mediaAnual(),
                    res.notaRecuperacao(),
                    res.mediaFinal(),
                    sit.totalAulas(),
                    sit.totalFaltas(),
                    res.percentualFrequencia(),
                    res.situacao().name(),
                    res.situacao().getDescricao(),
                    sit.alertaInfrequencia()
            ));
        }

        return boletins;
    }

    /**
     * Calcula a situação atual de uma matrícula (notas, frequência e situação final).
     * Usado pelo boletim e pelos observers (ObservadorHistorico) para não duplicar a regra.
     */
    public SituacaoMatricula calcularSituacao(UUID matriculaId) {
        // Notas dos 4 períodos
        List<Nota> notasAluno = notaDAO.listarNotasPorMatricula(matriculaId);
        List<Double> notasPeriodos = new ArrayList<>(Arrays.asList(null, null, null, null));
        Double notaRecuperacao = null;

        for (Nota n : notasAluno) {
            if (n.periodo() >= 1 && n.periodo() <= 4) {
                notasPeriodos.set(n.periodo() - 1, n.valorNota());
            } else if (n.periodo() == 5 || "RECUPERACAO".equalsIgnoreCase(n.avaliacaoNome())) {
                notaRecuperacao = n.valorNota();
            }
        }

        // Filtra notas não nulas para cálculo da média
        List<Double> notasValidas = notasPeriodos.stream().filter(Objects::nonNull).toList();
        double mediaAnual = motorRegras.calcularMedia(notasValidas);

        // Aulas e Faltas
        int[] horas = presencaDAO.obterTotalAulasEFaltas(matriculaId);
        int totalAulas = horas[0];
        int totalFaltas = horas[1];

        MotorRegrasService.ResultadoAvaliacao res = motorRegras.avaliarSituacaoFinal(
                mediaAnual,
                totalAulas,
                totalFaltas,
                notaRecuperacao
        );

        // Alerta preventivo se frequência < 80% (Art. 24 da LDB reprova com < 75%)
        boolean alertaLdb = motorRegras.emAlertaInfrequencia(res.percentualFrequencia());

        return new SituacaoMatricula(notasPeriodos, totalAulas, totalFaltas, res, alertaLdb);
    }

    public record SituacaoMatricula(
            List<Double> notasPeriodos,
            int totalAulas,
            int totalFaltas,
            MotorRegrasService.ResultadoAvaliacao resultado,
            boolean alertaInfrequencia
    ) {}
}
