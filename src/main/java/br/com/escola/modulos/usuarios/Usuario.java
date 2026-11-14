package br.com.escola.modulos.usuarios;

import java.util.UUID;

/**
 * Papel no padrao GoF Factory Method: Product.
 * Interface comum para todos os usuarios do sistema educacional.
 */
public interface Usuario {
    UUID id();
    String nome();
    String email();
    String perfil();
}
