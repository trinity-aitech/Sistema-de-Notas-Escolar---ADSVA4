package br.com.escola.modulos.usuarios;

import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteProduct.
 * Representa o responsavel legal por um ou mais estudantes.
 */
public record Responsavel(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String telefone,
        String parentesco,
        UUID alunoVinculadoId
) implements Usuario {

    public Responsavel(UUID id, UUID usuarioId, String nome, String email, String telefone, String parentesco) {
        this(id, usuarioId, nome, email, telefone, parentesco, null);
    }

    @Override
    public String perfil() {
        return "RESPONSAVEL";
    }
}
