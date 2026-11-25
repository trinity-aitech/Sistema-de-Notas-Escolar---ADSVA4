package br.com.escola.modulos.usuarios;

import br.com.escola.modulos.alunos.Aluno;
import br.com.escola.modulos.alunos.Professor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do padrao Factory Method (modulos.usuarios).
 */
class UsuarioFactoryTest {

    @Test
    void obterFabricaDevolveAFabricaCertaParaCadaPerfil() {
        assertInstanceOf(AlunoFactory.class, UsuarioFactory.obterFabrica("ALUNO"));
        assertInstanceOf(ProfessorFactory.class, UsuarioFactory.obterFabrica("PROFESSOR"));
        assertInstanceOf(ResponsavelFactory.class, UsuarioFactory.obterFabrica("RESPONSAVEL"));
        assertInstanceOf(AlunoFactory.class, UsuarioFactory.obterFabrica(" aluno "));
    }

    @Test
    void cadaFabricaCriaOTipoCertoDeUsuario() {
        Map<String, String> dados = Map.of("email", "teste@escola.com");

        Usuario aluno = UsuarioFactory.obterFabrica("ALUNO").criarUsuario(dados);
        Usuario professor = UsuarioFactory.obterFabrica("PROFESSOR").criarUsuario(dados);
        Usuario responsavel = UsuarioFactory.obterFabrica("RESPONSAVEL").criarUsuario(dados);

        assertInstanceOf(Aluno.class, aluno);
        assertInstanceOf(Professor.class, professor);
        assertInstanceOf(Responsavel.class, responsavel);

        assertEquals("ALUNO", aluno.perfil());
        assertEquals("PROFESSOR", professor.perfil());
        assertEquals("RESPONSAVEL", responsavel.perfil());
        assertEquals("teste@escola.com", aluno.email());
    }

    @Test
    void perfilInvalidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> UsuarioFactory.obterFabrica("DIRETOR"));
        assertThrows(IllegalArgumentException.class, () -> UsuarioFactory.obterFabrica(null));
    }

    @Test
    void identificarPerfilMantemARegraAntigaDoLogin() {
        assertEquals("PROFESSOR", UsuarioFactory.identificarPerfil("carlos.silva@escola.com"));
        assertEquals("PROFESSOR", UsuarioFactory.identificarPerfil("prof.ana@escola.com"));
        assertEquals("RESPONSAVEL", UsuarioFactory.identificarPerfil("maria.mendes@email.com"));
        assertEquals("ALUNO", UsuarioFactory.identificarPerfil("lucas.mendes@escola.com"));
    }

    @Test
    void respostaDoLoginTemOMesmoFormatoDeAntes() {
        Map<String, Object> prof = UsuarioFactory.obterFabrica("PROFESSOR")
                .montarRespostaLogin(Map.of("email", "carlos.silva@escola.com"));
        assertEquals(List.of("id", "nome", "email", "perfil"), List.copyOf(prof.keySet()));
        assertEquals("22222222-2222-2222-2222-222222222222", prof.get("id"));
        assertEquals("Prof. Carlos Silva", prof.get("nome"));

        Map<String, Object> resp = UsuarioFactory.obterFabrica("RESPONSAVEL")
                .montarRespostaLogin(Map.of("email", "maria.mendes@email.com"));
        assertEquals(List.of("id", "nome", "email", "perfil", "alunoVinculadoId"), List.copyOf(resp.keySet()));
        assertEquals("33333333-3333-3333-3333-333333333333", resp.get("id"));
        assertEquals("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01", resp.get("alunoVinculadoId"));

        Map<String, Object> aluno = UsuarioFactory.obterFabrica("ALUNO")
                .montarRespostaLogin(Map.of("email", "lucas.mendes@escola.com"));
        assertEquals(List.of("id", "nome", "email", "perfil", "alunoId"), List.copyOf(aluno.keySet()));
        assertEquals("44444444-4444-4444-4444-444444444444", aluno.get("id"));
        assertEquals("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01", aluno.get("alunoId"));
    }
}
