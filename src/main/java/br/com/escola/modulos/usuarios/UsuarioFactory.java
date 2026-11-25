package br.com.escola.modulos.usuarios;

import java.util.Map;

/**
 * Papel no padrao GoF Factory Method: Creator.
 * Define o contrato de criacao de usuarios e metodo estatico seletor de fabricas concretas.
 */
public abstract class UsuarioFactory {

    public abstract Usuario criarUsuario(Map<String, String> dados);

    public static UsuarioFactory obterFabrica(String perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil nao informado");
        }
        return switch (perfil.trim().toUpperCase()) {
            case "PROFESSOR" -> new ProfessorFactory();
            case "ALUNO" -> new AlunoFactory();
            case "RESPONSAVEL" -> new ResponsavelFactory();
            default -> throw new IllegalArgumentException("Perfil desconhecido: " + perfil);
        };
    }

    public static UsuarioFactory obterFabricaPorEmail(String email) {
        if (email == null) {
            return new AlunoFactory();
        }
        String lower = email.toLowerCase();
        if (lower.contains("carlos") || lower.contains("prof")) {
            return new ProfessorFactory();
        } else if (lower.contains("maria") || lower.contains("responsavel")) {
            return new ResponsavelFactory();
        } else {
            return new AlunoFactory();
        }
    }
}
