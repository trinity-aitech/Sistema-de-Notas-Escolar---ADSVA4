package br.com.escola.config;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Configuração e gerenciador de conexões JDBC com PostgreSQL / Supabase.
 * Suporta tanto URLs padrão JDBC quanto URLs do Supabase (DATABASE_URL / SUPABASE_DB_URL).
 */
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());

    private static String jdbcUrl;
    private static String user;
    private static String password;
    private static boolean driverCarregado = false;

    static {
        carregarDriver();
        configurarParametros();
    }

    private static void carregarDriver() {
        try {
            Class.forName("org.postgresql.Driver");
            driverCarregado = true;
            LOGGER.info("Driver JDBC PostgreSQL carregado com sucesso.");
        } catch (ClassNotFoundException e) {
            LOGGER.warning("Driver org.postgresql.Driver não encontrado no classpath. Conexões diretas ao DB falharão se o driver não estiver presente.");
        }
    }

    private static void configurarParametros() {
        // Verifica se há DATABASE_URL no formato padrão de PaaS / Supabase
        // Ex: postgres://postgres:senha@db.ref.supabase.co:5432/postgres
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = System.getProperty("DATABASE_URL");
        }
        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = System.getenv("SUPABASE_DB_URL");
        }
        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = System.getProperty("SUPABASE_DB_URL");
        }

        if (databaseUrl != null && !databaseUrl.isBlank() && databaseUrl.startsWith("postgres")) {
            try {
                // Converte URI no formato postgresql://user:pass@host:port/db para JDBC
                URI uri = new URI(databaseUrl.replace("jdbc:", ""));
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // /postgres
                String userInfo = uri.getUserInfo();

                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    password = parts[1];
                }

                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + "?sslmode=require";
                LOGGER.info("Configurado a partir de DATABASE_URL do Supabase: " + host + ":" + port);
                return;
            } catch (Exception e) {
                LOGGER.warning("Falha ao analisar DATABASE_URL: " + e.getMessage() + ". Utilizando parâmetros DB_URL padrão.");
            }
        }

        // Variáveis padrão ou valores locais
        jdbcUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/escola_db");
        user = System.getenv().getOrDefault("DB_USER", "postgres");
        password = System.getenv().getOrDefault("DB_PASSWORD", "postgrespassword");
    }

    public static Connection getConnection() throws SQLException {
        if (!driverCarregado) {
            carregarDriver();
        }
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            LOGGER.warning("Aviso: Banco de dados não acessível no momento (" + e.getMessage() + "). O sistema iniciará com dados mock para demonstração.");
            return false;
        }
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getUser() {
        return user;
    }
}
