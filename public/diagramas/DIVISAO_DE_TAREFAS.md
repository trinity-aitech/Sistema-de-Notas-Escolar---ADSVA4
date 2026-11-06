# Divisão de Tarefas: Padrões GoF no Sistema de Notas Escolar (ADSVA4)

Grupo de 6 integrantes. Objetivo: aplicar **3 padrões GoF** no sistema que já existe (Java 21, Maven, monólito com DAOs), sem quebrar o comportamento atual.

| Categoria | Padrão | Onde entra no sistema |
|---|---|---|
| Criacional | **Factory Method** | Criação dos usuários (Aluno, Professor, Responsável) e login |
| Estrutural | **Facade** | `SecretariaFacade` como ponto de entrada único para o `HttpServerApp` |
| Comportamental | **Observer** | Avisos automáticos ao lançar nota ou registrar presença |

---

## 1. Resumo de quem faz o quê

| # | Integrante | Responsabilidade | Padrão |
|---|---|---|---|
| 1 | _(Guilherm)_ | Usuario, Responsavel e fábricas, refatorar login | Factory Method |
| 2 | _(Romao)_ | `SecretariaFacade` e limpeza do `HttpServerApp` | Facade |
| 3 | _(Isaac)_ | Núcleo do Observer: eventos, interface e publicador | Observer |
| 4 | _(Theks)_ | Observers concretos (responsável, histórico, infrequência) | Observer |
| 5 | _(Gabriel)_ | Testes JUnit, teste manual e integração final | Todos |
| 6 | _(Pedro)_ | Diagramas UML, README e apresentação | Todos |

---

## 2. Combinados do grupo (ler antes de começar)

Estes nomes são o "contrato" entre as pessoas. Ninguém muda sem avisar o grupo.

**Pacotes novos** (base `br.com.escola`):
- `modulos.usuarios` (Usuario, Responsavel, fábricas)
- `modulos.secretaria` (SecretariaFacade)
- `modulos.eventos` (EventoEscolar, Observador, PublicadorEventos e os observers)

**Nomes das classes:**
- Factory: `Usuario`, `Responsavel`, `UsuarioFactory`, `AlunoFactory`, `ProfessorFactory`, `ResponsavelFactory`
- Facade: `SecretariaFacade`
- Observer: `EventoEscolar` (com `NotaLancada` e `PresencaRegistrada`), `Observador`, `PublicadorEventos`, `ObservadorResponsavel`, `ObservadorHistorico`, `ObservadorInfrequencia`

**Git:**
- Uma branch por pessoa: `feature/factory`, `feature/facade`, `feature/observer-core`, `feature/observer-concretos`, `feature/testes`, `docs/diagramas`.
- Commits pequenos e com mensagem clara (ex: `feat(usuarios): cria UsuarioFactory`).
- Nada direto na `main`. Tudo entra por Pull Request, revisado por pelo menos 1 colega.
- Antes de abrir o PR: `mvn test` precisa passar.

**Regras de código:**
- Cada classe nova leva um comentário dizendo qual papel do padrão ela cumpre (Creator, ConcreteCreator, Facade, Subject, Observer...).
- Não alterar o formato do JSON das respostas da API.

---

## 3. Tarefas de cada integrante

### Integrante 1: Factory Method

**Entrega:** pacote `modulos.usuarios` e login refatorado.

**Tarefas:**
- [ ] Criar a interface `Usuario` (`id`, `nome`, `email`, `perfil`).
- [ ] Fazer os records `Aluno` e `Professor` implementarem `Usuario`, sem mudar os campos atuais.
- [ ] Criar o record `Responsavel` (`id`, `usuarioId`, `nome`, `email`, `telefone`, `parentesco`) implementando `Usuario`.
- [ ] Criar a classe abstrata `UsuarioFactory` com o método `criarUsuario(Map<String,String> dados)` e um método estático que devolve a fábrica pelo perfil (PROFESSOR, ALUNO, RESPONSAVEL).
- [ ] Criar `AlunoFactory`, `ProfessorFactory` e `ResponsavelFactory`.
- [ ] Trocar o `if/else` por e-mail do `handleLogin` pelo uso da fábrica (combinar com o Integrante 2, que mexe no mesmo arquivo).

