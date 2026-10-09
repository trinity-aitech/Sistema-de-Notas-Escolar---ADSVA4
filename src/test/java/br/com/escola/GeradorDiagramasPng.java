package br.com.escola;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GeradorDiagramasPng {

    public static void main(String[] args) {
        File outDir = new File("public/diagramas");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            gerarDiagramaArquitetura(new File(outDir, "02_arquitetura_camadas_monolito.png"));
            gerarDiagramaBanco(new File(outDir, "03_modelo_relacional_banco_dados.png"));
            gerarDiagramaRegras(new File(outDir, "04_fluxo_motor_regras_ldb.png"));
            System.out.println("✅ Todos os diagramas PNG foram gerados com sucesso em public/diagramas/!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void aplicarAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    // --- DIAGRAMA 2: ARQUITETURA ---
    private static void gerarDiagramaArquitetura(File file) throws IOException {
        int w = 1400, h = 950;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        aplicarAntiAliasing(g);

        // Fundo
        g.setColor(new Color(15, 23, 42)); // Slate 900
        g.fillRect(0, 0, w, h);

        // Cabeçalho
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Arquitetura do Sistema de Notas Escolar", 50, 55);
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString("Monólito Modular em Java Puro (Java SE 21+, Virtual Threads & JDBC) + PostgreSQL / Supabase", 50, 85);

        // 1. Camada Clientes
        desenharCaixa(g, 50, 120, 1300, 110, new Color(30, 41, 59), new Color(51, 65, 85), "1. ATORES EXTERNOS (CLIENTES WEB / HTTPS)");
        desenharModulo(g, 80, 160, 360, 50, new Color(37, 99, 235), "Professor (Lançamento de notas e chamada)");
        desenharModulo(g, 520, 160, 360, 50, new Color(16, 185, 129), "Aluno (Boletim e Frequência LDB)");
        desenharModulo(g, 960, 160, 360, 50, new Color(245, 158, 11), "Pais / Responsáveis (Alertas de Infrequência)");

        // 2. Camada HTTP & Roteamento
        desenharCaixa(g, 50, 260, 1300, 130, new Color(30, 41, 59), new Color(51, 65, 85), "2. CAMADA DE ENTRADA HTTP & SERVIDOR JAVA PURO");
        desenharModulo(g, 80, 305, 360, 60, new Color(99, 102, 241), "HttpServer Nativo (Virtual Threads)");
        desenharModulo(g, 520, 305, 360, 60, new Color(99, 102, 241), "Roteador REST & CORS (/api/*)");
        desenharModulo(g, 960, 305, 360, 60, new Color(99, 102, 241), "StaticFileHandler (Frontend Web /public)");

        // 3. Módulos de Domínio & Regras
        desenharCaixa(g, 50, 420, 1300, 220, new Color(30, 41, 59), new Color(51, 65, 85), "3. MÓDULOS DE DOMÍNIO & REGRAS DE NEGÓCIO (NÚCLEO)");
        desenharCardModulo(g, 80, 465, 270, 150, new Color(14, 165, 233), "Alunos e Turmas", "• Cadastro de Estudantes\n• Matrículas e Séries\n• Atribuição de Professores");
        desenharCardModulo(g, 390, 465, 270, 150, new Color(16, 185, 129), "Notas e Avaliações", "• Avaliações Bimestrais\n• Lançamento de Notas\n• Médias Ponderadas/Simples");
        desenharCardModulo(g, 700, 465, 270, 150, new Color(245, 158, 11), "Presença e Diário", "• Registro de Horas/Aulas\n• Apuração de Faltas\n• Percentual de Frequência");
        desenharCardModulo(g, 1010, 465, 310, 150, new Color(239, 68, 68), "Motor de Regras", "• Art. 24 LDB: Freq >= 75%\n• Normas do CEE\n• Médias de Corte e Exame\n• Situação Final");

        // 4. Camada de Acesso a Dados
        desenharCaixa(g, 50, 670, 1300, 100, new Color(30, 41, 59), new Color(51, 65, 85), "4. CAMADA DE PERSISTÊNCIA (JDBC PURO)");
        desenharModulo(g, 80, 710, 220, 45, new Color(71, 85, 105), "AlunoDAO");
        desenharModulo(g, 330, 710, 220, 45, new Color(71, 85, 105), "TurmaDAO");
        desenharModulo(g, 580, 710, 220, 45, new Color(71, 85, 105), "NotaDAO");
        desenharModulo(g, 830, 710, 220, 45, new Color(71, 85, 105), "PresencaDAO");
        desenharModulo(g, 1080, 710, 240, 45, new Color(71, 85, 105), "DatabaseConfig (JDBC)");

        // 5. Banco de Dados
        desenharCaixa(g, 50, 795, 1300, 110, new Color(15, 23, 42), new Color(37, 99, 235), "5. BANCO DE DADOS RELACIONAL");
        desenharModulo(g, 350, 835, 600, 50, new Color(30, 64, 175), "🐘 PostgreSQL / Supabase (11 Tabelas Normalizadas 3FN)");

        g.dispose();
        ImageIO.write(img, "PNG", file);
    }

    // --- DIAGRAMA 3: MODELO DO BANCO DE DADOS ---
    private static void gerarDiagramaBanco(File file) throws IOException {
        int w = 1500, h = 1000;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        aplicarAntiAliasing(g);

        g.setColor(new Color(15, 23, 42));
        g.fillRect(0, 0, w, h);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Modelo de Dados Relacional (PostgreSQL / Supabase)", 50, 55);
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString("Esquema relacional em 3ª Forma Normal com integridade referencial e índices", 50, 85);

        // Tabelas Principais
        desenharTabelaDb(g, 50, 120, 260, "usuarios", new String[]{"PK: id (UUID)", "nome (VARCHAR)", "email (UNIQUE)", "senha_hash", "perfil (CHECK)", "ativo (BOOLEAN)"});
        desenharTabelaDb(g, 340, 120, 260, "professores", new String[]{"PK: id (UUID)", "FK: usuario_id", "matricula_funcional", "formacao"});
        desenharTabelaDb(g, 630, 120, 260, "responsaveis", new String[]{"PK: id (UUID)", "FK: usuario_id", "telefone", "parentesco"});
        desenharTabelaDb(g, 920, 120, 260, "alunos", new String[]{"PK: id (UUID)", "FK: usuario_id", "FK: responsavel_id", "matricula (UNIQUE)", "data_nascimento"});
        desenharTabelaDb(g, 1210, 120, 240, "regras_escola", new String[]{"PK: id (UUID)", "chave (UNIQUE)", "valor", "descricao"});

        desenharTabelaDb(g, 50, 360, 260, "turmas", new String[]{"PK: id (UUID)", "codigo", "ano_letivo", "serie", "turno (CHECK)"});
        desenharTabelaDb(g, 340, 360, 260, "disciplinas", new String[]{"PK: id (UUID)", "codigo (UNIQUE)", "nome", "carga_horaria"});
        desenharTabelaDb(g, 630, 360, 260, "turma_disciplinas", new String[]{"PK: id (UUID)", "FK: turma_id", "FK: disciplina_id", "FK: professor_id"});
        desenharTabelaDb(g, 920, 360, 260, "matriculas", new String[]{"PK: id (UUID)", "FK: aluno_id", "FK: turma_id", "numero_chamada", "status"});

        desenharTabelaDb(g, 50, 620, 280, "avaliacoes", new String[]{"PK: id (UUID)", "FK: turma_disciplina_id", "periodo (1-4)", "nome", "tipo (PROVA/REC)", "peso", "nota_maxima", "data_avaliacao"});
        desenharTabelaDb(g, 360, 620, 260, "notas", new String[]{"PK: id (UUID)", "FK: avaliacao_id", "FK: matricula_id", "valor_nota (0-10)", "observacao"});
        desenharTabelaDb(g, 650, 620, 260, "aulas", new String[]{"PK: id (UUID)", "FK: turma_disciplina_id", "data_aula", "quantidade_horas", "conteudo_lecionado"});
        desenharTabelaDb(g, 940, 620, 260, "presencas", new String[]{"PK: id (UUID)", "FK: aula_id", "FK: matricula_id", "presente (BOOLEAN)", "justificativa"});
        desenharTabelaDb(g, 1220, 620, 230, "resultados_finais", new String[]{"PK: id (UUID)", "FK: matricula_id", "FK: turma_disciplina_id", "media_anual", "nota_recuperacao", "media_final", "percentual_frequencia", "situacao (CHECK)"});

        g.dispose();
        ImageIO.write(img, "PNG", file);
    }

    // --- DIAGRAMA 4: FLUXO DE REGRAS LDB ---
    private static void gerarDiagramaRegras(File file) throws IOException {
        int w = 1400, h = 900;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        aplicarAntiAliasing(g);

        g.setColor(new Color(15, 23, 42));
        g.fillRect(0, 0, w, h);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Fluxo de Avaliação Escolar: LDB 9.394/96 Art. 24 & Normas CEE", 50, 55);
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString("Lógica de decisão implementada no MotorRegrasService.java", 50, 85);

        // Bloco Entrada
        desenharCaixa(g, 50, 130, 360, 220, new Color(30, 41, 59), new Color(51, 65, 85), "DADOS DE ENTRADA DO ALUNO");
        desenharModulo(g, 80, 175, 300, 45, new Color(37, 99, 235), "Notas Bimestrais (1º, 2º, 3º, 4º)");
        desenharModulo(g, 80, 235, 300, 45, new Color(16, 185, 129), "Diário: Total Aulas & Faltas");
        desenharModulo(g, 80, 295, 300, 45, new Color(245, 158, 11), "Nota de Recuperação (se houver)");

        // Bloco Validação LDB
        desenharCaixa(g, 470, 130, 420, 220, new Color(30, 41, 59), new Color(239, 68, 68), "PASSO 1: REGRA ART. 24 DA LDB 9.394/96");
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.drawString("Cálculo: % Freq = ((Aulas - Faltas) / Aulas) * 100", 490, 180);
        desenharModulo(g, 490, 205, 380, 50, new Color(220, 38, 38), "Frequência < 75%? -> Infrequência Compulsória!");
        desenharModulo(g, 490, 275, 380, 50, new Color(22, 163, 74), "Frequência >= 75%? -> Regular perante a LDB");

        // Bloco Avaliação Acadêmica
        desenharCaixa(g, 50, 400, 840, 230, new Color(30, 41, 59), new Color(51, 65, 85), "PASSO 2: REGULAMENTO DA ESCOLA & CRITÉRIOS DE MÉDIA");
        desenharModulo(g, 80, 450, 360, 50, new Color(22, 163, 74), "Média Anual >= 7.0 (Aprovação Direta)");
        desenharModulo(g, 480, 450, 380, 50, new Color(245, 158, 11), "4.0 <= Média < 7.0 (Direito a Exame Final)");
        desenharModulo(g, 80, 530, 360, 50, new Color(220, 38, 38), "Média Anual < 4.0 (Reprovação por Nota)");
        desenharModulo(g, 480, 530, 380, 50, new Color(99, 102, 241), "Média Pós-Exame = (Média + Exame) / 2 >= 5.0");

        // Bloco Resultados Finais
        desenharCaixa(g, 940, 130, 410, 500, new Color(30, 41, 59), new Color(51, 65, 85), "RESULTADOS FINAIS POSSÍVEIS");
        desenharModulo(g, 970, 180, 350, 50, new Color(22, 163, 74), "✅ APROVADO (Média >= 7.0 e Freq >= 75%)");
        desenharModulo(g, 970, 245, 350, 50, new Color(16, 185, 129), "✅ APROVADO_RECUPERACAO (Média Final >= 5.0)");
        desenharModulo(g, 970, 310, 350, 50, new Color(245, 158, 11), "⚠️ RECUPERACAO (Aguardando exame)");
        desenharModulo(g, 970, 375, 350, 50, new Color(220, 38, 38), "❌ REPROVADO_POR_NOTA (Média insuficiente)");
        desenharModulo(g, 970, 440, 350, 50, new Color(185, 28, 28), "❌ REPROVADO_POR_FALTA (Freq < 75% LDB)");
        desenharModulo(g, 970, 505, 350, 50, new Color(127, 29, 29), "❌ REPROVADO_NOTA_E_FALTA");

        // Rodapé de Conformidade
        desenharCaixa(g, 50, 670, 1300, 160, new Color(15, 23, 42), new Color(37, 99, 235), "BASE LEGAL & JURISPRUDÊNCIA EDUCACIONAL");
        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.drawString("• Art. 24, Inciso VI da Lei de Diretrizes e Bases da Educação Nacional (LDB 9.394/1996):", 70, 715);
        g.drawString("  'O controle de frequência fica a cargo da escola (...) exigida a frequência mínima de setenta e cinco por cento (75%) do total de horas letivas para aprovação.'", 85, 740);
        g.drawString("• A reprovação por falta tem prevalência sobre o rendimento pedagógico: aluno com nota máxima, porém infrequente, é reprovado compulsoriamente.", 70, 775);
        g.drawString("• Alunos com frequência regular e média entre 4.0 e 6.9 têm direito legal assegurado à recuperação paralela e final conforme normas do CEE.", 70, 805);

        g.dispose();
        ImageIO.write(img, "PNG", file);
    }

    // Utilitários de Desenho
    private static void desenharCaixa(Graphics2D g, int x, int y, int w, int h, Color bg, Color border, String titulo) {
        g.setColor(bg);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 14, 14));
        g.setColor(border);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, 14, 14));

        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(titulo, x + 16, y + 25);
    }

    private static void desenharModulo(Graphics2D g, int x, int y, int w, int h, Color cor, String texto) {
        g.setColor(cor);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 10, 10));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(texto)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, tx, ty);
    }

    private static void desenharCardModulo(Graphics2D g, int x, int y, int w, int h, Color topo, String titulo, String linhas) {
        g.setColor(new Color(51, 65, 85));
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 10, 10));

        g.setColor(topo);
        g.fill(new RoundRectangle2D.Float(x, y, w, 32, 10, 10));
        g.fillRect(x, y + 16, w, 16);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString(titulo, x + 10, y + 22);

        g.setColor(new Color(241, 245, 249));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        int ly = y + 54;
        for (String linha : linhas.split("\n")) {
            g.drawString(linha, x + 10, ly);
            ly += 22;
        }
    }

    private static void desenharTabelaDb(Graphics2D g, int x, int y, int w, String nomeTabela, String[] campos) {
        int h = 36 + campos.length * 22 + 10;
        g.setColor(new Color(30, 41, 59));
        g.fill(new RoundRectangle2D.Float(x, y, w, h, 10, 10));
        g.setColor(new Color(51, 65, 85));
        g.draw(new RoundRectangle2D.Float(x, y, w, h, 10, 10));

        g.setColor(new Color(37, 99, 235));
        g.fill(new RoundRectangle2D.Float(x, y, w, 30, 10, 10));
        g.fillRect(x, y + 16, w, 14);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString("📁 " + nomeTabela, x + 10, y + 20);

        g.setFont(new Font("Consolas", Font.PLAIN, 12));
        int cy = y + 50;
        for (String campo : campos) {
            if (campo.startsWith("PK")) {
                g.setColor(new Color(250, 204, 21));
            } else if (campo.startsWith("FK")) {
                g.setColor(new Color(147, 197, 253));
            } else {
                g.setColor(new Color(226, 232, 240));
            }
            g.drawString(campo, x + 10, cy);
            cy += 22;
        }
    }
}
