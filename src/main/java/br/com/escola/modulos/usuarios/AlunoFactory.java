package br.com.escola.modulos.usuarios;

import br.com.escola.modulos.alunos.Aluno;
import java.util.Map;
import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteCreator.
 * Fabrica concreta responsavel por criar instancias de Aluno.
 */
public class AlunoFactory extends UsuarioFactory {

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        UUID id = dados.containsKey("id") && dados.get("id") != null
                ? UUID.fromString(dados.get("id"))
                : UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID usuarioId = dados.containsKey("usuarioId") && dados.get("usuarioId") != null
                ? UUID.fromString(dados.get("usuarioId"))
                : id;
        String nome = dados.getOrDefault("nome", "Lucas Mendes");
        String email = dados.getOrDefault("email", "lucas.mendes@escola.com");
        String matricula = dados.getOrDefault("matricula", "2026-001");
        String dataNasc = dados.getOrDefault("dataNascimento", "2008-05-14");
        UUID respId = dados.containsKey("responsavelId") && dados.get("responsavelId") != null
                ? UUID.fromString(dados.get("responsavelId"))
                : UUID.fromString("33333333-3333-3333-3333-333333333333");
        String nomeResp = dados.getOrDefault("nomeResponsavel", "Maria Mendes");
        String telResp = dados.getOrDefault("telefoneResponsavel", "(11) 98888-7777");

        return new Aluno(id, usuarioId, nome, email, matricula, dataNasc, respId, nomeResp, telResp);
    }
}
