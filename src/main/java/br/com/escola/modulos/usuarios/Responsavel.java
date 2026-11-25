package br.com.escola.modulos.usuarios;

import java.util.UUID;

/**
 * Papel no padrao Factory Method: ConcreteProduct (Produto Concreto).
 * Responsavel legal por um ou mais alunos. Criado pela ResponsavelFactory.
 */
public record Responsavel(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String telefone,
        String parentesco
) implements Usuario {

    public static final String PERFIL = "RESPONSAVEL";

    @Override
    public String perfil() {
        return PERFIL;
    }
}