**Depende de:** ninguém. Pode começar já.
**Quem usa o resultado:** Integrante 2 (login na fachada), Integrante 5 (testes), Integrante 6 (diagrama).

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Aplicar o padrão FACTORY METHOD no pacote br.com.escola.modulos.usuarios:
- Criar interface Usuario (id, nome, email, perfil).
- Fazer os records Aluno e Professor (de modulos.alunos) implementarem Usuario sem mudar seus campos.
- Criar record Responsavel (id, usuarioId, nome, email, telefone, parentesco) implementando Usuario.
- Criar classe abstrata UsuarioFactory com criarUsuario(Map<String,String> dados) e método estático para obter a fábrica pelo perfil (PROFESSOR, ALUNO, RESPONSAVEL).
- Criar AlunoFactory, ProfessorFactory e ResponsavelFactory.
- Em HttpServerApp.handleLogin, remover o if/else por e-mail e usar a fábrica, mantendo exatamente o mesmo JSON de resposta.
Comentar cada classe com o papel no padrão. Sem emojis nem travessões. Rodar mvn test ao final e listar os arquivos criados e alterados.
```

</details>

---

### Integrante 2: Facade

**Entrega:** `SecretariaFacade` e `HttpServerApp` enxuto.

**Tarefas:**
- [ ] Criar `SecretariaFacade` no pacote `modulos.secretaria`.
- [ ] Implementar os métodos: `login`, `listarTurmas`, `listarAlunosDaTurma`, `listarAvaliacoes`, `listarNotas`, `lancarNota`, `listarAulas`, `listarPresencas`, `registrarPresenca`, `gerarBoletim`, `obterRegras`.
- [ ] A fachada instancia e esconde `AlunoDAO`, `TurmaDAO`, `NotaDAO`, `PresencaDAO`, `BoletimService` e `MotorRegrasService`.
- [ ] Refatorar o `HttpServerApp`: ele passa a ter só a `SecretariaFacade` como dependência. Os handlers apenas leem a requisição, chamam a fachada e respondem JSON.
- [ ] Deixar um ponto claro em `lancarNota` e `registrarPresenca` para chamar `notificar()` depois de salvar (o Integrante 3 entrega o publicador).

**Depende de:** Integrante 1 (para o login) e Integrante 3 (para o `notificar`). Pode começar já com o resto e plugar os dois depois.
**Cuidado:** combinar com o Integrante 1 para não gerar conflito no `HttpServerApp`.

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Aplicar o padrão FACADE:
- Criar br.com.escola.modulos.secretaria.SecretariaFacade com: login, listarTurmas, listarAlunosDaTurma, listarAvaliacoes, listarNotas, lancarNota, listarAulas, listarPresencas, registrarPresenca, gerarBoletim, obterRegras.
- A fachada instancia e esconde AlunoDAO, TurmaDAO, NotaDAO, PresencaDAO, BoletimService e MotorRegrasService.
- Refatorar HttpServerApp para depender apenas da SecretariaFacade; os handlers só leem a requisição, chamam a fachada e respondem JSON, sem mudar o formato das respostas.
- Em lancarNota e registrarPresenca, deixar um comentário TODO indicando onde será chamado o notificar() do Observer.
Comentar a classe com o papel Facade. Sem emojis nem travessões. Rodar mvn test e listar os arquivos criados e alterados.
```

</details>

---

### Integrante 3: Observer (núcleo)

**Entrega:** pacote `modulos.eventos` com a estrutura base do padrão.

**Tarefas:**
- [ ] Criar `EventoEscolar` (sealed interface ou record) com `NotaLancada(matriculaId, avaliacaoId, valor)` e `PresencaRegistrada(matriculaId, aulaId, presente)`.
- [ ] Criar a interface `Observador` com `void atualizar(EventoEscolar e)`.
- [ ] Criar `PublicadorEventos` (o Subject) com `adicionarObservador`, `removerObservador` e `notificar`.
- [ ] Tornar a lista de observers segura para uso com Virtual Threads (o servidor usa uma thread por requisição).
- [ ] Integrar com a `SecretariaFacade`: chamar `notificar()` somente quando `salvarNota` ou `salvarPresenca` retornar `true`, e registrar os observers no construtor da fachada (combinar com os Integrantes 2 e 4).

