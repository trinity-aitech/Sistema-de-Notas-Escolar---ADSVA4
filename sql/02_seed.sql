-- ==========================================================
-- DADOS DE TESTE INICIAIS (SEED DATA)
-- MODELAGEM CONFORME DIAGRAMA DE CLASSES UML
-- ==========================================================

-- Limpeza prévia segura para reinicialização
TRUNCATE TABLE presencas, notas, historicos, disciplinas, alunos, cursos, responsaveis, professores, usuarios RESTART IDENTITY CASCADE;

-- 1. Usuários
INSERT INTO usuarios (id, nome, email, senha, tipo_usuario) VALUES
(1, 'Administrador da Escola', 'admin@escola.com', '123456', 'ADMIN'),
(2, 'Prof. Carlos Silva', 'carlos.silva@escola.com', '123456', 'PROFESSOR'),
(3, 'Maria Mendes', 'maria.mendes@email.com', '123456', 'RESPONSAVEL'),
(4, 'Lucas Mendes', 'lucas.mendes@escola.com', '123456', 'ALUNO'),
(5, 'Beatriz Souza', 'beatriz.souza@escola.com', '123456', 'ALUNO'),
(6, 'Gabriel Costa', 'gabriel.costa@escola.com', '123456', 'ALUNO');

-- 2. Professores (Especialidade)
INSERT INTO professores (id, nome, especialidade) VALUES
(2, 'Prof. Carlos Silva', 'Matemática e Estatística Aplicada');

-- 3. Responsáveis (Telefone, CPF)
INSERT INTO responsaveis (id, nome, telefone, cpf) VALUES
(3, 'Maria Mendes', '(11) 98888-7777', '123.456.789-00');

-- 4. Cursos (Nome, Ano, Professor)
INSERT INTO cursos (id, nome, ano, professor_id) VALUES
(1, '9º Ano Fundamental', 2026, 2);

-- 5. Alunos (Nome, RA, Responsável, Curso)
INSERT INTO alunos (id, nome, ra, responsavel_id, curso_id) VALUES
(4, 'Lucas Mendes', '2026001', 3, 1),
(5, 'Beatriz Souza', '2026002', NULL, 1),
(6, 'Gabriel Costa', '2026003', NULL, 1);

-- 6. Históricos (Matrícula acadêmica do Aluno)
INSERT INTO historicos (id, aluno_id, matricula) VALUES
(1, 4, 'MAT-2026-001'),
(2, 5, 'MAT-2026-002'),
(3, 6, 'MAT-2026-003');

-- 7. Disciplinas (Nome, Curso)
INSERT INTO disciplinas (id, nome, curso_id, carga_horaria) VALUES
(1, 'Matemática', 1, 80);

-- 8. Notas (Aluno, Disciplina, Valor, Data, Período, Observação)
-- Lucas Mendes (Aluno exemplar: notas 8.5, 9.0, 8.0, 9.5 -> Média 8.75)
INSERT INTO notas (aluno_id, disciplina_id, valor, data, periodo, observacao) VALUES
(4, 1, 8.50, '2026-03-25', 1, 'Prova Bimestral 1'),
(4, 1, 9.00, '2026-06-20', 2, 'Prova Bimestral 2'),
(4, 1, 8.00, '2026-09-18', 3, 'Prova Bimestral 3'),
(4, 1, 9.50, '2026-11-28', 4, 'Prova Bimestral 4');

-- Beatriz Souza (Aluna em recuperação: notas 5.0, 6.0, 4.5, 6.5 -> Média 5.5)
INSERT INTO notas (aluno_id, disciplina_id, valor, data, periodo, observacao) VALUES
(5, 1, 5.00, '2026-03-25', 1, 'Prova Bimestral 1'),
(5, 1, 6.00, '2026-06-20', 2, 'Prova Bimestral 2'),
(5, 1, 4.50, '2026-09-18', 3, 'Prova Bimestral 3'),
(5, 1, 6.50, '2026-11-28', 4, 'Prova Bimestral 4');

-- Gabriel Costa (Notas boas, mas muitas faltas -> teste da LDB 75%)
INSERT INTO notas (aluno_id, disciplina_id, valor, data, periodo, observacao) VALUES
(6, 1, 7.50, '2026-03-25', 1, 'Prova Bimestral 1'),
(6, 1, 8.00, '2026-06-20', 2, 'Prova Bimestral 2'),
(6, 1, 7.00, '2026-09-18', 3, 'Prova Bimestral 3'),
(6, 1, 8.50, '2026-11-28', 4, 'Prova Bimestral 4');

-- 9. Presenças (Aluno, Disciplina, Data, Status)
-- Aula 1 (10/02/2026)
INSERT INTO presencas (aluno_id, disciplina_id, data, status) VALUES
(4, 1, '2026-02-10', 'PRESENTE'),
(5, 1, '2026-02-10', 'PRESENTE'),
(6, 1, '2026-02-10', 'FALTA');

-- Aula 2 (17/02/2026)
INSERT INTO presencas (aluno_id, disciplina_id, data, status) VALUES
(4, 1, '2026-02-17', 'PRESENTE'),
(5, 1, '2026-02-17', 'PRESENTE'),
(6, 1, '2026-02-17', 'FALTA');

-- Aula 3 (24/02/2026)
INSERT INTO presencas (aluno_id, disciplina_id, data, status) VALUES
(4, 1, '2026-02-24', 'PRESENTE'),
(5, 1, '2026-02-24', 'FALTA'),
(6, 1, '2026-02-24', 'PRESENTE');

-- Aula 4 (03/03/2026)
INSERT INTO presencas (aluno_id, disciplina_id, data, status) VALUES
(4, 1, '2026-03-03', 'PRESENTE'),
(5, 1, '2026-03-03', 'PRESENTE'),
(6, 1, '2026-03-03', 'PRESENTE');

-- Ajuste de sequências
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('cursos_id_seq', (SELECT MAX(id) FROM cursos));
SELECT setval('disciplinas_id_seq', (SELECT MAX(id) FROM disciplinas));
SELECT setval('historicos_id_seq', (SELECT MAX(id) FROM historicos));
SELECT setval('notas_id_seq', (SELECT MAX(id) FROM notas));
SELECT setval('presencas_id_seq', (SELECT MAX(id) FROM presencas));
