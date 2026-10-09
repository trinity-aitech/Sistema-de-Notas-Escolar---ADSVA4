-- ==========================================================
-- SISTEMA DE NOTAS ESCOLAR - ESQUEMA DE BANCO DE DADOS (POSTGRESQL / SUPABASE)
-- ==========================================================

-- 1. Tabela de Usuários (Autenticação e Perfis)
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL CHECK (perfil IN ('ADMIN', 'PROFESSOR', 'ALUNO', 'RESPONSAVEL')),
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Responsáveis
CREATE TABLE IF NOT EXISTS responsaveis (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    telefone VARCHAR(20) NOT NULL,
    parentesco VARCHAR(50) NOT NULL -- Pai, Mãe, Tutor Legal
);

-- 3. Tabela de Alunos
CREATE TABLE IF NOT EXISTS alunos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    responsavel_id UUID REFERENCES responsaveis(id) ON DELETE SET NULL,
    matricula VARCHAR(30) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL
);

-- 4. Tabela de Professores
CREATE TABLE IF NOT EXISTS professores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    matricula_funcional VARCHAR(30) UNIQUE NOT NULL,
    formacao VARCHAR(150)
);

-- 5. Estrutura Escolar e Turmas
CREATE TABLE IF NOT EXISTS turmas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(20) NOT NULL, -- Ex: "9A", "3EM-A"
    ano_letivo INT NOT NULL,     -- Ex: 2026
    serie VARCHAR(50) NOT NULL,  -- Ex: "9º Ano Fundamental"
    turno VARCHAR(20) NOT NULL CHECK (turno IN ('MANHA', 'TARDE', 'NOITE', 'INTEGRAL')),
    UNIQUE(codigo, ano_letivo)
);

CREATE TABLE IF NOT EXISTS disciplinas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    carga_horaria INT NOT NULL -- Ex: 80 horas/aulas
);

-- 6. Atribuição de Disciplina e Professor na Turma
CREATE TABLE IF NOT EXISTS turma_disciplinas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    turma_id UUID NOT NULL REFERENCES turmas(id) ON DELETE CASCADE,
    disciplina_id UUID NOT NULL REFERENCES disciplinas(id) ON DELETE CASCADE,
    professor_id UUID NOT NULL REFERENCES professores(id) ON DELETE RESTRICT,
    UNIQUE(turma_id, disciplina_id)
);

-- 7. Matrículas de Alunos na Turma
CREATE TABLE IF NOT EXISTS matriculas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aluno_id UUID NOT NULL REFERENCES alunos(id) ON DELETE RESTRICT,
    turma_id UUID NOT NULL REFERENCES turmas(id) ON DELETE RESTRICT,
    numero_chamada INT,
    status VARCHAR(20) DEFAULT 'ATIVA' CHECK (status IN ('ATIVA', 'TRANSFERIDA', 'CANCELADA')),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(aluno_id, turma_id)
);

-- 8. Avaliações e Notas
CREATE TABLE IF NOT EXISTS avaliacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    turma_disciplina_id UUID NOT NULL REFERENCES turma_disciplinas(id) ON DELETE CASCADE,
    periodo INT NOT NULL CHECK (periodo BETWEEN 1 AND 4), -- 1º ao 4º Bimestre
    nome VARCHAR(100) NOT NULL, -- "Prova Bimestral 1", "Trabalho", "Recuperação Paralela"
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('PROVA', 'TRABALHO', 'PARTICIPACAO', 'RECUPERACAO')),
    peso NUMERIC(3, 2) DEFAULT 1.00,
    nota_maxima NUMERIC(4, 2) DEFAULT 10.00,
    data_avaliacao DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS notas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliacao_id UUID NOT NULL REFERENCES avaliacoes(id) ON DELETE CASCADE,
    matricula_id UUID NOT NULL REFERENCES matriculas(id) ON DELETE CASCADE,
    valor_nota NUMERIC(4, 2) NOT NULL CHECK (valor_nota >= 0 AND valor_nota <= 10.00),
    data_lancamento TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,
    UNIQUE(avaliacao_id, matricula_id)
);

