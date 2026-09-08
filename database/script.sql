-- =========================================
-- RelivrO - Script de criação das tabelas
-- Banco: PostgreSQL (Supabase)
-- =========================================

-- Tabela: usuario
-- Armazena tanto Doadores quanto Solicitantes (definidos por tipo_perfil)
CREATE TABLE usuario (
    id             SERIAL PRIMARY KEY,
    nome_completo  VARCHAR(150) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    telefone       VARCHAR(20)  NOT NULL,
    endereco       VARCHAR(200) NOT NULL,
    tipo_perfil    VARCHAR(20)  NOT NULL CHECK (tipo_perfil IN ('doador', 'solicitante'))
);

-- Tabela: livro
-- Cada livro pertence a um usuario (doador) responsável
CREATE TABLE livro (
    id                   SERIAL PRIMARY KEY,
    titulo               VARCHAR(200) NOT NULL,
    autor                VARCHAR(150) NOT NULL,
    categoria            VARCHAR(80)  NOT NULL,
    estado_conservacao   VARCHAR(50)  NOT NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'disponivel'
                         CHECK (status IN ('disponivel', 'reservado', 'doado')),
    usuario_id           INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabela: reserva
-- Liga um solicitante (usuario) a um livro que ele deseja
CREATE TABLE reserva (
    id             SERIAL PRIMARY KEY,
    livro_id       INTEGER NOT NULL REFERENCES livro(id) ON DELETE CASCADE,
    usuario_id     INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    data_reserva   DATE NOT NULL DEFAULT CURRENT_DATE
);

-- =========================================
-- Índices auxiliares (melhoram performance de busca)
-- =========================================
CREATE INDEX idx_livro_usuario ON livro(usuario_id);
CREATE INDEX idx_reserva_livro ON reserva(livro_id);
CREATE INDEX idx_reserva_usuario ON reserva(usuario_id);
