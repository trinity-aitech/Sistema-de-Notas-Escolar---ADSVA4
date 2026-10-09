package br.com.escola.modulos.alunos;

import br.com.escola.config.DatabaseConfig;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class AlunoDAO {
    private static final Logger LOGGER = Logger.getLogger(AlunoDAO.class.getName());

    // Mock data de fallback se o banco de dados ainda não estiver conectado
    private static final List<Aluno> MOCK_ALUNOS = new ArrayList<>();
    private static final List<Matricula> MOCK_MATRICULAS = new ArrayList<>();

    static {
        UUID turmaId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        UUID a1 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01");
        UUID a2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02");
        UUID a3 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03");

        MOCK_ALUNOS.add(new Aluno(a1, UUID.fromString("44444444-4444-4444-4444-444444444444"), "Lucas Mendes", "lucas.mendes@escola.com", "2026001", "2011-05-14", UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "Maria Mendes", "(11) 98888-7777"));
        MOCK_ALUNOS.add(new Aluno(a2, UUID.fromString("55555555-5555-5555-5555-555555555555"), "Beatriz Souza", "beatriz.souza@escola.com", "2026002", "2011-08-22", null, null, null));
        MOCK_ALUNOS.add(new Aluno(a3, UUID.fromString("66666666-6666-6666-6666-666666666666"), "Gabriel Costa", "gabriel.costa@escola.com", "2026003", "2011-02-10", null, null, null));

        MOCK_MATRICULAS.add(new Matricula(UUID.fromString("10101010-1010-1010-1010-101010101001"), a1, "Lucas Mendes", "2026001", turmaId, 1, "ATIVA"));
        MOCK_MATRICULAS.add(new Matricula(UUID.fromString("10101010-1010-1010-1010-101010101002"), a2, "Beatriz Souza", "2026002", turmaId, 2, "ATIVA"));
        MOCK_MATRICULAS.add(new Matricula(UUID.fromString("10101010-1010-1010-1010-101010101003"), a3, "Gabriel Costa", "2026003", turmaId, 3, "ATIVA"));
    }

    public List<Aluno> listarTodos() {
        List<Aluno> lista = new ArrayList<>();
        String sql = """
            SELECT a.id, a.usuario_id, u.nome, u.email, a.matricula, a.data_nascimento,
                   a.responsavel_id, ur.nome AS nome_responsavel, r.telefone AS telefone_responsavel
            FROM alunos a
            JOIN usuarios u ON u.id = a.usuario_id
            LEFT JOIN responsaveis r ON r.id = a.responsavel_id
            LEFT JOIN usuarios ur ON ur.id = r.usuario_id
            ORDER BY u.nome
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Aluno(
                        (UUID) rs.getObject("id"),
                        (UUID) rs.getObject("usuario_id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("matricula"),
                        rs.getString("data_nascimento"),
                        (UUID) rs.getObject("responsavel_id"),
                        rs.getString("nome_responsavel"),
                        rs.getString("telefone_responsavel")
                ));
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para AlunoDAO.listarTodos: " + e.getMessage());
            return MOCK_ALUNOS;
        }
    }

    public List<Matricula> listarMatriculasPorTurma(UUID turmaId) {
        List<Matricula> lista = new ArrayList<>();
        String sql = """
            SELECT m.id, m.aluno_id, u.nome AS aluno_nome, a.matricula AS matricula_numero,
                   m.turma_id, m.numero_chamada, m.status
            FROM matriculas m
            JOIN alunos a ON a.id = m.aluno_id
            JOIN usuarios u ON u.id = a.usuario_id
            WHERE m.turma_id = ?
            ORDER BY m.numero_chamada, u.nome
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Matricula(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("aluno_id"),
                            rs.getString("aluno_nome"),
                            rs.getString("matricula_numero"),
                            (UUID) rs.getObject("turma_id"),
                            rs.getInt("numero_chamada"),
                            rs.getString("status")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarMatriculasPorTurma: " + e.getMessage());
            return MOCK_MATRICULAS;
        }
    }

    public Optional<Aluno> buscarPorUsuarioId(UUID usuarioId) {
        String sql = """
            SELECT a.id, a.usuario_id, u.nome, u.email, a.matricula, a.data_nascimento,
                   a.responsavel_id, ur.nome AS nome_responsavel, r.telefone AS telefone_responsavel
            FROM alunos a
            JOIN usuarios u ON u.id = a.usuario_id
            LEFT JOIN responsaveis r ON r.id = a.responsavel_id
            LEFT JOIN usuarios ur ON ur.id = r.usuario_id
            WHERE a.usuario_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Aluno(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("usuario_id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("matricula"),
                            rs.getString("data_nascimento"),
                            (UUID) rs.getObject("responsavel_id"),
                            rs.getString("nome_responsavel"),
                            rs.getString("telefone_responsavel")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para buscarPorUsuarioId: " + e.getMessage());
        }

        return MOCK_ALUNOS.stream()
                .filter(a -> a.usuarioId().equals(usuarioId))
                .findFirst();
    }
}