-- 9. Presença e Diário de Classe
CREATE TABLE IF NOT EXISTS aulas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    turma_disciplina_id UUID NOT NULL REFERENCES turma_disciplinas(id) ON DELETE CASCADE,
    data_aula DATE NOT NULL,
    quantidade_horas INT DEFAULT 1 CHECK (quantidade_horas > 0),
    conteudo_lecionado TEXT NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS presencas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aula_id UUID NOT NULL REFERENCES aulas(id) ON DELETE CASCADE,
    matricula_id UUID NOT NULL REFERENCES matriculas(id) ON DELETE CASCADE,
    presente BOOLEAN NOT NULL DEFAULT TRUE,
    justificativa TEXT,
    UNIQUE(aula_id, matricula_id)
);

-- 10. Regras da Escola e Parâmetros LDB / CEE
CREATE TABLE IF NOT EXISTS regras_escola (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chave VARCHAR(50) UNIQUE NOT NULL,
    valor VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Inserção das regras padrão se não existirem
INSERT INTO regras_escola (chave, valor, descricao) 
VALUES
('ldb.frequencia_minima_percentual', '75.0', 'Frequência mínima obrigatória segundo a LDB 9.394/96 Art. 24'),
('escola.media_aprovacao_direta', '7.0', 'Média anual mínima para aprovação direta'),
('escola.media_minima_recuperacao', '4.0', 'Média mínima necessária para ter direito ao exame de recuperação'),
('escola.media_final_com_recuperacao', '5.0', 'Média mínima exigida após a recuperação para aprovação final'),
('escola.quantidade_periodos', '4', 'Número de bimestres/períodos letivos no ano letivo')
ON CONFLICT (chave) DO NOTHING;

-- 11. Resultados Finais Consolidados
CREATE TABLE IF NOT EXISTS resultados_finais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    matricula_id UUID NOT NULL REFERENCES matriculas(id) ON DELETE CASCADE,
    turma_disciplina_id UUID NOT NULL REFERENCES turma_disciplinas(id) ON DELETE CASCADE,
    media_anual NUMERIC(4, 2) NOT NULL,
    nota_recuperacao NUMERIC(4, 2),
    media_final NUMERIC(4, 2) NOT NULL,
    total_aulas INT NOT NULL,
    total_faltas INT NOT NULL,
    percentual_frequencia NUMERIC(5, 2) NOT NULL,
    situacao VARCHAR(35) NOT NULL CHECK (situacao IN (
        'APROVADO', 
        'APROVADO_RECUPERACAO', 
        'RECUPERACAO', 
        'REPROVADO_POR_NOTA', 
        'REPROVADO_POR_FALTA', 
        'REPROVADO_NOTA_E_FALTA'
    )),
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(matricula_id, turma_disciplina_id)
);

-- Índices para alta performance de consultas
CREATE INDEX IF NOT EXISTS idx_matriculas_turma ON matriculas(turma_id);
CREATE INDEX IF NOT EXISTS idx_matriculas_aluno ON matriculas(aluno_id);
CREATE INDEX IF NOT EXISTS idx_notas_matricula ON notas(matricula_id);
CREATE INDEX IF NOT EXISTS idx_notas_avaliacao ON notas(avaliacao_id);
CREATE INDEX IF NOT EXISTS idx_presencas_matricula ON presencas(matricula_id);
CREATE INDEX IF NOT EXISTS idx_presencas_aula ON presencas(aula_id);
CREATE INDEX IF NOT EXISTS idx_aulas_turma_disciplina ON aulas(turma_disciplina_id);
CREATE INDEX IF NOT EXISTS idx_turma_disciplinas_prof ON turma_disciplinas(professor_id);
