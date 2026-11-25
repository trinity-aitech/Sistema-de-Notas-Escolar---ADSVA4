package br.com.escola.modulos.usuarios;

import java.util.Map;

/**
 * Papel no padrao Factory Method: ConcreteCreator (Criador Concreto).
 * Cria objetos Responsavel. Os valores padrao sao os da responsavel de
 * demonstracao (Maria Mendes), os mesmos que o login devolvia antes.
 */
public class ResponsavelFactory extends UsuarioFactory {

    static final String USUARIO_ID_PADRAO = "33333333-3333-3333-3333-333333333333";
    static final String RESPONSAVEL_ID_PADRAO = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    static final String ALUNO_VINCULADO_PADRAO = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01";

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        return new Responsavel(
                uuid(dados, "responsavelId", RESPONSAVEL_ID_PADRAO),
                uuid(dados, "usuarioId", USUARIO_ID_PADRAO),
                texto(dados, "nome", "Maria Mendes"),
                texto(dados, "email", "maria.mendes@email.com"),
                texto(dados, "telefone", "(11) 98888-7777"),
                texto(dados, "parentesco", "MAE")
        );
    }

    @Override
    protected void complementarRespostaLogin(Usuario usuario, Map<String, String> dados, Map<String, Object> resp) {
        resp.put("alunoVinculadoId", texto(dados, "alunoVinculadoId", ALUNO_VINCULADO_PADRAO));
    }
}
