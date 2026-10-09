package br.com.escola.http;

import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * Utilitário leve de serialização e parsing de JSON em Java puro,
 * sem necessidade de bibliotecas externas pesadas.
 */
public class JsonUtils {

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String || obj instanceof UUID) {
            return "\"" + escapeJson(obj.toString()) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Enum<?>) {
            return "\"" + ((Enum<?>) obj).name() + "\"";
        }
        if (obj instanceof Collection<?> col) {
            StringJoiner sj = new StringJoiner(",", "[", "]");
            for (Object item : col) {
                sj.add(toJson(item));
            }
            return sj.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            StringJoiner sj = new StringJoiner(",", "{", "}");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sj.add("\"" + escapeJson(String.valueOf(entry.getKey())) + "\":" + toJson(entry.getValue()));
            }
            return sj.toString();
        }
        if (obj.getClass().isRecord()) {
            StringJoiner sj = new StringJoiner(",", "{", "}");
            for (RecordComponent rc : obj.getClass().getRecordComponents()) {
                try {
                    Object val = rc.getAccessor().invoke(obj);
                    sj.add("\"" + escapeJson(rc.getName()) + "\":" + toJson(val));
                } catch (Exception e) {
                    sj.add("\"" + escapeJson(rc.getName()) + "\":null");
                }
            }
            return sj.toString();
        }

        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Parser básico para extrair campos de um JSON simples de primeiro nível {"chave": "valor", ...}
     */
    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        if (json == null || json.isBlank()) return result;

        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("^\"|\"$", "");
                String value = kv[1].trim().replaceAll("^\"|\"$", "");
                result.put(key, value);
            }
        }
        return result;
    }
}
