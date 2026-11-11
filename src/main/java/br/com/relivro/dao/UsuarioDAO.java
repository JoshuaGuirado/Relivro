package br.com.relivro.dao;

import br.com.relivro.model.Doador;
import br.com.relivro.model.Solicitante;
import br.com.relivro.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acesso à tabela "usuario".
 * Doadores e solicitantes ficam na mesma tabela, diferenciados por tipo_perfil.
 * Ao ler do banco, o DAO cria o objeto da subclasse certa (Doador ou Solicitante).
 */
public class UsuarioDAO implements CrudDao<Usuario> {

    private static final String COLUNAS = "id, nome_completo, email, telefone, endereco, tipo_perfil";

    @Override
    public Usuario inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome_completo, email, telefone, endereco, tipo_perfil) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setString(4, usuario.getEndereco());
            stmt.setString(5, usuario.getTipoPerfil());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                usuario.setId(rs.getInt("id"));
            }
            return usuario;
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE LOWER(email) = LOWER(?)";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar usuário por e-mail: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT " + COLUNAS + " FROM usuario ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar usuários: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome_completo = ?, email = ?, telefone = ?, endereco = ? WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setString(4, usuario.getEndereco());
            stmt.setInt(5, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    /** Converte uma linha do banco no objeto da subclasse correta. */
    private Usuario mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome_completo");
        String email = rs.getString("email");
        String telefone = rs.getString("telefone");
        String endereco = rs.getString("endereco");
        String tipo = rs.getString("tipo_perfil");

        return switch (tipo) {
            case "doador" -> new Doador(id, nome, email, telefone, endereco);
            case "solicitante" -> new Solicitante(id, nome, email, telefone, endereco);
            default -> throw new DaoException("tipo_perfil inválido no banco: " + tipo);
        };
    }
}
