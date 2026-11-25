# Sistema de Notas Escolar (ADSVA4)

Sistema web para uma escola lançar notas e frequência e para alunos e responsáveis acompanharem o boletim. Foi feito para a disciplina ADSVA4.

O professor lança as notas dos quatro bimestres e faz a chamada. O sistema calcula a média e a situação final de cada aluno seguindo duas regras:

- **LDB 9.394/96, Art. 24, VI:** o aluno precisa de pelo menos 75% de frequência para ser aprovado, independente da nota.
- **Regimento da escola:** média 7,0 aprova direto, média entre 4,0 e 6,9 vai para recuperação, e depois da recuperação a média final precisa ser pelo menos 5,0.

Quando a frequência fica abaixo de 80%, o boletim mostra um aviso antes de o aluno chegar ao limite da LDB.

**Para rodar no seu computador, veja [Como executar](#como-executar).** Só precisa do Java 21. O sistema já vem com dados de exemplo e funciona sem banco de dados.

## Como executar

### 1. Instale o Java 21

O código usa recursos do Java 21 (records e Virtual Threads), então o Java 8 ou o 17 não compilam o projeto.

1. Baixe o JDK 21 em [adoptium.net](https://adoptium.net/temurin/releases/?version=21). No Windows use o instalador `.msi`, no macOS o `.pkg`.
2. No Windows, marque as opções "Set JAVA_HOME variable" e "Add to PATH" durante a instalação.
3. Abra um terminal novo e confira com `java -version`. A versão mostrada tem que começar com 21.

### 2. Baixe o projeto

```bash
git clone https://github.com/trinity-aitech/Sistema-de-Notas-Escolar---ADSVA4.git
cd Sistema-de-Notas-Escolar---ADSVA4
```

Também dá para baixar pelo botão Code > Download ZIP aqui no GitHub e extrair a pasta.

### 3. Compile e inicie o servidor

**Windows (PowerShell), dentro da pasta do projeto:**

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

O primeiro comando compila o projeto e roda os 8 testes do motor de regras. No fim ele deve mostrar `Resumo dos testes: 8 passaram, 0 falharam.` Na primeira vez ele também baixa o driver do PostgreSQL para a pasta `lib/`, então precisa de internet.

O segundo comando sobe o servidor na porta 8080. Deixe a janela aberta enquanto estiver usando o sistema.

O `-ExecutionPolicy Bypass` é porque o Windows bloqueia scripts `.ps1` por padrão. Ele libera só esse comando e não muda nenhuma configuração do computador.

**macOS e Linux, dentro da pasta do projeto:**

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src/main/java -name "*.java")

# testes do motor de regras
javac -encoding UTF-8 -cp bin -d bin src/test/java/br/com/escola/modulos/regras/MotorRegrasTest.java
java -cp bin br.com.escola.modulos.regras.MotorRegrasTest

# servidor na porta 8080
java -cp bin br.com.escola.Main
```

**IntelliJ ou Eclipse:** abra a pasta como projeto Maven, escolha o Java 21 como SDK e rode a classe `br.com.escola.Main`.

### 4. Abra no navegador

Acesse [http://localhost:8080](http://localhost:8080), escolha um perfil na tela de login e clique em "Acessar o portal". Entre sempre pela tela de login, porque as telas de aluno e responsável usam o usuário escolhido ali para saber qual boletim mostrar.

| Perfil | E-mail | Senha | O que dá para fazer |
|---|---|---|---|
| Professor | `carlos.silva@escola.com` | `123456` | Lançar notas e fazer a chamada da turma 9A |
| Aluno | `lucas.mendes@escola.com` | `123456` | Ver boletim, frequência e critérios de aprovação |
| Responsável | `maria.mendes@email.com` | `123456` | Acompanhar o boletim e a frequência do filho |

Os dados de exemplo são da turma 9A de Matemática, com três alunos: o Lucas está aprovado, a Beatriz está em recuperação e o Gabriel está reprovado por falta. Para parar o servidor, aperte Ctrl+C no terminal.

### Se der erro

| Erro | O que fazer |
|---|---|
| `invalid target release: 21` ou erros envolvendo `record` | O terminal está usando um Java mais antigo. Instale o Java 21 e abra um terminal novo. |
| `'java' não é reconhecido como um comando` | O Java não está no PATH. Reinstale marcando "Add to PATH" ou abra um terminal novo. |
| `a execução de scripts foi desabilitada neste sistema` | Rode os scripts com `powershell -ExecutionPolicy Bypass -File`, como no passo 3. |
| `Address already in use` | A porta 8080 está ocupada. No PowerShell, rode `$env:SERVER_PORT="8081"` antes do `run.ps1` e acesse `http://localhost:8081`. |
| Tela de aluno ou responsável parada em "Carregando" | A página foi aberta direto pelo endereço. Entre pela tela de login. |

### Usando um banco PostgreSQL (opcional)

Sem banco, as notas lançadas ficam só na memória e somem quando o servidor para. Para gravar de verdade, dá para subir um PostgreSQL com Docker:

```bash
docker compose up -d
```

Isso cria o banco `escola_db` na porta 5432 já com as tabelas (`sql/01_schema.sql`) e os dados de exemplo (`sql/02_seed.sql`). O servidor Java já vem configurado para esse banco (veja `.env.example`), então no Windows é só rodar o `run.ps1` de novo. No macOS e no Linux é preciso baixar o driver e colocar no comando:

```bash
mkdir -p lib
curl -L -o lib/postgresql-42.7.3.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar
java -cp "bin:lib/postgresql-42.7.3.jar" br.com.escola.Main
```

Para usar outro banco, defina as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD` antes de iniciar o servidor.

## Como o projeto está organizado

O back-end é um monólito em Java 21 sem framework. O servidor HTTP é o `com.sun.net.httpserver` que já vem no Java, com uma Virtual Thread por requisição. O acesso ao banco é feito com JDBC puro, em classes DAO. O front-end é HTML, CSS e JavaScript sem framework, servido pelo próprio servidor Java.

Também existe uma versão da API em Node.js na pasta `api/`, usada quando o projeto é publicado na Vercel.

```
Navegador (HTML + CSS + JavaScript)
        |
        |  requisições /api/...
        v
Servidor Java 21 (local)   ou   Funções Node.js (Vercel)
        |
        v
PostgreSQL (local, Docker ou Supabase)
```

O banco tem 10 tabelas: `usuarios`, `professores`, `alunos`, `responsaveis`, `cursos`, `disciplinas`, `historicos`, `notas`, `presencas` e `regras_escola`. Professor, aluno e responsável ficam cada um na sua tabela, ligados a `usuarios`.

```
├── build.ps1, run.ps1          scripts para compilar e rodar no Windows
├── docker-compose.yml          PostgreSQL local com as tabelas e dados de exemplo
├── sql/
│   ├── 01_schema.sql           criação das tabelas
│   └── 02_seed.sql             dados de exemplo
├── api/                        versão da API em Node.js para a Vercel
├── public/                     front-end
│   ├── index.html              login
│   ├── professor.html          notas e chamada
│   ├── aluno.html              boletim, frequência e critérios
│   ├── responsavel.html        acompanhamento do filho
│   ├── css/styles.css
│   └── js/                     api.js (chamadas à API), layout.js e um arquivo por tela
└── src/
    ├── main/java/br/com/escola/
    │   ├── Main.java           inicia o servidor
    │   ├── config/             conexão com o banco
    │   ├── http/               rotas da API e arquivos estáticos
    │   └── modulos/
    │       ├── alunos/         alunos, turmas e matrículas
    │       ├── notas/          avaliações e notas
    │       ├── presenca/       aulas e presenças
    │       ├── eventos/        padrão Observer (eventos e publicador)
    │       └── regras/         motor de regras (LDB e regimento) e boletim
    └── test/java/br/com/escola/
        └── modulos/regras/MotorRegrasTest.java
```

A API tem as rotas `/api/auth/login`, `/api/turmas`, `/api/turmas/alunos`, `/api/avaliacoes`, `/api/notas`, `/api/aulas`, `/api/presencas`, `/api/boletim` e `/api/regras`.

## Publicando na Vercel com Supabase

1. No [Supabase](https://supabase.com), abra o SQL Editor e rode `sql/01_schema.sql` e depois `sql/02_seed.sql`.
2. Importe o repositório na [Vercel](https://vercel.com).
3. Em Settings > Environment Variables, crie a variável `DATABASE_URL` com a connection string do Supabase, no formato `postgresql://postgres:SUA_SENHA@db.SEU_PROJETO.supabase.co:5432/postgres`.
4. Faça um Redeploy. Para conferir se a API conectou no banco, acesse `https://seu-projeto.vercel.app/api`. A resposta deve ter `"database": "conectado"`.

## Integrantes

Guilherme, Romão, Isaac, Thiago, Gabriel e Pedro. A divisão de tarefas está em [`public/diagramas/DIVISAO_DE_TAREFAS.md`](public/diagramas/DIVISAO_DE_TAREFAS.md).
