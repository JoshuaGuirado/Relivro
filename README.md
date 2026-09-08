RelivrO

Plataforma web para doação e reaproveitamento de livros, conectando pessoas que desejam doar livros que não usam mais com pessoas que precisam de acesso à leitura e material de estudo.

🎯 ODS Relacionados
ODS 4 — Educação de Qualidade: amplia o acesso a livros e materiais de estudo para quem não tem condições de comprá-los.
ODS 10 — Redução das Desigualdades: reduz a desigualdade de acesso à leitura e à educação entre diferentes classes sociais.

📋 Requisitos do Sistema
O sistema deve permitir o cadastro de usuários, com nome completo, e-mail, telefone, endereço e tipo de perfil (doador ou solicitante).
O sistema deve permitir o cadastro de livros disponíveis para doação, com título, autor, categoria/gênero, estado de conservação e usuário doador responsável.
O sistema deve permitir a busca de livros disponíveis, filtrando por categoria e estado de conservação.
O sistema deve permitir que um solicitante registre um pedido de reserva sobre um livro disponível, vinculando o solicitante ao livro.
O sistema deve permitir a atualização do status de um livro (disponível, reservado, doado), refletindo o andamento do processo de doação.
O sistema deve permitir a exclusão de um cadastro de livro pelo usuário doador responsável, caso ele deseje remover o item da lista.

🗓️ Cronograma de Execução
Data	Atividade	Responsável
Semana 1	Definição do tema, ODS e levantamento de requisitos	Todo o grupo
Semana 2	Modelagem do Diagrama de Classe (UML) e do DER do banco	[Joshua Guirado]
Semana 3	Justificativa técnica e estruturação do repositório GitHub	[João Campiolo]
Semana 4	Revisão geral, formatação do PDF e entrega da 1ª etapa	Todo o grupo
Semana 5–6	Implementação das classes (model) e conexão com banco	[Douglas Guirado]
Semana 7	Implementação do CRUD completo (Create, Read, Update, Delete)	João Campiolo]
Semana 8	Testes, ajustes finais e entrega da 2ª etapa	Todo o grupo

📁 Estrutura do Repositório
relivro/
├── src/          # Código-fonte da aplicação
├── docs/         # Diagrama de Classe e DER do banco
└── database/     # Script SQL de criação das tabelas
🛠️ Tecnologias
Linguagem: Java

Banco de Dados: [SUPABASE]
Arquitetura: Camadas (model, dao, service)
