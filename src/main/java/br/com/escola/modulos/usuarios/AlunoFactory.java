package br.com.escola.modulos.usuarios;

import br.com.escola.modulos.alunos.Aluno;

import java.util.Map;

/**
 * Papel no padrao Factory Method: ConcreteCreator (Criador Concreto).
 * Cria objetos Aluno. Os valores padrao sao os do aluno de demonstracao
 * (Lucas Mendes), os mesmos que o login devolvia antes.
 */
public class AlunoFactory extends UsuarioFactory {

    static final String USUARIO_ID_PADRAO = "44444444-4444-4444-4444-444444444444";
    static final String ALUNO_ID_PADRAO = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01";

    @Override
    public Usuario criarUsuario(Map<String, String> dados) {
        return new Aluno(
                uuid(dados, "alunoId", ALUNO_ID_PADRAO),
                uuid(dados, "usuarioId", USUARIO_ID_PADRAO),
                texto(dados, "nome", "Lucas Mendes"),
                texto(dados, "email", "lucas.mendes@escola.com"),
                texto(dados, "matricula", "2026001"),
                texto(dados, "dataNascimento", "2011-05-14"),
                uuid(dados, "responsavelId", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                texto(dados, "nomeResponsavel", "Maria Mendes"),
                texto(dados, "telefoneResponsavel", "(11) 98888-7777")
        );
    }

    @Override
    protected void complementarRespostaLogin(Usuario usuario, Map<String, String> dados, Map<String, Object> resp) {
        resp.put("alunoId", usuario.id().toString());
    }
}
