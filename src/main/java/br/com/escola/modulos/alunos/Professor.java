package br.com.escola.modulos.alunos;

import br.com.escola.modulos.usuarios.Usuario;

import java.util.UUID;

/**
 * Papel no padrao Factory Method: ConcreteProduct (Produto Concreto).
 * Criado pela ProfessorFactory. Os campos nao mudaram; o record apenas passou a
 * implementar Usuario.
 */
public record Professor(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String matriculaFuncional,
        String formacao
) implements Usuario {

    public static final String PERFIL = "PROFESSOR";

    @Override
    public String perfil() {
        return PERFIL;
    }
}
