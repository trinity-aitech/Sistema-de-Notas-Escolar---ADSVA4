package br.com.escola.http;

import br.com.escola.modulos.alunos.Aluno;
import br.com.escola.modulos.alunos.AlunoDAO;
import br.com.escola.modulos.alunos.Matricula;
import br.com.escola.modulos.alunos.Turma;
import br.com.escola.modulos.alunos.TurmaDAO;
import br.com.escola.modulos.notas.Avaliacao;
import br.com.escola.modulos.notas.Nota;
import br.com.escola.modulos.notas.NotaDAO;
import br.com.escola.modulos.presenca.Aula;
import br.com.escola.modulos.presenca.Presenca;
import br.com.escola.modulos.presenca.PresencaDAO;
import br.com.escola.modulos.regras.BoletimDTO;
import br.com.escola.modulos.regras.BoletimService;
import br.com.escola.modulos.regras.MotorRegrasService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Servidor HTTP embutido da JDK com suporte nativo a Virtual Threads e JSON REST API.
 */
public class HttpServerApp {

    private static final Logger LOGGER = Logger.getLogger(HttpServerApp.class.getName());

    private final int port;
    private HttpServer server;

    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final TurmaDAO turmaDAO = new TurmaDAO();
    private final NotaDAO notaDAO = new NotaDAO();
    private final PresencaDAO presencaDAO = new PresencaDAO();
    private final BoletimService boletimService = new BoletimService();
    private final MotorRegrasService motorRegrasService = new MotorRegrasService();

    public HttpServerApp(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Executor com Virtual Threads nativo do Java moderno
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // Registro das rotas da API REST
        server.createContext("/api/auth/login", this::handleLogin);
        server.createContext("/api/turmas", this::handleTurmas);
        server.createContext("/api/turmas/alunos", this::handleTurmaAlunos);
        server.createContext("/api/avaliacoes", this::handleAvaliacoes);
        server.createContext("/api/notas", this::handleNotas);
        server.createContext("/api/aulas", this::handleAulas);
        server.createContext("/api/presencas", this::handlePresencas);
        server.createContext("/api/boletim", this::handleBoletim);
        server.createContext("/api/regras", this::handleRegras);

        // Handler raiz para servir as páginas do frontend (HTML/CSS/JS)
        server.createContext("/", new StaticFileHandler());

        server.start();
        LOGGER.info("Servidor HTTP rodando na porta " + port + " com Virtual Threads.");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --- Handlers REST ---

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = lerCorpo(exchange);
            Map<String, String> data = JsonUtils.parseSimpleJson(body);
            String email = data.getOrDefault("email", "carlos.silva@escola.com");

            // Mapeamento simples de usuário por email para o login demonstrativo
            Map<String, Object> resp = new LinkedHashMap<>();
            if (email.contains("carlos") || email.contains("prof")) {
                resp.put("id", "22222222-2222-2222-2222-222222222222");
                resp.put("nome", "Prof. Carlos Silva");
                resp.put("email", email);
                resp.put("perfil", "PROFESSOR");
            } else if (email.contains("maria") || email.contains("responsavel")) {
                resp.put("id", "33333333-3333-3333-3333-333333333333");
                resp.put("nome", "Maria Mendes");
                resp.put("email", email);
                resp.put("perfil", "RESPONSAVEL");
                resp.put("alunoVinculadoId", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");
            } else {
                resp.put("id", "44444444-4444-4444-4444-444444444444");
                resp.put("nome", "Lucas Mendes");
                resp.put("email", email);
                resp.put("perfil", "ALUNO");
                resp.put("alunoId", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");
            }
            responderJson(exchange, 200, resp);
        } else {
            responderJson(exchange, 405, Map.of("erro", "Método não permitido"));
        }
    }

    private void handleTurmas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        List<Turma> turmas = turmaDAO.listarTodas();
        responderJson(exchange, 200, turmas);
    }

