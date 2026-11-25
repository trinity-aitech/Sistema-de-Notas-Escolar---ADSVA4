package br.com.escola.modulos.usuarios;

import java.util.UUID;

/**
 * Papel no padrao Factory Method: Product (Produto).
 * Contrato comum a todos os usuarios do sistema (Aluno, Professor e Responsavel).
 * As fabricas concretas devolvem sempre um Usuario, entao quem chama nao
 * precisa conhecer a classe concreta que foi criada.
 *
 * Os metodos id, usuarioId, nome e email batem com os componentes dos records,
 * por isso os records ja os implementam automaticamente.
 */
public interface Usuario {

    /** Identificador da entidade especifica (aluno, professor ou responsavel). */
    UUID id();

    /** Identificador na tabela de usuarios, usado como "id" na resposta do login. */
    UUID usuarioId();

    String nome();

    String email();

    /** Perfil de acesso: ALUNO, PROFESSOR ou RESPONSAVEL. */
    String perfil();
}
