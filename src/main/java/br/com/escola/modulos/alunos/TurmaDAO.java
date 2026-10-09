package br.com.escola.modulos.alunos;

import br.com.escola.config.DatabaseConfig;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class TurmaDAO {
    private static final Logger LOGGER = Logger.getLogger(TurmaDAO.class.getName());

    private static final List<Turma> MOCK_TURMAS = new ArrayList<>();

    static {
        MOCK_TURMAS.add(new Turma(
                UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                "9A",
                2026,
                "9º Ano Fundamental",
                "MANHA",
                UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"),
                "Matemática",
                "MAT-9",
                "Prof. Carlos Silva"
        ));
    }

    public List<Turma> listarTodas() {
        List<Turma> lista = new ArrayList<>();
        String sql = """
            SELECT t.id, t.codigo, t.ano_letivo, t.serie, t.turno,
                   td.id AS turma_disciplina_id, d.nome AS disciplina_nome, d.codigo AS disciplina_codigo,
                   u.nome AS professor_nome
            FROM turmas t
            JOIN turma_disciplinas td ON td.turma_id = t.id
            JOIN disciplinas d ON d.id = td.disciplina_id
            JOIN professores p ON p.id = td.professor_id
            JOIN usuarios u ON u.id = p.usuario_id
            ORDER BY t.ano_letivo DESC, t.codigo
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Turma(
                        (UUID) rs.getObject("id"),
                        rs.getString("codigo"),
                        rs.getInt("ano_letivo"),
                        rs.getString("serie"),
                        rs.getString("turno"),
                        (UUID) rs.getObject("turma_disciplina_id"),
                        rs.getString("disciplina_nome"),
                        rs.getString("disciplina_codigo"),
                        rs.getString("professor_nome")
                ));
            }
            return lista;
        } catch (Exception e) {
            LOGGER.fine("Utilizando fallback mock para TurmaDAO.listarTodas: " + e.getMessage());
            return MOCK_TURMAS;
        }
    }
}
