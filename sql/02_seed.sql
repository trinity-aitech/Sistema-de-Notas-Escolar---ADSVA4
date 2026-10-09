-- ==========================================================
-- DADOS DE TESTE INICIAIS (SEED DATA)
-- ==========================================================

-- Limpeza prévia segura para testes
TRUNCATE TABLE resultados_finais, presencas, aulas, notas, avaliacoes, matriculas, turma_disciplinas, turmas, disciplinas, alunos, responsaveis, professores, usuarios CASCADE;

-- 1. Usuários (Senha em texto simples para teste didático: "123456")
INSERT INTO usuarios (id, nome, email, senha_hash, perfil) VALUES
('11111111-1111-1111-1111-111111111111', 'Administrador da Escola', 'admin@escola.com', '123456', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', 'Prof. Carlos Silva', 'carlos.silva@escola.com', '123456', 'PROFESSOR'),
('33333333-3333-3333-3333-333333333333', 'Maria Mendes', 'maria.mendes@email.com', '123456', 'RESPONSAVEL'),
('44444444-4444-4444-4444-444444444444', 'Lucas Mendes', 'lucas.mendes@escola.com', '123456', 'ALUNO'),
('55555555-5555-5555-5555-555555555555', 'Beatriz Souza', 'beatriz.souza@escola.com', '123456', 'ALUNO'),
('66666666-6666-6666-6666-666666666666', 'Gabriel Costa', 'gabriel.costa@escola.com', '123456', 'ALUNO');

-- 2. Responsável
INSERT INTO responsaveis (id, usuario_id, telefone, parentesco) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '33333333-3333-3333-3333-333333333333', '(11) 98888-7777', 'Mãe');

-- 3. Alunos
INSERT INTO alunos (id, usuario_id, responsavel_id, matricula, data_nascimento) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01', '44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026001', '2011-05-14'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02', '55555555-5555-5555-5555-555555555555', NULL, '2026002', '2011-08-22'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03', '66666666-6666-6666-6666-666666666666', NULL, '2026003', '2011-02-10');

-- 4. Professor
INSERT INTO professores (id, usuario_id, matricula_funcional, formacao) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'PROF-2026-01', 'Licenciatura em Matemática');

-- 5. Turma e Disciplinas
INSERT INTO turmas (id, codigo, ano_letivo, serie, turno) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', '9A', 2026, '9º Ano Fundamental', 'MANHA');

INSERT INTO disciplinas (id, codigo, nome, carga_horaria) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'MAT-9', 'Matemática', 80);

-- 6. Vinculação da Turma com Disciplina e Professor
INSERT INTO turma_disciplinas (id, turma_id, disciplina_id, professor_id) VALUES
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'cccccccc-cccc-cccc-cccc-cccccccccccc');

-- 7. Matrículas na Turma
INSERT INTO matriculas (id, aluno_id, turma_id, numero_chamada, status) VALUES
('10101010-1010-1010-1010-101010101001', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 1, 'ATIVA'),
('10101010-1010-1010-1010-101010101002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb02', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 2, 'ATIVA'),
('10101010-1010-1010-1010-101010101003', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb03', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 3, 'ATIVA');

-- 8. Avaliações (1º ao 4º Bimestre)
INSERT INTO avaliacoes (id, turma_disciplina_id, periodo, nome, tipo, peso, nota_maxima, data_avaliacao) VALUES
('a1111111-1111-1111-1111-111111111111', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 1, 'Prova Bimestral 1', 'PROVA', 1.00, 10.00, '2026-03-25'),
('a2222222-2222-2222-2222-222222222222', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 2, 'Prova Bimestral 2', 'PROVA', 1.00, 10.00, '2026-06-20'),
('a3333333-3333-3333-3333-333333333333', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 3, 'Prova Bimestral 3', 'PROVA', 1.00, 10.00, '2026-09-18'),
('a4444444-4444-4444-4444-444444444444', 'ffffffff-ffff-ffff-ffff-ffffffffffff', 4, 'Prova Bimestral 4', 'PROVA', 1.00, 10.00, '2026-11-28');

-- 9. Notas Lançadas
-- Lucas Mendes (Aluno exemplar: notas 8.5, 9.0, 8.0, 9.5)
INSERT INTO notas (avaliacao_id, matricula_id, valor_nota) VALUES
('a1111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101001', 8.50),
('a2222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101001', 9.00),
('a3333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101001', 8.00),
('a4444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101001', 9.50);

-- Beatriz Souza (Aluna em recuperação: notas 5.0, 6.0, 4.5, 6.5 -> Média 5.5)
INSERT INTO notas (avaliacao_id, matricula_id, valor_nota) VALUES
('a1111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101002', 5.00),
('a2222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101002', 6.00),
('a3333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101002', 4.50),
('a4444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101002', 6.50);

-- Gabriel Costa (Notas boas, mas muitas faltas - teste da regra dos 75% da LDB)
INSERT INTO notas (avaliacao_id, matricula_id, valor_nota) VALUES
('a1111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101003', 7.50),
('a2222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101003', 8.00),
('a3333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101003', 7.00),
('a4444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101003', 8.50);

-- 10. Aulas do Diário de Classe
INSERT INTO aulas (id, turma_disciplina_id, data_aula, quantidade_horas, conteudo_lecionado) VALUES
('71111111-1111-1111-1111-111111111111', 'ffffffff-ffff-ffff-ffff-ffffffffffff', '2026-02-10', 2, 'Equações de 2º Grau: Introdução e Bhaskara'),
('72222222-2222-2222-2222-222222222222', 'ffffffff-ffff-ffff-ffff-ffffffffffff', '2026-02-17', 2, 'Exercícios práticos de Bhaskara'),
('73333333-3333-3333-3333-333333333333', 'ffffffff-ffff-ffff-ffff-ffffffffffff', '2026-02-24', 2, 'Geometria Espacial: Prismas e Cilindros'),
('74444444-4444-4444-4444-444444444444', 'ffffffff-ffff-ffff-ffff-ffffffffffff', '2026-03-03', 2, 'Funções e Gráficos no Plano Cartesiano');

-- 11. Presenças Registradas (Total 8 horas/aulas dadas)
-- Aula 1: Todos presentes
INSERT INTO presencas (aula_id, matricula_id, presente) VALUES
('71111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101001', TRUE),
('71111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101002', TRUE),
('71111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101003', FALSE);

-- Aula 2: Gabriel faltou de novo
INSERT INTO presencas (aula_id, matricula_id, presente) VALUES
('72222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101001', TRUE),
('72222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101002', TRUE),
('72222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101003', FALSE);

-- Aula 3: Beatriz faltou
INSERT INTO presencas (aula_id, matricula_id, presente) VALUES
('73333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101001', TRUE),
('73333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101002', FALSE),
('73333333-3333-3333-3333-333333333333', '10101010-1010-1010-1010-101010101003', TRUE);

-- Aula 4: Todos presentes
INSERT INTO presencas (aula_id, matricula_id, presente) VALUES
('74444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101001', TRUE),
('74444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101002', TRUE),
('74444444-4444-4444-4444-444444444444', '10101010-1010-1010-1010-101010101003', TRUE);
