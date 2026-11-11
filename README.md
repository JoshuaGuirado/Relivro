# RelivrO

Sistema para doação e reaproveitamento de livros, conectando pessoas que desejam doar livros que não usam mais com pessoas que precisam de acesso à leitura e material de estudo.

##  Equipe

- Joshua
- Douglas
- João Campiolo

##  ODS Relacionados

 **ODS 4 — Educação de Qualidade**: amplia o acesso a livros e materiais de estudo para quem não tem condições de comprá-los.
 **ODS 10 — Redução das Desigualdades**: reduz a desigualdade de acesso à leitura e à educação entre diferentes classes sociais.

##  Requisitos do Sistema

1. O sistema deve permitir o cadastro de usuários, com nome completo, e-mail, telefone, endereço e tipo de perfil (doador ou solicitante).
2. O sistema deve permitir o cadastro de livros disponíveis para doação, com título, autor, categoria/gênero, estado de conservação e usuário doador responsável.
3. O sistema deve permitir a busca de livros disponíveis, filtrando por categoria e estado de conservação.
4. O sistema deve permitir que um solicitante registre um pedido de reserva sobre um livro disponível, vinculando o solicitante ao livro.
5. O sistema deve permitir a atualização do status de um livro (disponível, reservado, doado), refletindo o andamento do processo de doação.
6. O sistema deve permitir a exclusão de um cadastro de livro pelo usuário doador responsável, caso ele deseje remover o item da lista.

##  Cronograma de Execução

| Data | Atividade | Responsável |
|---|---|---|
| Semana 1 | Definição do tema, ODS e levantamento de requisitos | Todo o grupo |
| Semana 2 | Modelagem do Diagrama de Classe (UML) e do DER do banco | [Joshua] |
| Semana 3 | Justificativa técnica e estruturação do repositório GitHub | [Douglas] |
| Semana 4 | Revisão geral, formatação do PDF e entrega da 1ª etapa | Todo o grupo |
| Semana 5–6 | Implementação das classes (model) e conexão com banco | [João Campiolo] |
| Semana 7 | Implementação do CRUD completo (Create, Read, Update, Delete) | [João Campiolo] |
| Semana 8 | Testes, ajustes finais e entrega da 2ª etapa | Todo o grupo |

##  Tecnologias

- **Linguagem:** Java 17+
- **Banco de Dados:** PostgreSQL (Supabase ou local)
- **Acesso ao banco:** JDBC (driver `org.postgresql`)
- **Build:** Maven
- **Arquitetura:** Camadas (model, dao, service, view)

##  Estrutura do Repositório

```
relivro/
├── src/main/java/br/com/relivro/
│   ├── Main.java          # Ponto de entrada: monta as camadas e abre o menu
│   ├── model/             # Usuario (abstrata), Doador, Solicitante, Livro, Reserva, StatusLivro
│   ├── dao/               # CrudDao (interface), UsuarioDAO, LivroDAO, ReservaDAO, ConexaoBD
│   ├── service/           # Regras de negócio e validações
│   └── view/              # Menus do terminal (principal, doador, solicitante)
├── docs/                  # Diagrama de Classe e DER do banco
├── database/
│   ├── script.sql         # Criação das tabelas
│   └── dados_exemplo.sql  # Dados de exemplo (opcional)
├── db.properties.example  # Modelo de configuração do banco
├── docker-compose.yml     # PostgreSQL local pronto (opcional)
└── pom.xml                # Dependências e build (Maven)
```

---

##  Como Executar

### 1. Pré-requisitos

| Ferramenta | Versão | Como verificar |
|---|---|---|
| JDK (Java) | 17 ou superior | `java -version` |
| Maven | 3.8 ou superior | `mvn -v` |
| PostgreSQL | 14 ou superior **ou** projeto no Supabase **ou** Docker | — |

