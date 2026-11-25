# Sistema de Notas Escolar - ADSVA4

O **Sistema de Notas Escolar (ADSVA4)** é uma plataforma de gestão acadêmica e pedagógica desenvolvida com arquitetura híbrida — oferecendo tanto execução local de alta performance em **Java SE Puro** (utilizando Virtual Threads do Java 21) quanto operação em nuvem via **Serverless Functions na Vercel** integradas ao banco de dados relacional **Supabase (PostgreSQL)**. Projetado para integrar professores, estudantes e familiares em um ambiente único e seguro, o sistema implementa de forma rigorosa as exigências da legislação educacional brasileira (**LDB 9.394/1996, Art. 24, Inciso VI** — obrigatoriedade de 75% de frequência mínima para aprovação) e as normas dos Conselhos Estaduais de Educação (CEE), calculando médias anuais ponderadas, gerenciando períodos de recuperação e emitindo alertas preventivos de infrequência escolar.

> **Para rodar o projeto no seu computador**, siga o passo a passo em [Como Executar Localmente](#-como-executar-localmente). Só é preciso ter o Java 21 instalado: o sistema já vem com dados de demonstração e não precisa de banco de dados.

---

## 🎨 Interface Moderna Inspirada no Google Mail (Gmail UI)

O frontend foi redesenhado do zero adotando o padrão visual e ergonômico do **Gmail**:

- **Barra Superior com Busca Global:** Campo de pesquisa instantânea para filtrar alunos, matrículas ou matérias rapidamente (atalho `/`).
- **Sidebar Retrátil com Seções em Pílula:** 
  - Alternância rápida entre seções sem recarregar a página (com persistência via hash `#secao`).
  - **Botão Principal Contextual (*Compose Button*):** Salva as alterações da seção ativa com um único clique.
  - **Menu de Turmas e Dependentes:** Visualização do contexto atual de aula ou do filho matriculado.
- **Painel Central com Foco em Produtividade:**
  - Superfícies escuras de alto contraste com cantos arredondados, garantindo legibilidade máxima de números e notas.
  - O papel de parede fotográfico foi sutilmente desfocado e escurecido em segundo plano para não competir com as tabelas.
  - Remoção de badges repetitivos no topo, mantendo avisos legais apenas quando há risco real de infrequência.
- **Responsividade Total:** Suporte a gaveta lateral retrátil (*drawer*) e overlay escurecido em telas de celular e tablets.

---

## 🏛️ Arquitetura do Sistema

O projeto adota uma arquitetura em duas vias compatível tanto com infraestrutura local quanto com deploy em nuvem sem servidores dedicados:

```
                  ┌────────────────────────────────────────────────────────┐
                  │                 Frontend Web (Gmail UI)                │
                  │   HTML5 + CSS3 Escuro + Vanilla JS + Lucide Icons      │
                  └───────────┬────────────────────────────────┬───────────┘
                              │                                │
            (Requisições HTTP locais)             (Requisições Serverless / Nuvem)
                              │                                │
                              ▼                                ▼
            ┌───────────────────────────────────┐    ┌───────────────────────────────────┐
            │       Backend Monolítico Java     │    │   Vercel Serverless Functions     │
            │   Java 21 com Virtual Threads     │    │   Node.js ESM (pg connection pool)│
            │   com.sun.net.httpserver nativo   │    │   Roteamento via vercel.json      │
            └─────────────────┬─────────────────┘    └─────────────────┬─────────────────┘
                              │                                        │
                              └───────────────────┬────────────────────┘
                                                  │
                                                  ▼
                                   ┌─────────────────────────────┐
                                   │  PostgreSQL / Supabase      │
                                   │  Esquema UML Normalizado    │
                                   │  10 Tabelas + Índices       │
                                   └─────────────────────────────┘
```

### Principais Tecnologias
- **Frontend:** HTML5, CSS3 moderno, Vanilla JavaScript ES6+, Lucide Icons e SweetAlert2.
- **Backend Local:** Java 21+ com `com.sun.net.httpserver.HttpServer` e `Executors.newVirtualThreadPerTaskExecutor()`. Sem frameworks pesados (zero Spring/Hibernate).
- **Backend Nuvem:** Serverless Functions em Node.js (`api/`) com pool nativo `pg` conectando ao Supabase via SSL.
- **Banco de Dados:** PostgreSQL com esquema estritamente alinhado ao Diagrama de Classes UML:
  - Herança *Table-per-Subclass*: `usuarios` $\rightarrow$ `professores`, `responsaveis`, `alunos`.
  - Agregações e Associações: `cursos`, `disciplinas`, `historicos`, `notas`, `presencas` e `regras_escola`.
- **Motor de Regras:** 8 testes unitários cobrindo todos os cenários da LDB 9.394/96 e normas escolares.

---

## 🚀 Como Executar Localmente

O sistema roda sem banco de dados e sem instalar nada além do Java. Quando nenhum banco está configurado, o servidor usa automaticamente dados de demonstração em memória (turma 9A de Matemática, com 3 alunos, notas e presenças). Então os passos abaixo bastam para abrir e testar todas as telas.

### 1. Instalar o Java 21

O projeto usa recursos do **Java 21** (Virtual Threads e records). Versões anteriores, como o Java 8 ou o 17, não compilam o código.

1. Baixe o **JDK 21** em [adoptium.net](https://adoptium.net/temurin/releases/?version=21) (escolha o seu sistema e o instalador `.msi` no Windows ou `.pkg` no macOS).
2. Durante a instalação no Windows, marque as opções **"Set JAVA_HOME variable"** e **"Add to PATH"**.
3. Abra um terminal novo e confira:

```bash
java -version
```

A saída deve mostrar `21` (por exemplo, `openjdk version "21.0.5"`). Se mostrar outra versão, o Java 21 não está em primeiro lugar no PATH.

### 2. Baixar o projeto

Com Git:

```bash
git clone https://github.com/trinity-aitech/Sistema-de-Notas-Escolar---ADSVA4.git
cd Sistema-de-Notas-Escolar---ADSVA4
```

Sem Git: na página do repositório, clique em **Code > Download ZIP**, extraia o arquivo e abra um terminal dentro da pasta extraída.

### 3. Compilar e iniciar

#### Windows (PowerShell)

Na pasta do projeto:

```powershell
# Compila o Java e roda os 8 testes do motor de regras
powershell -ExecutionPolicy Bypass -File .\build.ps1

# Inicia o servidor na porta 8080 (deixe esta janela aberta)
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

O `-ExecutionPolicy Bypass` evita o erro *"a execução de scripts foi desabilitada neste sistema"*, que aparece no Windows quando os scripts nunca foram liberados. Ele vale só para esse comando e não altera nenhuma configuração do computador.

Na primeira vez, o `build.ps1` baixa o driver do PostgreSQL para a pasta `lib/` (precisa de internet). Ao final, ele deve mostrar `Resumo dos testes: 8 passaram, 0 falharam.`

#### macOS e Linux (Terminal)

Na pasta do projeto:

```bash
# Compila o Java
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src/main/java -name "*.java")

# Roda os 8 testes do motor de regras
javac -encoding UTF-8 -cp bin -d bin src/test/java/br/com/escola/modulos/regras/MotorRegrasTest.java
java -cp bin br.com.escola.modulos.regras.MotorRegrasTest

# Inicia o servidor na porta 8080 (deixe este terminal aberto)
java -cp bin br.com.escola.Main
```

#### IntelliJ IDEA ou Eclipse

Abra a pasta do projeto como projeto **Maven** (o `pom.xml` já está configurado para Java 21), confirme que o SDK do projeto é o Java 21 e execute a classe `br.com.escola.Main`.

### 4. Abrir no navegador

Com o servidor rodando, acesse **[http://localhost:8080](http://localhost:8080)**. Na tela de login, clique em um dos perfis (Professor, Aluno ou Responsável) e em **Acessar o portal**. As credenciais de cada perfil estão em [Perfis de Teste no Sistema](#-perfis-de-teste-no-sistema).

| Tela | Endereço |
|---|---|
| Login | [http://localhost:8080](http://localhost:8080) |
| Professor | [http://localhost:8080/professor.html](http://localhost:8080/professor.html) |
| Aluno | [http://localhost:8080/aluno.html](http://localhost:8080/aluno.html) |
| Responsável | [http://localhost:8080/responsavel.html](http://localhost:8080/responsavel.html) |

Entre sempre pela tela de login: as telas de aluno e responsável usam o usuário escolhido no login para saber qual boletim mostrar.

Para parar o servidor, volte ao terminal e pressione **Ctrl+C**.

### Problemas comuns

| Mensagem ou sintoma | Causa e solução |
|---|---|
| `invalid target release: 21`, `release version 21 not supported` ou erros com `record` | O terminal está usando um Java antigo. Instale o Java 21 (passo 1) e abra um terminal novo. |
| `'java' não é reconhecido como um comando` | O Java não está no PATH. Reinstale marcando "Add to PATH" ou abra um terminal novo depois da instalação. |
| `a execução de scripts foi desabilitada neste sistema` | Rode os scripts com `powershell -ExecutionPolicy Bypass -File`, como no passo 3. |
| `Address already in use` ou porta 8080 ocupada | Outro programa usa a porta. Inicie em outra porta definindo `SERVER_PORT`, por exemplo no PowerShell: `$env:SERVER_PORT="8081"; powershell -ExecutionPolicy Bypass -File .\run.ps1`, e acesse `http://localhost:8081`. |
| Tela de aluno ou responsável parada em "Carregando" | A página foi aberta direto pelo endereço. Entre pela tela de login. |

### Opcional: usar um banco PostgreSQL de verdade

Os dados de demonstração ficam só na memória e são perdidos quando o servidor para. Para gravar em um banco real com Docker:

```bash
docker compose up -d
```

O `docker-compose.yml` sobe um PostgreSQL 16 na porta 5432 já com as tabelas (`sql/01_schema.sql`) e os dados de exemplo (`sql/02_seed.sql`). As credenciais padrão do servidor Java (`DB_URL`, `DB_USER` e `DB_PASSWORD`, listadas em `.env.example`) já apontam para esse banco. No Windows, basta iniciar o servidor com o `run.ps1`, que já inclui o driver do PostgreSQL. No macOS e no Linux, baixe o driver uma vez e inclua-o no comando:

```bash
mkdir -p lib
curl -L -o lib/postgresql-42.7.3.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar
java -cp "bin:lib/postgresql-42.7.3.jar" br.com.escola.Main
```

Para usar outro banco, defina essas variáveis de ambiente antes de iniciar o servidor.

---

## ⚡ Conexão com Supabase e Deploy na Vercel

### 1. Banco de Dados no Supabase
1. No [Supabase](https://supabase.com), abra o **SQL Editor**.
2. Execute o arquivo [`sql/01_schema.sql`](sql/01_schema.sql) para criar as 10 tabelas e índices.
3. Execute o arquivo [`sql/02_seed.sql`](sql/02_seed.sql) para popular os dados de demonstração.

### 2. Deploy Automático na Vercel
1. Importe o repositório no [Vercel Dashboard](https://vercel.com).
2. Em **Settings ➔ Environments** (ou Environment Variables), adicione:
   - **Key:** `DATABASE_URL`
   - **Value:** `postgresql://postgres:SUA_SENHA@db.SEU_PROJETO.supabase.co:5432/postgres`
3. Salve e execute um **Redeploy**.
4. Teste o status da sua API acessando `https://seu-projeto.vercel.app/api`. O endpoint retornará:
   ```json
   {
     "status": "online",
     "database": "conectado",
     "totalUsuarios": 6,
     "mensagem": "API Serverless do Sistema de Notas Escolar ativa"
   }
   ```

---

## 🔑 Perfis de Teste no Sistema

| Perfil | E-mail de Teste | Senha | Seções na Sidebar |
|---|---|---|---|
| **Professor** | `carlos.silva@escola.com` | `123456` | **Notas** (lançamento e cálculo de médias) e **Chamada** (diário de frequência e justificativas) |
| **Aluno** | `lucas.mendes@escola.com` | `123456` | **Boletim** (notas e médias anuais), **Frequência** (horas/faltas acumuladas) e **Critérios** (regras LDB) |
| **Responsável** | `maria.mendes@email.com` | `123456` | **Boletim** (desempenho do dependente), **Frequência** (acompanhamento preventivo) e **Contato** |

---

## 📁 Estrutura de Diretórios

```
Sistema-de-Notas-Escolar---ADSVA4/
├── build.ps1                       # Compilador e executor de testes em Java puro
├── run.ps1                         # Inicializador do servidor web Java
├── vercel.json                     # Roteamento de rewrites e headers CORS da Vercel
├── package.json                    # Dependências Serverless (pg) e tipo module
├── .env.example                    # Exemplo de variáveis de ambiente
├── sql/
│   ├── 01_schema.sql               # Esquema relacional das 9 classes UML + regras
│   └── 02_seed.sql                 # Carga inicial de turmas, alunos, notas e faltas
├── lib/
│   └── postgresql-42.7.3.jar       # Driver JDBC oficial PostgreSQL
├── api/                            # Camada Serverless Vercel
│   ├── index.js                    # Handler principal com conexão pg.Pool e endpoints REST
│   ├── turmas.js                   # Rota direta /api/turmas
│   ├── notas.js                    # Rota direta /api/notas
│   ├── presencas.js                # Rota direta /api/presencas
│   ├── boletim.js                  # Rota direta /api/boletim
│   ├── avaliacoes.js               # Rota direta /api/avaliacoes
│   ├── aulas.js                    # Rota direta /api/aulas
│   ├── regras.js                   # Rota direta /api/regras
│   ├── turmas/alunos.js            # Rota direta /api/turmas/alunos
│   └── auth/login.js               # Rota direta /api/auth/login
├── public/                         # Frontend Web Estático (Interface Gmail UI)
│   ├── index.html                  # Tela de Acesso e seleção rápida de perfis
│   ├── professor.html              # Painel do Professor com sidebar (Notas / Chamada)
│   ├── aluno.html                  # Portal do Aluno com sidebar (Boletim / Frequência / Critérios)
│   ├── responsavel.html            # Portal dos Pais com sidebar (Boletim / Frequência / Contato)
│   ├── assets/
│   │   └── bg.jpg                  # Papel de parede fotográfico de alta resolução
│   ├── css/
│   │   └── styles.css              # Tema escuro, barra superior, sidebar em pílula e tabelas
│   └── js/
│       ├── api.js                  # Cliente unificado de consumo REST
│       ├── layout.js               # Gerenciador global de navegação da sidebar, busca e avatar
│       ├── professor.js            # Lógica do diário de classe e lançamento de notas
│       ├── aluno.js                # Lógica de renderização do boletim acadêmico
│       └── responsavel.js          # Lógica de acompanhamento dos dependentes
└── src/
    ├── main/java/br/com/escola/
    │   ├── Main.java               # Inicializador do HttpServer com Virtual Threads
    │   ├── config/DatabaseConfig.java # Conexão JDBC compatível com DATABASE_URL e Supabase
    │   ├── http/                   # Handlers REST e servidor HTTP estático
    │   └── modulos/
    │       ├── alunos/             # Modelos de Alunos e Turmas
    │       ├── notas/              # Modelos de Avaliações e Notas
    │       ├── presenca/           # Modelos de Faltas e Diário
    │       └── regras/             # Motor LDB (Art. 24) e Regulamento Escolar
    └── test/java/br/com/escola/
        └── modulos/regras/
            └── MotorRegrasTest.java # 8 testes unitários do motor de regras
```

---

## 📜 Licença e Conformidade

Este projeto é disponibilizado sob a licença **MIT** e foi desenvolvido para a disciplina **ADSVA4**, em conformidade com as diretrizes do **MEC/LDB 9.394/96** e resoluções complementares do Conselho Estadual de Educação (CEE).
