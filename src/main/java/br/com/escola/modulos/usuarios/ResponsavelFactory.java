package br.com.escola.modulos.usuarios;

import java.util.Map;
import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteCreator.
 * Fabrica concreta responsavel por criar instancias de Responsavel.
 */
public class ResponsavelFactory extends UsuarioFactory {

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        UUID id = dados.containsKey("id") && dados.get("id") != null
                ? UUID.fromString(dados.get("id"))
                : UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID usuarioId = dados.containsKey("usuarioId") && dados.get("usuarioId") != null
                ? UUID.fromString(dados.get("usuarioId"))
                : id;
        String nome = dados.getOrDefault("nome", "Maria Mendes");
        String email = dados.getOrDefault("email", "maria.mendes@email.com");
        String telefone = dados.getOrDefault("telefone", "(11) 98888-7777");
        String parentesco = dados.getOrDefault("parentesco", "Mae");
        UUID alunoVinculadoId = dados.containsKey("alunoVinculadoId") && dados.get("alunoVinculadoId") != null
                ? UUID.fromString(dados.get("alunoVinculadoId"))
                : UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");

        return new Responsavel(id, usuarioId, nome, email, telefone, parentesco, alunoVinculadoId);
    }
}
