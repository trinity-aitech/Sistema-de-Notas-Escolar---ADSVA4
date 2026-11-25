package br.com.escola.modulos.alunos;

import br.com.escola.modulos.usuarios.Usuario;
import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: ConcreteProduct.
 * Representa um docente responsavel pelas disciplinas escolares.
 */
public record Professor(
        UUID id,
        UUID usuarioId,
        String nome,
        String email,
        String matriculaFuncional,
        String formacao
) implements Usuario {
    @Override
    public String perfil() {
        return "PROFESSOR";
    }
}