**Depende de:** ninguém para a estrutura base. A integração depende do Integrante 2.
**Quem usa o resultado:** Integrante 4 (implementa a interface), Integrante 5 e 6.

> Entregar a interface `Observador` e os eventos **primeiro** (nos primeiros commits), porque o Integrante 4 depende deles.

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Aplicar o núcleo do padrão OBSERVER em br.com.escola.modulos.eventos:
- sealed interface EventoEscolar com records NotaLancada(matriculaId, avaliacaoId, valor) e PresencaRegistrada(matriculaId, aulaId, presente).
- interface Observador { void atualizar(EventoEscolar e); }
- classe PublicadorEventos (Subject) com adicionarObservador, removerObservador e notificar, thread-safe (o servidor usa Virtual Threads), e um observer com erro não pode impedir os demais de serem notificados.
Comentar cada classe com o papel no padrão (Subject, Observer, Event). Sem emojis nem travessões. Rodar mvn test e listar os arquivos criados.
```

</details>

---

### Integrante 4: Observer (observers concretos)

**Entrega:** as três reações do sistema aos eventos.

**Tarefas:**
- [ ] `ObservadorResponsavel`: ao receber um evento, gera um aviso para o responsável do aluno (pode ser log mais uma lista em memória).
- [ ] `ObservadorHistorico`: ao receber um evento, recalcula a situação do aluno usando o `MotorRegrasService`.
- [ ] `ObservadorInfrequencia`: ao receber `PresencaRegistrada`, verifica a frequência e alerta quando ficar abaixo de 80% (essa regra hoje está solta no `BoletimService`, então reaproveitar a lógica, sem duplicar).
- [ ] Cada observer trata só os eventos que lhe interessam.

**Depende de:** Integrante 3 (interface `Observador` e eventos). Enquanto não sair, combinar a assinatura e começar pela lógica interna.
**Quem usa o resultado:** Integrante 3 e 2 (registro na fachada), Integrante 5 (testes).

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Já existem Observador, EventoEscolar (NotaLancada, PresencaRegistrada) e PublicadorEventos em modulos.eventos. Criar os observers concretos no mesmo pacote:
- ObservadorResponsavel: gera um aviso para o responsável do aluno (log e lista em memória).
- ObservadorHistorico: recalcula a situação do aluno via MotorRegrasService.
- ObservadorInfrequencia: em PresencaRegistrada, alerta quando a frequência < 80%, reaproveitando a lógica que hoje está em BoletimService, sem duplicar.
Cada observer ignora os eventos que não lhe interessam. Comentar com o papel ConcreteObserver. Sem emojis nem travessões. Rodar mvn test e listar os arquivos criados.
```

</details>

---

### Integrante 5: Testes e integração final

**Entrega:** testes JUnit passando, sistema rodando e validado de ponta a ponta.

**Tarefas:**
- [ ] Teste da fábrica: `UsuarioFactory` retorna o tipo certo para cada perfil.
- [ ] Teste do `PublicadorEventos`: notifica todos os observers cadastrados e continua se um deles falhar.
- [ ] Teste dos observers concretos (principalmente o de infrequência, com casos acima e abaixo de 80%).
- [ ] Teste da fachada: `lancarNota` e `registrarPresenca` disparam notificação só quando salvam com sucesso.
- [ ] Garantir que o `MotorRegrasTest` existente continua passando.
- [ ] Teste manual: subir o servidor, abrir `professor.html`, `aluno.html` e `responsavel.html`, lançar uma nota, registrar presença, ver o boletim.
- [ ] Revisar os Pull Requests dos colegas e fazer o merge final na `main`, resolvendo conflitos.

