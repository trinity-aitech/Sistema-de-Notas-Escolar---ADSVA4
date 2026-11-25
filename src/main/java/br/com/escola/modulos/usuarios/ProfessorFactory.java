package br.com.escola.modulos.usuarios;

import br.com.escola.modulos.alunos.Professor;
import java.util.Map;
import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteCreator.
 * Fabrica concreta responsavel por criar instancias de Professor.
 */
public class ProfessorFactory extends UsuarioFactory {

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        UUID id = dados.containsKey("id") && dados.get("id") != null
                ? UUID.fromString(dados.get("id"))
                : UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID usuarioId = dados.containsKey("usuarioId") && dados.get("usuarioId") != null
                ? UUID.fromString(dados.get("usuarioId"))
                : id;
        String nome = dados.getOrDefault("nome", "Prof. Carlos Silva");
        String email = dados.getOrDefault("email", "carlos.silva@escola.com");
        String matriculaFunc = dados.getOrDefault("matriculaFuncional", "DOC-2026-01");
        String formacao = dados.getOrDefault("formacao", "Licenciatura em Matematica");

        return new Professor(id, usuarioId, nome, email, matriculaFunc, formacao);
    }
}
