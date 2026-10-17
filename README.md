# Sistema de Notas Escolar - ADSVA4

O **Sistema de Notas Escolar (ADSVA4)** é uma plataforma de gestão acadêmica e pedagógica desenvolvida com arquitetura híbrida — oferecendo tanto execução local de alta performance em **Java SE Puro** (utilizando Virtual Threads do Java 21) quanto operação em nuvem via **Serverless Functions na Vercel** integradas ao banco de dados relacional **Supabase (PostgreSQL)**. Projetado para integrar professores, estudantes e familiares em um ambiente único e seguro, o sistema implementa de forma rigorosa as exigências da legislação educacional brasileira (**LDB 9.394/1996, Art. 24, Inciso VI** — obrigatoriedade de 75% de frequência mínima para aprovação) e as normas dos Conselhos Estaduais de Educação (CEE), calculando médias anuais ponderadas, gerenciando períodos de recuperação e emitindo alertas preventivos de infrequência escolar.

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

### 1. Pré-requisitos
- **Java 21 ou superior** instalado e configurado no PATH.

### 2. Compilar e Rodar com Scripts PowerShell
No terminal PowerShell, na raiz do projeto:

```powershell
# 1. Compila o Java e roda os 8 testes unitários do motor de regras
.\build.ps1

# 2. Inicia o servidor web na porta 8080
.\run.ps1
```

Acesse no navegador:
- 🌐 **Portal Inicial / Login:** [http://localhost:8080](http://localhost:8080)
- 👨‍🏫 **Painel do Professor:** [http://localhost:8080/professor.html](http://localhost:8080/professor.html)
- 🎓 **Portal do Aluno:** [http://localhost:8080/aluno.html](http://localhost:8080/aluno.html)
- 👨‍👩‍👧 **Portal dos Responsáveis:** [http://localhost:8080/responsavel.html](http://localhost:8080/responsavel.html)

*(Observação: Se nenhuma connection string externa for configurada, o servidor conecta automaticamente ao repositório simulado em memória para demonstração imediata).*

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
