-- ==========================================================
-- SISTEMA DE NOTAS ESCOLAR - ESQUEMA DE BANCO DE DADOS
-- MODELAGEM CONFORME DIAGRAMA DE CLASSES UML DO PROJETO
-- ==========================================================

-- 1. Classe Base: Usuário
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(30) DEFAULT 'ALUNO' CHECK (tipo_usuario IN ('ADMIN', 'PROFESSOR', 'RESPONSAVEL', 'ALUNO')),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Subclasse: Professor (Herança de Usuario)
-- Métodos: lançarNota(), registrarPresenca()
CREATE TABLE IF NOT EXISTS professores (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    especialidade VARCHAR(100) NOT NULL
);

-- 3. Subclasse: Responsavel (Herança de Usuario)
-- Métodos: visualizarInformacoes()
CREATE TABLE IF NOT EXISTS responsaveis (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(30) NOT NULL,
    cpf VARCHAR(18) UNIQUE NOT NULL
);

-- 4. Classe: Curso
-- Relação: Professor 1 leciona 0..* Curso
-- Métodos: adicionarAluno()
CREATE TABLE IF NOT EXISTS cursos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ano INT NOT NULL,
    professor_id INT REFERENCES professores(id) ON DELETE SET NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(nome, ano)
);

-- 5. Subclasse: Aluno (Herança de Usuario)
-- Relações: 
--   Responsavel 1 <>-- 0..* Aluno (agregação)
--   Aluno 0..* inscrito 1 Curso
CREATE TABLE IF NOT EXISTS alunos (
    id INT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    ra VARCHAR(30) UNIQUE NOT NULL,
    responsavel_id INT REFERENCES responsaveis(id) ON DELETE SET NULL,
    curso_id INT REFERENCES cursos(id) ON DELETE SET NULL
);

-- 6. Classe: Historico
-- Relação: Aluno 1 possui 1 Historico
-- Métodos: incluirMateria(), verHistorico()
CREATE TABLE IF NOT EXISTS historicos (
    id SERIAL PRIMARY KEY,
    aluno_id INT UNIQUE NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    matricula VARCHAR(30) UNIQUE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Classe: Disciplina
-- Relação: Curso 1 possui 0..* Disciplina
CREATE TABLE IF NOT EXISTS disciplinas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    curso_id INT NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
    carga_horaria INT DEFAULT 80
);

-- 8. Classe: Notas
-- Relação: Disciplina 1 registra 0..* Notas (vinculadas ao Aluno)
-- Atributos: valor (float), data (Date)
-- Métodos: calcularMedia()
CREATE TABLE IF NOT EXISTS notas (
    id SERIAL PRIMARY KEY,
    aluno_id INT NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    disciplina_id INT NOT NULL REFERENCES disciplinas(id) ON DELETE CASCADE,
    valor NUMERIC(4, 2) NOT NULL CHECK (valor >= 0.0 AND valor <= 10.0),
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    periodo INT DEFAULT 1 CHECK (periodo BETWEEN 1 AND 5),
    observacao TEXT
);

-- 9. Classe: Presencas
-- Relação: Disciplina 1 registra 0..* Presencas (vinculadas ao Aluno)
-- Atributos: data (Date), status (String)
CREATE TABLE IF NOT EXISTS presencas (
    id SERIAL PRIMARY KEY,
    aluno_id INT NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    disciplina_id INT NOT NULL REFERENCES disciplinas(id) ON DELETE CASCADE,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENTE' CHECK (status IN ('PRESENTE', 'FALTA', 'JUSTIFICADA')),
    justificativa TEXT
);

-- 10. Parâmetros Legais da Escola (LDB 9.394/96 Art. 24)
CREATE TABLE IF NOT EXISTS regras_escola (
    id SERIAL PRIMARY KEY,
    chave VARCHAR(50) UNIQUE NOT NULL,
    valor VARCHAR(100) NOT NULL,
    descricao TEXT
);

INSERT INTO regras_escola (chave, valor, descricao) 
VALUES
('ldb.frequencia_minima_percentual', '75.0', 'Frequência mínima obrigatória segundo a LDB 9.394/96 Art. 24'),
('escola.media_aprovacao_direta', '7.0', 'Média anual mínima para aprovação direta'),
('escola.media_minima_recuperacao', '4.0', 'Média mínima necessária para exame de recuperação'),
('escola.media_final_com_recuperacao', '5.0', 'Média mínima exigida após recuperação para aprovação final'),
('escola.quantidade_periodos', '4', 'Número de bimestres/períodos letivos no ano')
ON CONFLICT (chave) DO NOTHING;

-- Índices de Alta Performance
CREATE INDEX IF NOT EXISTS idx_alunos_curso ON alunos(curso_id);
CREATE INDEX IF NOT EXISTS idx_alunos_responsavel ON alunos(responsavel_id);
CREATE INDEX IF NOT EXISTS idx_disciplinas_curso ON disciplinas(curso_id);
CREATE INDEX IF NOT EXISTS idx_notas_aluno_disc ON notas(aluno_id, disciplina_id);
CREATE INDEX IF NOT EXISTS idx_presencas_aluno_disc ON presencas(aluno_id, disciplina_id);
CREATE INDEX IF NOT EXISTS idx_cursos_professor ON cursos(professor_id);
