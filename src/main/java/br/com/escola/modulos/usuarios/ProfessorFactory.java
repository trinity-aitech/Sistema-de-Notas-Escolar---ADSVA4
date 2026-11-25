package br.com.escola.modulos.usuarios;

import br.com.escola.modulos.alunos.Professor;

import java.util.Map;

/**
 * Papel no padrao Factory Method: ConcreteCreator (Criador Concreto).
 * Cria objetos Professor. Os valores padrao sao os do professor de
 * demonstracao (Prof. Carlos Silva), os mesmos que o login devolvia antes.
 */
public class ProfessorFactory extends UsuarioFactory {

    static final String USUARIO_ID_PADRAO = "22222222-2222-2222-2222-222222222222";
    static final String PROFESSOR_ID_PADRAO = "cccccccc-cccc-cccc-cccc-cccccccccc01";

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        return new Professor(
                uuid(dados, "professorId", PROFESSOR_ID_PADRAO),
                uuid(dados, "usuarioId", USUARIO_ID_PADRAO),
                texto(dados, "nome", "Prof. Carlos Silva"),
                texto(dados, "email", "carlos.silva@escola.com"),
                texto(dados, "matriculaFuncional", "PROF001"),
                texto(dados, "formacao", "Licenciatura em Matematica")
        );
    }
}
