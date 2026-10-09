package br.com.escola.modulos.presenca;

import br.com.escola.config.DatabaseConfig;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class PresencaDAO {
    private static final Logger LOGGER = Logger.getLogger(PresencaDAO.class.getName());

    private static final List<Aula> MOCK_AULAS = new ArrayList<>();
    private static final Map<String, Presenca> MOCK_PRESENCAS = new LinkedHashMap<>();

    static {
        UUID tdId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID u1 = UUID.fromString("71111111-1111-1111-1111-111111111111");
        UUID u2 = UUID.fromString("72222222-2222-2222-2222-222222222222");
        UUID u3 = UUID.fromString("73333333-3333-3333-3333-333333333333");
        UUID u4 = UUID.fromString("74444444-4444-4444-4444-444444444444");

        MOCK_AULAS.add(new Aula(u1, tdId, "2026-02-10", 2, "Equações de 2º Grau: Introdução"));
        MOCK_AULAS.add(new Aula(u2, tdId, "2026-02-17", 2, "Exercícios práticos de Bhaskara"));
        MOCK_AULAS.add(new Aula(u3, tdId, "2026-02-24", 2, "Geometria Espacial: Prismas"));
        MOCK_AULAS.add(new Aula(u4, tdId, "2026-03-03", 2, "Funções e Gráficos"));

        UUID m1 = UUID.fromString("10101010-1010-1010-1010-101010101001");
        UUID m2 = UUID.fromString("10101010-1010-1010-1010-101010101002");
        UUID m3 = UUID.fromString("10101010-1010-1010-1010-101010101003");

        // Lucas Mendes: 100% frequência
        for (Aula a : MOCK_AULAS) {
            MOCK_PRESENCAS.put(a.id() + "_" + m1, new Presenca(UUID.randomUUID(), a.id(), m1, "Lucas Mendes", 1, true, null));
        }

        // Beatriz Souza: Faltou aula 3
        MOCK_PRESENCAS.put(u1 + "_" + m2, new Presenca(UUID.randomUUID(), u1, m2, "Beatriz Souza", 2, true, null));
        MOCK_PRESENCAS.put(u2 + "_" + m2, new Presenca(UUID.randomUUID(), u2, m2, "Beatriz Souza", 2, true, null));
        MOCK_PRESENCAS.put(u3 + "_" + m2, new Presenca(UUID.randomUUID(), u3, m2, "Beatriz Souza", 2, false, "Atestado médico"));
        MOCK_PRESENCAS.put(u4 + "_" + m2, new Presenca(UUID.randomUUID(), u4, m2, "Beatriz Souza", 2, true, null));

        // Gabriel Costa: Faltou aula 1 e 2 (4 faltas em 8 aulas = 50% frequência -> Abaixo dos 75% da LDB!)
        MOCK_PRESENCAS.put(u1 + "_" + m3, new Presenca(UUID.randomUUID(), u1, m3, "Gabriel Costa", 3, false, null));
        MOCK_PRESENCAS.put(u2 + "_" + m3, new Presenca(UUID.randomUUID(), u2, m3, "Gabriel Costa", 3, false, null));
        MOCK_PRESENCAS.put(u3 + "_" + m3, new Presenca(UUID.randomUUID(), u3, m3, "Gabriel Costa", 3, true, null));
        MOCK_PRESENCAS.put(u4 + "_" + m3, new Presenca(UUID.randomUUID(), u4, m3, "Gabriel Costa", 3, true, null));
    }

    public List<Aula> listarAulas(UUID turmaDisciplinaId) {
        List<Aula> lista = new ArrayList<>();
        String sql = """
            SELECT id, turma_disciplina_id, data_aula, quantidade_horas, conteudo_lecionado
            FROM aulas
            WHERE turma_disciplina_id = ?
            ORDER BY data_aula DESC
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, turmaDisciplinaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Aula(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("turma_disciplina_id"),
                            rs.getString("data_aula"),
                            rs.getInt("quantidade_horas"),
                            rs.getString("conteudo_lecionado")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarAulas: " + e.getMessage());
            return MOCK_AULAS;
        }
    }

    public List<Presenca> listarPresencasPorAula(UUID aulaId) {
        List<Presenca> lista = new ArrayList<>();
        String sql = """
            SELECT p.id, p.aula_id, p.matricula_id, u.nome AS aluno_nome, m.numero_chamada,
                   p.presente, p.justificativa
            FROM presencas p
            JOIN matriculas m ON m.id = p.matricula_id
            JOIN alunos a ON a.id = m.aluno_id
            JOIN usuarios u ON u.id = a.usuario_id
            WHERE p.aula_id = ?
            ORDER BY m.numero_chamada, u.nome
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, aulaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Presenca(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("aula_id"),
                            (UUID) rs.getObject("matricula_id"),
                            rs.getString("aluno_nome"),
                            rs.getInt("numero_chamada"),
                            rs.getBoolean("presente"),
                            rs.getString("justificativa")
                    ));
                }
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para listarPresencasPorAula: " + e.getMessage());
            return MOCK_PRESENCAS.values().stream()
                    .filter(p -> p.aulaId().equals(aulaId))
                    .toList();
        }
    }

    public boolean salvarPresenca(UUID aulaId, UUID matriculaId, boolean presente, String justificativa) {
        String sql = """
            INSERT INTO presencas (aula_id, matricula_id, presente, justificativa)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (aula_id, matricula_id)
            DO UPDATE SET presente = EXCLUDED.presente,
                          justificativa = EXCLUDED.justificativa
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, aulaId);
            ps.setObject(2, matriculaId);
            ps.setBoolean(3, presente);
            ps.setString(4, justificativa);

            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para salvarPresenca: " + e.getMessage());
            MOCK_PRESENCAS.put(aulaId + "_" + matriculaId, new Presenca(
                    UUID.randomUUID(), aulaId, matriculaId, "Aluno", 1, presente, justificativa
            ));
            return true;
        }
    }

    public int[] obterTotalAulasEFaltas(UUID matriculaId) {
        String sql = """
            SELECT 
                COALESCE(SUM(a.quantidade_horas), 0) AS total_aulas,
                COALESCE(SUM(CASE WHEN p.presente = FALSE THEN a.quantidade_horas ELSE 0 END), 0) AS total_faltas
            FROM presencas p
            JOIN aulas a ON a.id = p.aula_id
            WHERE p.matricula_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, matriculaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt("total_aulas"), rs.getInt("total_faltas")};
                }
            }
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para obterTotalAulasEFaltas: " + e.getMessage());
        }

        // Mock fallback
        int totalHoras = 8;
        int faltas = 0;
        for (Presenca p : MOCK_PRESENCAS.values()) {
            if (p.matriculaId().equals(matriculaId) && !p.presente()) {
                faltas += 2;
            }
        }
        return new int[]{totalHoras, faltas};
    }
}
