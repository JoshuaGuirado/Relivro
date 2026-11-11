-- =========================================
-- RelivrO - Dados de exemplo (opcional)
-- Execute DEPOIS do script.sql para já ter usuários e livros para testar.
-- =========================================

INSERT INTO usuario (nome_completo, email, telefone, endereco, tipo_perfil) VALUES
    ('Ana Paula Ferreira', 'ana@relivro.com',   '(44) 99999-1001', 'Rua das Flores, 120 - Maringá/PR',   'doador'),
    ('Carlos Henrique Lima', 'carlos@relivro.com', '(44) 99999-1002', 'Av. Brasil, 2500 - Maringá/PR',     'doador'),
    ('Beatriz Souza', 'beatriz@relivro.com',    '(44) 99999-2001', 'Rua Santos Dumont, 45 - Sarandi/PR', 'solicitante'),
    ('Pedro Oliveira', 'pedro@relivro.com',     '(44) 99999-2002', 'Rua Paraná, 980 - Paiçandu/PR',      'solicitante');

INSERT INTO livro (titulo, autor, categoria, estado_conservacao, status, usuario_id) VALUES
    ('Dom Casmurro', 'Machado de Assis', 'Romance', 'Seminovo', 'disponivel',
        (SELECT id FROM usuario WHERE email = 'ana@relivro.com')),
    ('Matemática - Volume Único', 'Luiz Roberto Dante', 'Didático', 'Usado - bom estado', 'disponivel',
        (SELECT id FROM usuario WHERE email = 'ana@relivro.com')),
    ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Infantil', 'Novo', 'disponivel',
        (SELECT id FROM usuario WHERE email = 'carlos@relivro.com')),
    ('Química Geral', 'John B. Russell', 'Didático', 'Usado - desgastado', 'disponivel',
        (SELECT id FROM usuario WHERE email = 'carlos@relivro.com'));