    private void handleTurmaAlunos(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String turmaIdStr = params.getOrDefault("turmaId", "dddddddd-dddd-dddd-dddd-dddddddddddd");
        UUID turmaId = UUID.fromString(turmaIdStr);

        List<Matricula> matriculas = alunoDAO.listarMatriculasPorTurma(turmaId);
        responderJson(exchange, 200, matriculas);
    }

    private void handleAvaliacoes(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID tdId = UUID.fromString(tdIdStr);

        List<Avaliacao> avaliacoes = notaDAO.listarAvaliacoes(tdId);
        responderJson(exchange, 200, avaliacoes);
    }

    private void handleNotas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
            String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
            UUID tdId = UUID.fromString(tdIdStr);

            List<Nota> notas = notaDAO.listarNotasPorTurmaDisciplina(tdId);
            responderJson(exchange, 200, notas);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = lerCorpo(exchange);
            Map<String, String> data = JsonUtils.parseSimpleJson(body);

            UUID avaliacaoId = UUID.fromString(data.get("avaliacaoId"));
            UUID matriculaId = UUID.fromString(data.get("matriculaId"));
            double valor = Double.parseDouble(data.get("valorNota"));
            String obs = data.getOrDefault("observacao", "");

            boolean ok = notaDAO.salvarNota(avaliacaoId, matriculaId, valor, obs);
            responderJson(exchange, 200, Map.of("sucesso", ok, "mensagem", "Nota lançada com sucesso"));
        }
    }

    private void handleAulas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID tdId = UUID.fromString(tdIdStr);

        List<Aula> aulas = presencaDAO.listarAulas(tdId);
        responderJson(exchange, 200, aulas);
    }

    private void handlePresencas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
            String aulaIdStr = params.get("aulaId");
            if (aulaIdStr != null) {
                UUID aulaId = UUID.fromString(aulaIdStr);
                List<Presenca> presencas = presencaDAO.listarPresencasPorAula(aulaId);
                responderJson(exchange, 200, presencas);
            } else {
                responderJson(exchange, 400, Map.of("erro", "Parâmetro aulaId é obrigatório"));
            }
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = lerCorpo(exchange);
            Map<String, String> data = JsonUtils.parseSimpleJson(body);

            UUID aulaId = UUID.fromString(data.get("aulaId"));
            UUID matriculaId = UUID.fromString(data.get("matriculaId"));
            boolean presente = Boolean.parseBoolean(data.getOrDefault("presente", "true"));
            String justificativa = data.get("justificativa");

            boolean ok = presencaDAO.salvarPresenca(aulaId, matriculaId, presente, justificativa);
            responderJson(exchange, 200, Map.of("sucesso", ok, "mensagem", "Presença registrada"));
        }
    }

    private void handleBoletim(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String alunoIdStr = params.getOrDefault("alunoId", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");
        UUID alunoId = UUID.fromString(alunoIdStr);

        List<BoletimDTO> boletim = boletimService.gerarBoletimPorAluno(alunoId);
        responderJson(exchange, 200, boletim);
    }

    private void handleRegras(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        Map<String, Object> regras = new LinkedHashMap<>();
        regras.put("ldb_frequencia_minima_percentual", motorRegrasService.getConfig().getFrequenciaMinimaLdb());
        regras.put("ldb_artigo_referencia", "Art. 24, inciso VI da Lei 9.394/1996");
        regras.put("escola_media_aprovacao_direta", motorRegrasService.getConfig().getMediaAprovacaoDireta());
        regras.put("escola_media_minima_recuperacao", motorRegrasService.getConfig().getMediaMinimaRecuperacao());
        regras.put("escola_media_final_com_recuperacao", motorRegrasService.getConfig().getMediaFinalComRecuperacao());
        regras.put("escola_quantidade_periodos", motorRegrasService.getConfig().getQuantidadePeriodos());

        responderJson(exchange, 200, regras);
    }

    // --- Utilitários HTTP & CORS ---

    private boolean aplicarCorsEOpcoes(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }

    private void responderJson(HttpExchange exchange, int status, Object data) throws IOException {
        String json = JsonUtils.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> extrairQueryParams(String query) {
        Map<String, String> map = new LinkedHashMap<>();
        if (query == null || query.isBlank()) return map;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
