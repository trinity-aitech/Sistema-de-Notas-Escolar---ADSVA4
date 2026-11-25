package br.com.escola.http;

import br.com.escola.modulos.alunos.Matricula;
import br.com.escola.modulos.alunos.Turma;
import br.com.escola.modulos.notas.Avaliacao;
import br.com.escola.modulos.notas.Nota;
import br.com.escola.modulos.presenca.Aula;
import br.com.escola.modulos.presenca.Presenca;
import br.com.escola.modulos.regras.BoletimDTO;
import br.com.escola.modulos.secretaria.SecretariaFacade;
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
 * Papel no padrao GoF Facade: Client. Depende apenas da SecretariaFacade; os handlers
 * so leem a requisicao, chamam a fachada e respondem JSON.
 */
public class HttpServerApp {

    private static final Logger LOGGER = Logger.getLogger(HttpServerApp.class.getName());

    private final int port;
    private HttpServer server;

    private final SecretariaFacade secretaria;

    public HttpServerApp(int port) {
        this(port, new SecretariaFacade());
    }

    public HttpServerApp(int port, SecretariaFacade secretaria) {
        this.port = port;
        this.secretaria = secretaria;
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

            try {
                responderJson(exchange, 200, secretaria.login(email));
            } catch (IllegalArgumentException e) {
                responderJson(exchange, 400, Map.of("erro", e.getMessage()));
            }
        } else {
            responderJson(exchange, 405, Map.of("erro", "Método não permitido"));
        }
    }

    private void handleTurmas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        List<Turma> turmas = secretaria.listarTurmas();
        responderJson(exchange, 200, turmas);
    }

    private void handleTurmaAlunos(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String turmaIdStr = params.getOrDefault("turmaId", "dddddddd-dddd-dddd-dddd-dddddddddddd");
        UUID turmaId = UUID.fromString(turmaIdStr);

        List<Matricula> matriculas = secretaria.listarAlunosDaTurma(turmaId);
        responderJson(exchange, 200, matriculas);
    }

    private void handleAvaliacoes(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID tdId = UUID.fromString(tdIdStr);

        List<Avaliacao> avaliacoes = secretaria.listarAvaliacoes(tdId);
        responderJson(exchange, 200, avaliacoes);
    }

    private void handleNotas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
            String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
            UUID tdId = UUID.fromString(tdIdStr);

            List<Nota> notas = secretaria.listarNotas(tdId);
            responderJson(exchange, 200, notas);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = lerCorpo(exchange);
            Map<String, String> data = JsonUtils.parseSimpleJson(body);

            UUID avaliacaoId = UUID.fromString(data.get("avaliacaoId"));
            UUID matriculaId = UUID.fromString(data.get("matriculaId"));
            double valor = Double.parseDouble(data.get("valorNota"));
            String obs = data.getOrDefault("observacao", "");

            boolean ok = secretaria.lancarNota(avaliacaoId, matriculaId, valor, obs);
            responderJson(exchange, 200, Map.of("sucesso", ok, "mensagem", "Nota lançada com sucesso"));
        }
    }

    private void handleAulas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;
        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String tdIdStr = params.getOrDefault("turmaDisciplinaId", "ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID tdId = UUID.fromString(tdIdStr);

        List<Aula> aulas = secretaria.listarAulas(tdId);
        responderJson(exchange, 200, aulas);
    }

    private void handlePresencas(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
            String aulaIdStr = params.get("aulaId");
            if (aulaIdStr != null) {
                UUID aulaId = UUID.fromString(aulaIdStr);
                List<Presenca> presencas = secretaria.listarPresencas(aulaId);
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

            boolean ok = secretaria.registrarPresenca(aulaId, matriculaId, presente, justificativa);
            responderJson(exchange, 200, Map.of("sucesso", ok, "mensagem", "Presença registrada"));
        }
    }

    private void handleBoletim(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        Map<String, String> params = extrairQueryParams(exchange.getRequestURI().getQuery());
        String alunoIdStr = params.getOrDefault("alunoId", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");
        UUID alunoId = UUID.fromString(alunoIdStr);

        List<BoletimDTO> boletim = secretaria.gerarBoletim(alunoId);
        responderJson(exchange, 200, boletim);
    }

    private void handleRegras(HttpExchange exchange) throws IOException {
        if (aplicarCorsEOpcoes(exchange)) return;

        Map<String, Object> regras = secretaria.obterRegras();
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
