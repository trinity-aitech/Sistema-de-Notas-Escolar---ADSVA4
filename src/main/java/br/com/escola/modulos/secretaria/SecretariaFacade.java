package br.com.escola.modulos.secretaria;

import br.com.escola.modulos.alunos.AlunoDAO;
import br.com.escola.modulos.alunos.Matricula;
import br.com.escola.modulos.alunos.Turma;
import br.com.escola.modulos.alunos.TurmaDAO;
import br.com.escola.modulos.eventos.EventoEscolar;
import br.com.escola.modulos.eventos.ObservadorHistorico;
import br.com.escola.modulos.eventos.ObservadorInfrequencia;
import br.com.escola.modulos.eventos.ObservadorResponsavel;
import br.com.escola.modulos.eventos.PublicadorEventos;
import br.com.escola.modulos.notas.Avaliacao;
import br.com.escola.modulos.notas.Nota;
import br.com.escola.modulos.notas.NotaDAO;
import br.com.escola.modulos.presenca.Aula;
import br.com.escola.modulos.presenca.Presenca;
import br.com.escola.modulos.presenca.PresencaDAO;
import br.com.escola.modulos.regras.BoletimDTO;
import br.com.escola.modulos.regras.BoletimService;
import br.com.escola.modulos.regras.MotorRegrasService;
import br.com.escola.modulos.regras.RegrasEscolaConfig;
import br.com.escola.modulos.usuarios.UsuarioFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Papel no padrao GoF Facade: Facade.
 * Ponto de entrada unico da secretaria escolar para a camada HTTP.
 * Esconde os subsistemas (AlunoDAO, TurmaDAO, NotaDAO, PresencaDAO,
 * BoletimService e MotorRegrasService), de modo que o HttpServerApp
 * depende apenas desta classe.
 * Tambem e quem dispara os eventos do padrao Observer: depois de salvar
 * uma nota ou presenca com sucesso, notifica o PublicadorEventos.
 */
public class SecretariaFacade {

    private final AlunoDAO alunoDAO;
    private final TurmaDAO turmaDAO;
    private final NotaDAO notaDAO;
    private final PresencaDAO presencaDAO;
    private final BoletimService boletimService;
    private final MotorRegrasService motorRegrasService;
    private final PublicadorEventos publicador;

    public SecretariaFacade() {
        this(new AlunoDAO(), new TurmaDAO(), new NotaDAO(), new PresencaDAO(),
                new BoletimService(), new MotorRegrasService());
    }

    /**
     * Construtor que recebe os subsistemas prontos e registra os observers padrao
     * (responsavel, historico e infrequencia) usando esses mesmos subsistemas.
     */
    public SecretariaFacade(AlunoDAO alunoDAO, TurmaDAO turmaDAO, NotaDAO notaDAO, PresencaDAO presencaDAO,
                            BoletimService boletimService, MotorRegrasService motorRegrasService) {
        this(alunoDAO, turmaDAO, notaDAO, presencaDAO, boletimService, motorRegrasService,
                criarPublicadorPadrao(presencaDAO, boletimService, motorRegrasService));
    }

    /**
     * Construtor completo. Permite usar dubles dos DAOs e um publicador proprio nos testes.
     */
    public SecretariaFacade(AlunoDAO alunoDAO, TurmaDAO turmaDAO, NotaDAO notaDAO, PresencaDAO presencaDAO,
                            BoletimService boletimService, MotorRegrasService motorRegrasService,
                            PublicadorEventos publicador) {
        this.alunoDAO = alunoDAO;
        this.turmaDAO = turmaDAO;
        this.notaDAO = notaDAO;
        this.presencaDAO = presencaDAO;
        this.boletimService = boletimService;
        this.motorRegrasService = motorRegrasService;
        this.publicador = publicador;
    }

    private static PublicadorEventos criarPublicadorPadrao(PresencaDAO presencaDAO, BoletimService boletimService,
                                                           MotorRegrasService motorRegrasService) {
        PublicadorEventos publicador = new PublicadorEventos();
        publicador.adicionarObservador(new ObservadorResponsavel());
        publicador.adicionarObservador(new ObservadorHistorico(boletimService));
        publicador.adicionarObservador(new ObservadorInfrequencia(presencaDAO, motorRegrasService));
        return publicador;
    }

    public PublicadorEventos getPublicador() {
        return publicador;
    }

    // --- Acesso ---

    /**
     * Login demonstrativo: identifica o perfil pelo e-mail e usa o Factory Method
     * (modulos.usuarios) para criar o usuario e montar os dados da sessao.
     *
     * @throws IllegalArgumentException se o perfil nao for reconhecido
     */
    public Map<String, Object> login(String email) {
        Map<String, String> dados = new HashMap<>();
        dados.put("email", email);
        String perfil = UsuarioFactory.identificarPerfil(email);
        return UsuarioFactory.obterFabrica(perfil).montarRespostaLogin(dados);
    }

    // --- Turmas e alunos ---

    public List<Turma> listarTurmas() {
        return turmaDAO.listarTodas();
    }

    public List<Matricula> listarAlunosDaTurma(UUID turmaId) {
        return alunoDAO.listarMatriculasPorTurma(turmaId);
    }

    // --- Notas ---

    public List<Avaliacao> listarAvaliacoes(UUID turmaDisciplinaId) {
        return notaDAO.listarAvaliacoes(turmaDisciplinaId);
    }

    public List<Nota> listarNotas(UUID turmaDisciplinaId) {
        return notaDAO.listarNotasPorTurmaDisciplina(turmaDisciplinaId);
    }

    public boolean lancarNota(UUID avaliacaoId, UUID matriculaId, double valor, String observacao) {
        boolean salvou = notaDAO.salvarNota(avaliacaoId, matriculaId, valor, observacao);
        if (salvou) {
            publicador.notificar(new EventoEscolar.NotaLancada(matriculaId, avaliacaoId, valor));
        }
        return salvou;
    }

    // --- Presencas ---

    public List<Aula> listarAulas(UUID turmaDisciplinaId) {
        return presencaDAO.listarAulas(turmaDisciplinaId);
    }

    public List<Presenca> listarPresencas(UUID aulaId) {
        return presencaDAO.listarPresencasPorAula(aulaId);
    }

    public boolean registrarPresenca(UUID aulaId, UUID matriculaId, boolean presente, String justificativa) {
        boolean salvou = presencaDAO.salvarPresenca(aulaId, matriculaId, presente, justificativa);
        if (salvou) {
            publicador.notificar(new EventoEscolar.PresencaRegistrada(matriculaId, aulaId, presente));
        }
        return salvou;
    }

    // --- Boletim e regras ---

    public List<BoletimDTO> gerarBoletim(UUID alunoId) {
        return boletimService.gerarBoletimPorAluno(alunoId);
    }

    public Map<String, Object> obterRegras() {
        RegrasEscolaConfig config = motorRegrasService.getConfig();
        Map<String, Object> regras = new LinkedHashMap<>();
        regras.put("ldb_frequencia_minima_percentual", config.getFrequenciaMinimaLdb());
        regras.put("ldb_artigo_referencia", "Art. 24, inciso VI da Lei 9.394/1996");
        regras.put("escola_media_aprovacao_direta", config.getMediaAprovacaoDireta());
        regras.put("escola_media_minima_recuperacao", config.getMediaMinimaRecuperacao());
        regras.put("escola_media_final_com_recuperacao", config.getMediaFinalComRecuperacao());
        regras.put("escola_quantidade_periodos", config.getQuantidadePeriodos());
        return regras;
    }
}
