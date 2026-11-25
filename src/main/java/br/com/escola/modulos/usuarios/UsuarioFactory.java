package br.com.escola.modulos.usuarios;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Papel no padrao Factory Method: Creator (Criador).
 *
 * Declara o factory method criarUsuario, que cada subclasse implementa para
 * devolver o seu tipo concreto de Usuario. Tambem possui a operacao
 * montarRespostaLogin, que usa o factory method sem saber qual classe concreta
 * esta sendo criada.
 *
 * O metodo estatico obterFabrica escolhe a fabrica concreta pelo perfil, e assim
 * o HttpServerApp nao precisa mais de um if/else para cada tipo de usuario.
 */
public abstract class UsuarioFactory {

    /**
     * Factory method: cada fabrica concreta decide qual Usuario instanciar.
     *
     * @param dados campos vindos da requisicao (email, nome, ids...). Campos
     *              ausentes recebem os valores de demonstracao da fabrica.
     */
    public abstract Usuario criarUsuario(Map<String, String> dados);

    /**
     * Monta o JSON de resposta do login. As chaves comuns sao preenchidas aqui
     * e cada fabrica concreta pode acrescentar as suas pelo gancho
     * complementarRespostaLogin.
     */
    public Map<String, Object> montarRespostaLogin(Map<String, String> dados) {
        Usuario usuario = criarUsuario(dados);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", usuario.usuarioId().toString());
        resp.put("nome", usuario.nome());
        resp.put("email", usuario.email());
        resp.put("perfil", usuario.perfil());
        complementarRespostaLogin(usuario, dados, resp);
        return resp;
    }

    /** Gancho opcional para campos extras da resposta do login. */
    protected void complementarRespostaLogin(Usuario usuario, Map<String, String> dados, Map<String, Object> resp) {
        // Por padrao nao acrescenta nada.
    }

    /**
     * Devolve a fabrica concreta correspondente ao perfil informado.
     *
     * @throws IllegalArgumentException se o perfil nao for reconhecido
     */
    public static UsuarioFactory obterFabrica(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            throw new IllegalArgumentException("Perfil nao informado");
        }
        return switch (perfil.trim().toUpperCase(Locale.ROOT)) {
            case "ALUNO" -> new AlunoFactory();
            case "PROFESSOR" -> new ProfessorFactory();
            case "RESPONSAVEL" -> new ResponsavelFactory();
            default -> throw new IllegalArgumentException("Perfil desconhecido: " + perfil);
        };
    }

    /**
     * Descobre o perfil a partir do e-mail, mantendo a mesma regra do login de
     * demonstracao que existia antes no HttpServerApp.
     */
    public static String identificarPerfil(String email) {
        String e = email == null ? "" : email.toLowerCase(Locale.ROOT);
        if (e.contains("carlos") || e.contains("prof")) {
            return "PROFESSOR";
        }
        if (e.contains("maria") || e.contains("responsavel")) {
            return "RESPONSAVEL";
        }
        return "ALUNO";
    }

    // --- Auxiliares para as fabricas concretas ---

    protected static String texto(Map<String, String> dados, String chave, String padrao) {
        String valor = dados == null ? null : dados.get(chave);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }

    protected static UUID uuid(Map<String, String> dados, String chave, String padrao) {
        return UUID.fromString(texto(dados, chave, padrao));
    }
}
