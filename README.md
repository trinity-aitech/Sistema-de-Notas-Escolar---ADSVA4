# Sistema Web de Notas Escolar (Monolítico em Java Puro)

Sistema completo de gestão acadêmica escolar, diário de classe, cálculo de médias e validação da legislação educacional brasileira (**LDB 9.394/1996 Art. 24 - frequência mínima de 75%** e normas do CEE), com backend puramente em Java, banco de dados relacional PostgreSQL / Supabase e pronto para deploy na Vercel.

---

## 🏛️ Arquitetura do Sistema

Conforme o diagrama de containers:

```
[Professor]       --> (HTTPS) --+
[Aluno]           --> (HTTPS) --+--> [ Aplicação Java Monolítica ] <--(JDBC)--> [ Banco de Dados ]
[Pais / Resp.]    --> (HTTPS) --+     ├── Módulo de Alunos e Turmas              (PostgreSQL / Supabase)
                                      ├── Módulo de Notas
                                      ├── Módulo de Presença
                                      └── Módulo de Regras (LDB / CEE / Escola)
```

### Características Técnicas
- **Backend**: **Java SE Puro** (Java 21+) utilizando `com.sun.net.httpserver.HttpServer` com **Virtual Threads** (`Executors.newVirtualThreadPerTaskExecutor()`). Zero frameworks pesados (sem Spring, sem Hibernate).
- **Banco de Dados**: **PostgreSQL** ou **Supabase** acessado via **JDBC nativo** (`org.postgresql.Driver`).
- **Frontend**: Web responsivo em HTML5, CSS3 moderno e Vanilla JavaScript (`public/`), com suporte nativo de deploy na **Vercel**.
- **Motor de Regras**:
  - **LDB Art. 24, Inciso VI**: Frequência mínima obrigatória de **75%** do total de horas letivas para aprovação de série/ano.
  - **Regulamento Escolar**: Média anual de corte direta ($\ge 7.0$), faixa de recuperação ($4.0 \le \text{média} < 7.0$) e média final pós-exame ($\ge 5.0$).

---

## 🚀 Como Executar Localmente

### 1. Pré-requisitos
- **Java 21+** instalado.

### 2. Compilar e Rodar com Scripts Prontos
No terminal PowerShell:

```powershell
# 1. Compila o Java e executa a bateria de testes unitários do motor de regras
.\build.ps1

# 2. Inicia o servidor web na porta 8080
.\run.ps1
```

