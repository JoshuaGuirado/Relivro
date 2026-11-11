package br.com.relivro.dao;

import br.com.relivro.model.Livro;
import br.com.relivro.model.StatusLivro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acesso à tabela "livro" (entidade principal do CRUD).
 */
public class LivroDAO implements CrudDao<Livro> {

    private static final String COLUNAS =
            "id, titulo, autor, categoria, estado_conservacao, status, usuario_id";

    @Override
    public Livro inserir(Livro livro) {
        String sql = "INSERT INTO livro (titulo, autor, categoria, estado_conservacao, status, usuario_id) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getCategoria());
            stmt.setString(4, livro.getEstadoConservacao());
            stmt.setString(5, livro.getStatus().getValorBanco());
            stmt.setInt(6, livro.getDoadorId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                livro.setId(rs.getInt("id"));
            }
            return livro;
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Livro> buscarPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM livro WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Livro> listarTodos() {
        return consultar("SELECT " + COLUNAS + " FROM livro ORDER BY id", new ArrayList<>());
    }

    /** Livros de um doador específico. */
    public List<Livro> listarPorDoador(int doadorId) {
        List<Object> parametros = new ArrayList<>();
        parametros.add(doadorId);
        return consultar("SELECT " + COLUNAS + " FROM livro WHERE usuario_id = ? ORDER BY id", parametros);
    }

    /**
     * Busca de livros disponíveis com filtros opcionais (RF3).
     * Filtros nulos ou em branco são ignorados; a comparação ignora maiúsculas/minúsculas.
     */
    public List<Livro> buscarDisponiveis(String categoria, String estadoConservacao) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM livro WHERE status = ?");
        List<Object> parametros = new ArrayList<>();
        parametros.add(StatusLivro.DISPONIVEL.getValorBanco());

        if (categoria != null && !categoria.isBlank()) {
            sql.append(" AND categoria ILIKE ?");
            parametros.add("%" + categoria.trim() + "%");
        }
        if (estadoConservacao != null && !estadoConservacao.isBlank()) {
            sql.append(" AND estado_conservacao ILIKE ?");
            parametros.add("%" + estadoConservacao.trim() + "%");
        }
        sql.append(" ORDER BY titulo");
        return consultar(sql.toString(), parametros);
    }

    @Override
    public boolean atualizar(Livro livro) {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, categoria = ?, estado_conservacao = ?, status = ? "
                + "WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getCategoria());
            stmt.setString(4, livro.getEstadoConservacao());
            stmt.setString(5, livro.getStatus().getValorBanco());
            stmt.setInt(6, livro.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar livro: " + e.getMessage(), e);
        }
    }

    /** Atualiza apenas o status (RF5). */
    public boolean atualizarStatus(int livroId, StatusLivro novoStatus) {
        String sql = "UPDATE livro SET status = ? WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoStatus.getValorBanco());
            stmt.setInt(2, livroId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar status do livro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(int id) {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir livro: " + e.getMessage(), e);
        }
    }

    private List<Livro> consultar(String sql, List<Object> parametros) {
        List<Livro> livros = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    livros.add(mapear(rs));
                }
            }
            return livros;
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar livros: " + e.getMessage(), e);
        }
    }

    private Livro mapear(ResultSet rs) throws SQLException {
        return new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("categoria"),
                rs.getString("estado_conservacao"),
                StatusLivro.fromValorBanco(rs.getString("status")),
                rs.getInt("usuario_id"));
    }
}