**Depende de:** todos (vai testando conforme cada PR chega).

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Os padrões Factory Method (modulos.usuarios), Facade (modulos.secretaria) e Observer (modulos.eventos) já foram implementados. Criar testes JUnit em src/test/java:
- UsuarioFactory retorna o tipo certo para cada perfil.
- PublicadorEventos notifica todos os observers e continua se um deles lançar exceção.
- ObservadorInfrequencia alerta com frequência abaixo de 80% e não alerta acima.
- SecretariaFacade só notifica quando salvarNota/salvarPresenca retorna true (usar dublês/fakes dos DAOs se necessário).
Garantir que MotorRegrasTest continua passando. Rodar mvn test, mostrar o resultado e listar os arquivos criados.
```

</details>

---

### Integrante 6: Diagramas, README e apresentação

**Entrega:** documentação alinhada com o código final.

**Tarefas:**
- [ ] Atualizar o **diagrama de classes** com as classes novas: `Usuario`, `Responsavel`, `UsuarioFactory` e as 3 fábricas, `SecretariaFacade`, `Observador`, `PublicadorEventos` e os 3 observers. Destacar visualmente cada padrão (cor ou legenda).
- [ ] Atualizar o **diagrama de sequência UC01 (Lançar Nota)**: incluir a `SecretariaFacade` como participante e o `notificar()` chamando os observers.
- [ ] Atualizar o diagrama de containers/arquitetura se necessário.
- [ ] Escrever no README uma seção "Padrões de Projeto aplicados": qual problema cada um resolveu, onde está no código e por que foi escolhido.
- [ ] Montar a apresentação (um slide por padrão: problema, solução, trecho do código, diagrama).
- [ ] Combinar com o grupo quem apresenta cada parte.

**Depende de:** nomes combinados na seção 2. Pode começar os diagramas já e ajustar no final conforme o código real.
**Atenção:** o diagrama deve refletir o que foi **de fato implementado**, e não o que foi planejado.

<details>
<summary>Prompt para o Claude Code</summary>

```
Projeto Java 21 (Maven), pacote br.com.escola. Os padrões Factory Method (modulos.usuarios), Facade (modulos.secretaria) e Observer (modulos.eventos) foram implementados. Ler o código real e gerar em Mermaid:
1) Diagrama de classes com Usuario, Aluno, Professor, Responsavel, UsuarioFactory e as 3 fábricas, SecretariaFacade, Observador, PublicadorEventos e os 3 observers, indicando o padrão de cada grupo.
2) Diagrama de sequência do caso de uso Lançar Nota passando por HttpServerApp, SecretariaFacade, NotaDAO, PublicadorEventos e os observers.
Depois, escrever no README.md uma seção "Padrões de Projeto aplicados" com: problema resolvido, onde está no código e motivo da escolha de cada padrão. Os diagramas devem refletir exatamente o código existente. Sem emojis nem travessões.
```

</details>

---

## 4. Ordem sugerida de trabalho

```
Fase 1 (em paralelo):   Integrante 1 (Factory)   Integrante 3 (Observer núcleo)   Integrante 2 (Facade, parte base)
                        Integrante 6 (rascunho dos diagramas)

Fase 2:                 Integrante 4 (observers concretos, depois que o 3 entregar a interface)
                        Integrante 2 pluga o Factory (login) e o notificar() na fachada

Fase 3:                 Integrante 5 (testes + teste manual + merge final)
                        Integrante 6 (diagramas finais + README + apresentação)

Fase 4:                 Ensaio da apresentação com o grupo inteiro
```

---

## 5. Checklist de entrega

- [ ] Os 3 padrões implementados e identificáveis no código (comentários com o papel de cada classe).
- [ ] `mvn test` passando.
- [ ] Sistema sobe e os painéis de professor, aluno e responsável funcionam.
- [ ] Diagrama de classes atualizado.
- [ ] Diagrama de sequência UC01 atualizado.
- [ ] README com a seção de padrões de projeto.
- [ ] Apresentação pronta, com cada integrante sabendo explicar a sua parte.

---

## 6. Dica para a apresentação

Todo integrante deve conseguir responder, para a sua parte: **qual problema existia antes, o que o padrão resolveu e onde está no código**. Quem fez o Observer, por exemplo, mostra que antes `salvarNota` só gravava e respondia "sucesso", e agora o responsável e o histórico reagem sozinhos.
