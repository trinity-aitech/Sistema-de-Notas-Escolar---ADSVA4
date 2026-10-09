package br.com.escola;

import br.com.escola.config.DatabaseConfig;
import br.com.escola.http.HttpServerApp;

import java.util.logging.Logger;

/**
 * Ponto de entrada principal do Sistema de Notas Escolar (Backend Java Puro).
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  SISTEMA DE NOTAS ESCOLAR - BACKEND EM JAVA PURO         ");
        System.out.println("  Compatível com PostgreSQL, Supabase e Vercel            ");
        System.out.println("==========================================================");

        int port = 8080;
        String portEnv = System.getenv("SERVER_PORT");
        if (portEnv == null || portEnv.isBlank()) {
            portEnv = System.getenv("PORT");
        }
        if (portEnv != null && !portEnv.isBlank()) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {}
        }

        LOGGER.info("Verificando conexão com o Banco de Dados...");
        boolean dbConectado = DatabaseConfig.testarConexao();
        if (dbConectado) {
            LOGGER.info("✅ Conexão com o Banco de Dados estabelecida com sucesso!");
        } else {
            LOGGER.warning("⚠️  Banco de Dados não acessível no momento. O servidor responderá usando modo de dados de demonstração em memória.");
        }

        try {
            HttpServerApp app = new HttpServerApp(port);
            app.start();

            System.out.println("\n🚀 Servidor web iniciado com sucesso!");
            System.out.println("👉 Acesse a aplicação no navegador: http://localhost:" + port);
            System.out.println("👉 Painel do Professor: http://localhost:" + port + "/professor.html");
            System.out.println("👉 Painel do Aluno: http://localhost:" + port + "/aluno.html");
            System.out.println("👉 Painel dos Pais/Responsáveis: http://localhost:" + port + "/responsavel.html");
            System.out.println("\nPressione Ctrl+C para encerrar o servidor.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nEncerrando servidor...");
                app.stop();
                System.out.println("Servidor encerrado.");
            }));
        } catch (Exception e) {
            LOGGER.severe("Falha crítica ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
