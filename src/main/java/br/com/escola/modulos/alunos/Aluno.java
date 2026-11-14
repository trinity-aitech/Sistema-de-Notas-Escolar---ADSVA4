package br.com.escola.modulos.alunos;

import br.com.escola.modulos.usuarios.Usuario;
import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteProduct.
 * Representa um estudante matriculado na instituicao de ensino.
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
    @Override
    public String perfil() {
        return "ALUNO";
    }
}