Instalação:
- **Windows:** baixe o JDK em [adoptium.net](https://adoptium.net) e o Maven em [maven.apache.org](https://maven.apache.org/download.cgi) (descompacte e adicione a pasta `bin` ao `PATH`). Ou, com winget: `winget install EclipseAdoptium.Temurin.17.JDK` e `winget install Apache.Maven`.
- **Linux (Ubuntu/Debian):** `sudo apt install openjdk-17-jdk maven`
- **macOS (Homebrew):** `brew install openjdk@17 maven`

> Usando IntelliJ IDEA? Basta abrir a pasta do projeto: ele reconhece o `pom.xml` e já tem Maven embutido. Execute a classe `br.com.relivro.Main`.

### 2. Preparar o banco de dados (escolha uma opção)

**Opção A — Supabase (banco do grupo)**
1. No painel do Supabase, abra **SQL Editor**, cole o conteúdo de `database/script.sql` e execute.
2. (Opcional) Faça o mesmo com `database/dados_exemplo.sql`.
3. Clique em **Connect** › **Session pooler** e anote host, porta, usuário e senha.

**Opção B — PostgreSQL instalado no computador**
```bash
createdb -U postgres relivro
psql -U postgres -d relivro -f database/script.sql
psql -U postgres -d relivro -f database/dados_exemplo.sql   # opcional
```

**Opção C — Docker** (já cria as tabelas e os dados de exemplo)
```bash
docker compose up -d
```

### 3. Configurar a conexão

Copie o arquivo de exemplo e edite com os dados do seu banco:

```bash
# Linux / macOS
cp db.properties.example db.properties

# Windows (CMD ou PowerShell)
copy db.properties.example db.properties
```

As opções B e C funcionam sem alterar nada. Para o Supabase, use a URL do **Session pooler** com `?sslmode=require` (veja os comentários no arquivo).

> Alternativa: em vez do arquivo, defina as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. Elas têm prioridade sobre o `db.properties`.

### 4. Compilar e executar

Na pasta raiz do projeto:

```bash
mvn clean package
java -jar target/relivro.jar
```

O comando é o mesmo no Windows, Linux e macOS. O `db.properties` deve estar na pasta de onde o comando é executado (a raiz do projeto).

---

##  Como Usar

1. No menu principal, cadastre um usuário **doador** e um **solicitante** (ou use os dados de exemplo, como `ana@relivro.com` e `beatriz@relivro.com`).
2. Use **"Entrar no sistema"** com o e-mail do doador para cadastrar, editar, mudar o status e excluir livros.
3. Entre com o e-mail do solicitante para buscar livros com filtros, reservar e cancelar reservas.

##  Requisitos × Implementação

| Requisito | Onde está no código | Menu |
|---|---|---|
| RF1 — Cadastro de usuários | `UsuarioService`, `UsuarioDAO` | Principal › 1 a 4 (cadastrar, listar, editar, excluir) |
| RF2 — Cadastro de livros | `LivroService.cadastrar`, `Doador.cadastrarLivro`, `LivroDAO.inserir` | Doador › 1 |
| RF3 — Busca com filtros | `LivroService.buscarDisponiveis`, `LivroDAO.buscarDisponiveis` | Solicitante › 1 |
| RF4 — Reserva de livro | `Solicitante.reservarLivro`, `ReservaService.reservar`, `ReservaDAO.registrarReserva` | Solicitante › 2 a 4 |
| RF5 — Atualização de status | `Livro.atualizarStatus`, `LivroService.atualizarStatus` | Doador › 4 |
| RF6 — Exclusão pelo doador responsável | `LivroService.excluir` (verifica se o livro é do doador) | Doador › 5 |

**CRUD da entidade principal (Livro):** Create (Doador › 1), Read (Doador › 2 e Solicitante › 1), Update (Doador › 3 e 4), Delete (Doador › 5).

##  Pilares de POO

| Pilar | Onde |
|---|---|
| **Classe abstrata** | `model/Usuario.java` (métodos abstratos `exibirPainel()` e `getTipoPerfil()`) |
| **Interface** | `dao/CrudDao.java`, implementada por `UsuarioDAO`, `LivroDAO` e `ReservaDAO` |
| **Herança** | `Doador extends Usuario` e `Solicitante extends Usuario` |
| **Polimorfismo (@Override)** | `exibirPainel()` e `getTipoPerfil()` em `Doador` e `Solicitante`; métodos de `CrudDao` em cada DAO; `toString()` nas entidades |
| **Composição (1:N)** | `Doador` possui a lista de `Livro` (no banco: `livro.usuario_id` com `ON DELETE CASCADE`) |
| **Encapsulamento** | Atributos `private` com getters/setters; validações na camada `service` |

##  Persistência

- Conexão real com PostgreSQL via JDBC (`dao/ConexaoBD.java`); nenhum dado fica só em memória.
- Script de criação em `database/script.sql`, compatível com as classes Java:
  - `usuario` ↔ `Usuario` / `Doador` / `Solicitante` (coluna `tipo_perfil`)
  - `livro` ↔ `Livro` (coluna `status` ↔ enum `StatusLivro`)
  - `reserva` ↔ `Reserva`
- A reserva usa transação (`SELECT ... FOR UPDATE`) para que duas pessoas não reservem o mesmo livro ao mesmo tempo.
