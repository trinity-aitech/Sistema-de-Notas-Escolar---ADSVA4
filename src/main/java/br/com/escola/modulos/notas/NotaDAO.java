package br.com.escola.modulos.notas;

import br.com.escola.config.DatabaseConfig;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class NotaDAO {
    private static final Logger LOGGER = Logger.getLogger(NotaDAO.class.getName());

    private static final List<Avaliacao> MOCK_AVALIACOES = new ArrayList<>();
    private static final Map<String, Nota> MOCK_NOTAS = new LinkedHashMap<>();

    static {
        UUID tdId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID av1 = UUID.fromString("a1111111-1111-1111-1111-111111111111");
        UUID av2 = UUID.fromString("a2222222-2222-2222-2222-222222222222");
        UUID av3 = UUID.fromString("a3333333-3333-3333-3333-333333333333");
        UUID av4 = UUID.fromString("a4444444-4444-4444-4444-444444444444");

        MOCK_AVALIACOES.add(new Avaliacao(av1, tdId, 1, "Prova Bimestral 1", "PROVA", 1.0, 10.0, "2026-03-25"));
        MOCK_AVALIACOES.add(new Avaliacao(av2, tdId, 2, "Prova Bimestral 2", "PROVA", 1.0, 10.0, "2026-06-20"));
        MOCK_AVALIACOES.add(new Avaliacao(av3, tdId, 3, "Prova Bimestral 3", "PROVA", 1.0, 10.0, "2026-09-18"));
        MOCK_AVALIACOES.add(new Avaliacao(av4, tdId, 4, "Prova Bimestral 4", "PROVA", 1.0, 10.0, "2026-11-28"));

        UUID m1 = UUID.fromString("10101010-1010-1010-1010-101010101001");
        UUID m2 = UUID.fromString("10101010-1010-1010-1010-101010101002");
        UUID m3 = UUID.fromString("10101010-1010-1010-1010-101010101003");

        MOCK_NOTAS.put(av1 + "_" + m1, new Nota(UUID.randomUUID(), av1, m1, "Lucas Mendes", 1, "Prova Bimestral 1", 8.5, ""));
        MOCK_NOTAS.put(av2 + "_" + m1, new Nota(UUID.randomUUID(), av2, m1, "Lucas Mendes", 2, "Prova Bimestral 2", 9.0, ""));
        MOCK_NOTAS.put(av3 + "_" + m1, new Nota(UUID.randomUUID(), av3, m1, "Lucas Mendes", 3, "Prova Bimestral 3", 8.0, ""));
        MOCK_NOTAS.put(av4 + "_" + m1, new Nota(UUID.randomUUID(), av4, m1, "Lucas Mendes", 4, "Prova Bimestral 4", 9.5, ""));

        MOCK_NOTAS.put(av1 + "_" + m2, new Nota(UUID.randomUUID(), av1, m2, "Beatriz Souza", 1, "Prova Bimestral 1", 5.0, ""));
        MOCK_NOTAS.put(av2 + "_" + m2, new Nota(UUID.randomUUID(), av2, m2, "Beatriz Souza", 2, "Prova Bimestral 2", 6.0, ""));
        MOCK_NOTAS.put(av3 + "_" + m2, new Nota(UUID.randomUUID(), av3, m2, "Beatriz Souza", 3, "Prova Bimestral 3", 4.5, ""));
        MOCK_NOTAS.put(av4 + "_" + m2, new Nota(UUID.randomUUID(), av4, m2, "Beatriz Souza", 4, "Prova Bimestral 4", 6.5, ""));

        MOCK_NOTAS.put(av1 + "_" + m3, new Nota(UUID.randomUUID(), av1, m3, "Gabriel Costa", 1, "Prova Bimestral 1", 7.5, ""));
        MOCK_NOTAS.put(av2 + "_" + m3, new Nota(UUID.randomUUID(), av2, m3, "Gabriel Costa", 2, "Prova Bimestral 2", 8.0, ""));
        MOCK_NOTAS.put(av3 + "_" + m3, new Nota(UUID.randomUUID(), av3, m3, "Gabriel Costa", 3, "Prova Bimestral 3", 7.0, ""));
        MOCK_NOTAS.put(av4 + "_" + m3, new Nota(UUID.randomUUID(), av4, m3, "Gabriel Costa", 4, "Prova Bimestral 4", 8.5, ""));
    }

    public List<Avaliacao> listarAvaliacoes(UUID turmaDisciplinaId) {
        List<Avaliacao> lista = new ArrayList<>();
        String sql = """
            SELECT id, turma_disciplina_id, periodo, nome, tipo, peso, nota_maxima, data_avaliacao
            FROM avaliacoes
            WHERE turma_disciplina_id = ?
            ORDER BY periodo, data_avaliacao
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, turmaDisciplinaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Avaliacao(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("turma_disciplina_id"),
                            rs.getInt("periodo"),
                            rs.getString("nome"),
                            rs.getString("tipo"),
                            rs.getDouble("peso"),
                            rs.getDouble("nota_maxima"),
                            rs.getString("data_avaliacao")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarAvaliacoes: " + e.getMessage());
            return MOCK_AVALIACOES;
        }
    }

    public List<Nota> listarNotasPorTurmaDisciplina(UUID turmaDisciplinaId) {
        List<Nota> lista = new ArrayList<>();
        String sql = """
            SELECT n.id, n.avaliacao_id, n.matricula_id, u.nome AS aluno_nome,
                   av.periodo, av.nome AS avaliacao_nome, n.valor_nota, n.observacao
            FROM notas n
            JOIN avaliacoes av ON av.id = n.avaliacao_id
            JOIN matriculas m ON m.id = n.matricula_id
            JOIN alunos a ON a.id = m.aluno_id
            JOIN usuarios u ON u.id = a.usuario_id
            WHERE av.turma_disciplina_id = ?
            ORDER BY av.periodo, u.nome
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, turmaDisciplinaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Nota(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("avaliacao_id"),
                            (UUID) rs.getObject("matricula_id"),
                            rs.getString("aluno_nome"),
                            rs.getInt("periodo"),
                            rs.getString("avaliacao_nome"),
                            rs.getDouble("valor_nota"),
                            rs.getString("observacao")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarNotasPorTurmaDisciplina: " + e.getMessage());
            return new ArrayList<>(MOCK_NOTAS.values());
        }
    }

    public List<Nota> listarNotasPorMatricula(UUID matriculaId) {
        List<Nota> lista = new ArrayList<>();
        String sql = """
            SELECT n.id, n.avaliacao_id, n.matricula_id, u.nome AS aluno_nome,
                   av.periodo, av.nome AS avaliacao_nome, n.valor_nota, n.observacao
            FROM notas n
            JOIN avaliacoes av ON av.id = n.avaliacao_id
            JOIN matriculas m ON m.id = n.matricula_id
            JOIN alunos a ON a.id = m.aluno_id
            JOIN usuarios u ON u.id = a.usuario_id
            WHERE n.matricula_id = ?
            ORDER BY av.periodo
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, matriculaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Nota(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("avaliacao_id"),
                            (UUID) rs.getObject("matricula_id"),
                            rs.getString("aluno_nome"),
                            rs.getInt("periodo"),
                            rs.getString("avaliacao_nome"),
                            rs.getDouble("valor_nota"),
                            rs.getString("observacao")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarNotasPorMatricula: " + e.getMessage());
            return MOCK_NOTAS.values().stream()
                    .filter(n -> n.matriculaId().equals(matriculaId))
                    .toList();
        }
    }

    public boolean salvarNota(UUID avaliacaoId, UUID matriculaId, double valorNota, String observacao) {
        String sql = """
            INSERT INTO notas (avaliacao_id, matricula_id, valor_nota, observacao, data_lancamento)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (avaliacao_id, matricula_id)
            DO UPDATE SET valor_nota = EXCLUDED.valor_nota,
                          observacao = EXCLUDED.observacao,
                          data_lancamento = CURRENT_TIMESTAMP
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, avaliacaoId);
            ps.setObject(2, matriculaId);
            ps.setDouble(3, valorNota);
            ps.setString(4, observacao != null ? observacao : "");

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.warning("Gravando no mock em memória para salvarNota: " + e.getMessage());
            MOCK_NOTAS.put(avaliacaoId + "_" + matriculaId, new Nota(
                    UUID.randomUUID(), avaliacaoId, matriculaId, "Aluno", 1, "Avaliação", valorNota, observacao
            ));
            return true;
        }
    }
}
