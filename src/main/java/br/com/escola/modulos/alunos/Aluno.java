package br.com.escola.modulos.alunos;

import br.com.escola.modulos.usuarios.Usuario;

import java.util.UUID;

/**
 * Papel no padrao Factory Method: ConcreteProduct (Produto Concreto).
 * Criado pela AlunoFactory. Os campos nao mudaram; o record apenas passou a
 * implementar Usuario.
 */
public record Aluno(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String matricula,
        String dataNascimento,
        UUID responsavelId,
        String nomeResponsavel,
        String telefoneResponsavel
) implements Usuario {

    public static final String PERFIL = "ALUNO";

    @Override
    public String perfil() {
        return PERFIL;
    }
}
