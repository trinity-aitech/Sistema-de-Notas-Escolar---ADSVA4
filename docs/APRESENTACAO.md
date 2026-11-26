# Apresentação do Projeto: Padrões de Projeto GoF no Sistema de Notas Escolar (ADSVA4)

## Estrutura da Apresentação do Grupo (6 Integrantes)

### Slide 1: Introdução e Contexto Geral
- **Apresentador:** Grupo
- **Contexto:** Sistema de Notas Escolar em Java 21 com Virtual Threads e arquitetura web.
- **Desafio:** Evoluir o monólito aplicando 3 padrões de projeto GoF (Factory Method, Facade e Observer) mantendo a retrocompatibilidade da API REST e a conformidade com as regras da LDB 9.394/96.

---

### Slide 2: Padrão Criacional - Factory Method
- **Apresentador:** Guilherme (Integrante 1)
- **Problema:** Login acoplado com estruturas condicionais `if/else` por e-mail para deduzir o perfil de usuário, dificultando a inclusão de novos tipos como Responsável sem alterar código existente (violação do princípio Open/Closed).
- **Solução Aplicada:** 
  - Interface `Usuario` (`Product`).
  - Records `Aluno`, `Professor` e `Responsavel` (`ConcreteProduct`).
  - Classe abstrata `UsuarioFactory` (`Creator`).
  - Fábricas concretas `AlunoFactory`, `ProfessorFactory` e `ResponsavelFactory` (`ConcreteCreator`).
- **Onde está no código:** Pacote `br.com.escola.modulos.usuarios`.

---

### Slide 3: Padrão Estrutural - Facade
- **Apresentador:** Romao (Integrante 2)
- **Problema:** O controlador HTTP (`HttpServerApp`) dependia diretamente de múltiplos DAOs (`AlunoDAO`, `TurmaDAO`, `NotaDAO`, `PresencaDAO`) e múltiplos serviços (`BoletimService`, `MotorRegrasService`), gerando alto acoplamento e duplicação de lógica entre handlers.
- **Solução Aplicada:**
  - `SecretariaFacade` unifica e centraliza os casos de uso do sistema.
  - O `HttpServerApp` passa a ter dependência única na fachada.
  - Ponto unificado para disparo automático de eventos do sistema.
- **Onde está no código:** Pacote `br.com.escola.modulos.secretaria`.

---

### Slide 4: Padrão Comportamental - Observer (Núcleo)
- **Apresentador:** Isaac (Integrante 3)
- **Problema:** Ao registrar uma nota ou presença, ações complementares (como avisar familiares ou auditar histórico) precisavam ser chamadas sequencialmente no fluxo principal, acoplando responsabilidades distintas e propagando erros desnecessariamente.
- **Solução Aplicada:**
  - Interface selada `EventoEscolar` (`NotaLancada` e `PresencaRegistrada`).
  - Interface `Observador` (`Observer`).
  - `PublicadorEventos` (`Subject`) thread-safe com `CopyOnWriteArrayList` adequado para concorrência com Virtual Threads e isolamento de exceções.
- **Onde está no código:** Pacote `br.com.escola.modulos.eventos`.

---

### Slide 5: Padrão Comportamental - Observer (Implementações Concretas)
- **Apresentador:** Theks (Integrante 4)
- **Problema:** Falta de automatização para reações imediatas a eventos escolares: responsáveis desinformados e lentidão na identificação de alunos com risco de reprovação por falta.
- **Solução Aplicada:**
  - `ObservadorResponsavel`: Gera aviso imediato para os pais ou responsáveis.
  - `ObservadorHistorico`: Recalcula médias e situação acadêmica em tempo real.
  - `ObservadorInfrequencia`: Monitora o índice de faltas e aciona alerta preventivo caso a frequência caia abaixo de 80%.
- **Onde está no código:** Pacote `br.com.escola.modulos.eventos`.

---

### Slide 6: Testes Automatizados e Validação
- **Apresentador:** Gabriel (Integrante 5)
- **Cobertura:**
  - Testes do Factory Method garantindo polimorfismo correto por perfil.
  - Testes do Publicador e isolamento de exceções entre observadores.
  - Testes do Observador de Infrequência (abaixo e acima de 80%).
  - Testes da SecretariaFacade e integridade das regras da LDB (8 testes do motor preservados).
- **Onde está no código:** Pasta `src/test/java/br/com/escola/`.

---

### Slide 7: Diagramas Arquiteturais e Conclusão
- **Apresentador:** Pedro (Integrante 6)
- **Entregas:**
  - Diagrama de classes UML completo com os 3 padrões identificados.
  - Diagrama de sequência do caso de uso Lançar Nota (UC01).
  - Documentação completa no README.md e separação das branches no Git via Pull Request.