Acesse no navegador:
- 🌐 **Portal Inicial / Login:** [http://localhost:8080](http://localhost:8080)
- 👨‍🏫 **Painel do Professor:** [http://localhost:8080/professor.html](http://localhost:8080/professor.html)
- 🎓 **Portal do Aluno:** [http://localhost:8080/aluno.html](http://localhost:8080/aluno.html)
- 👨‍👩‍👧 **Portal da Família / Responsáveis:** [http://localhost:8080/responsavel.html](http://localhost:8080/responsavel.html)

*(Observação: Se o banco de dados ainda não estiver iniciado, a aplicação roda normalmente utilizando um repositório simulado em memória para demonstração imediata).*

---

## 🐘 Integração com Banco de Dados PostgreSQL

### Opção A: PostgreSQL Local com Docker
Se desejar rodar o PostgreSQL localmente via Docker:
```bash
docker compose up -d
```
O PostgreSQL inicializará na porta `5432` com o banco `escola_db` e executará automaticamente os scripts `sql/01_schema.sql` e `sql/02_seed.sql`.

---

## ⚡ Integração com Supabase

O projeto foi projetado para funcionar perfeitamente com o **Supabase**:

1. Crie um projeto gratuito no [Supabase](https://supabase.com).
2. No painel do Supabase, acesse **SQL Editor** e execute na ordem:
   - Arquivo `sql/01_schema.sql` (Criação de tabelas, chaves e regras)
   - Arquivo `sql/02_seed.sql` (Dados iniciais de turmas, alunos e notas)
3. Obtenha a connection string JDBC em **Project Settings -> Database**:
   - Formato recomendado:
     ```
     DB_URL=jdbc:postgresql://db.SEU_PROJETO.supabase.co:5432/postgres?sslmode=require
     DB_USER=postgres
     DB_PASSWORD=SUA_SENHA_DO_SUPABASE
     ```
4. Defina essas variáveis no seu arquivo `.env` ou nas variáveis de ambiente do sistema. A classe `DatabaseConfig.java` detecta e configura automaticamente!

---

## 🔺 Deploy na Vercel

O projeto possui os arquivos `vercel.json` e `package.json` configurados para deploy imediato na Vercel:

1. Suba o repositório para o seu **GitHub** (veja comandos abaixo).
2. Acesse [vercel.com](https://vercel.com) e clique em **Add New Project -> Import Git Repository**.
3. Selecione o repositório `friendly-nobel`.
4. A Vercel implantará as páginas de `public/` como site estático e a pasta `api/` como Serverless Functions.
5. (Opcional) Nas configurações de ambiente da Vercel (**Environment Variables**), você pode definir:
   - `JAVA_BACKEND_URL`: URL do seu backend Java (ex: hospedado no Render, Fly.io ou Railway) para que a Vercel faça proxy transparente de `/api/*`.

---

## 🔑 Perfis de Teste no Sistema

| Perfil | E-mail de Teste | Senha | Funcionalidades |
|---|---|---|---|
| **Professor** | `carlos.silva@escola.com` | `123456` | Lançamento de notas bimestrais, chamada diária, cálculo em tempo real de médias |
| **Aluno** | `lucas.mendes@escola.com` | `123456` | Visualização do boletim, percentual de frequência LDB com barra de corte de 75% |
| **Pais / Responsáveis** | `maria.mendes@email.com` | `123456` | Acompanhamento do rendimento dos filhos, alertas de risco de infrequência preventiva |

---

## 📁 Estrutura do Projeto

```
friendly-nobel/
├── build.ps1                       # Compilador e executor de testes em Java puro
├── run.ps1                         # Inicializador do servidor web Java
├── docker-compose.yml              # PostgreSQL conteinerizado
├── pom.xml                         # Projeto Maven padrão
├── vercel.json                     # Configuração para deploy na Vercel
├── package.json                    # Metadados e scripts para Vercel
├── .env.example                    # Exemplo de credenciais para PostgreSQL / Supabase
├── sql/
│   ├── 01_schema.sql               # Esquema relacional PostgreSQL / Supabase
│   └── 02_seed.sql                 # Dados de demonstração
├── lib/
│   └── postgresql-42.7.3.jar       # Driver JDBC oficial PostgreSQL
├── api/
│   └── index.js                    # Adaptador Serverless para deploy na Vercel
├── public/                         # Frontend Web Responsivo
│   ├── index.html                  # Tela de Login e seleção de perfis
│   ├── professor.html              # Painel do Professor (Notas e Chamada)
│   ├── aluno.html                  # Portal do Aluno (Boletim e Frequência LDB)
│   ├── responsavel.html            # Portal dos Pais (Alertas e Desempenho)
│   ├── css/styles.css              # Design moderno
│   └── js/
│       ├── api.js                  # Cliente unificado de API
│       ├── professor.js            # Lógica do painel do professor
│       ├── aluno.js                # Lógica do boletim do estudante
│       └── responsavel.js          # Lógica do portal dos responsáveis
└── src/
    ├── main/java/br/com/escola/
    │   ├── Main.java               # Inicializador do HttpServer
    │   ├── config/DatabaseConfig.java # Conexão JDBC PostgreSQL / Supabase
    │   ├── http/                   # Camada HTTP e Handlers REST
    │   └── modulos/
    │       ├── alunos/             # Alunos, Turmas e Matrículas
    │       ├── notas/              # Lançamento e Avaliações
    │       ├── presenca/           # Diário de Classe e Faltas
    │       └── regras/             # Motor LDB 75%, CEE e Regulamento Escolar
    └── test/java/br/com/escola/
        └── modulos/regras/
            └── MotorRegrasTest.java # Bateria de testes das regras da LDB
```
