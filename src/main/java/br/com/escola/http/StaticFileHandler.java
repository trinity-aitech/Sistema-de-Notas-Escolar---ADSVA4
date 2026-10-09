package br.com.escola.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handler responsável por entregar os arquivos do frontend web (HTML, CSS, JS).
 * Procura primeiro na pasta local 'public/', e como fallback nos recursos embutidos.
 */
public class StaticFileHandler implements HttpHandler {

    private final Path publicDir;

    public StaticFileHandler() {
        this.publicDir = Paths.get("public").toAbsolutePath();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String pathStr = exchange.getRequestURI().getPath();
        if (pathStr.equals("/") || pathStr.isEmpty()) {
            pathStr = "/index.html";
        }

        // Sanitização contra path traversal
        pathStr = pathStr.replace("..", "");
        Path targetFile = publicDir.resolve(pathStr.startsWith("/") ? pathStr.substring(1) : pathStr).normalize();

        if (!targetFile.startsWith(publicDir) || !Files.exists(targetFile) || Files.isDirectory(targetFile)) {
            // Tenta servir index.html para SPAs ou retorna 404
            Path fallbackIndex = publicDir.resolve("index.html");
            if (Files.exists(fallbackIndex)) {
                targetFile = fallbackIndex;
            } else {
                String notFound = "<h1>404 - Arquivo não encontrado</h1>";
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes());
                }
                return;
            }
        }

        String mimeType = getMimeType(targetFile.toString());
        byte[] bytes = Files.readAllBytes(targetFile);

        exchange.getResponseHeaders().set("Content-Type", mimeType + "; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html")) return "text/html";
        if (lower.endsWith(".css")) return "text/css";
        if (lower.endsWith(".js") || lower.endsWith(".mjs")) return "application/javascript";
        if (lower.endsWith(".json")) return "application/json";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
