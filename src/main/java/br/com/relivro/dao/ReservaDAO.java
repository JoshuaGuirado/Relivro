package br.com.relivro.dao;

import br.com.relivro.model.Livro;
import br.com.relivro.model.Reserva;
import br.com.relivro.model.Solicitante;
import br.com.relivro.model.StatusLivro;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acesso à tabela "reserva".
 * As consultas fazem JOIN com livro e usuario para montar os objetos completos.
 */
public class ReservaDAO implements CrudDao<Reserva> {

    private static final String SELECT_COMPLETO =
            "SELECT r.id AS reserva_id, r.data_reserva, "
            + "l.id AS livro_id, l.titulo, l.autor, l.categoria, l.estado_conservacao, l.status, "
            + "l.usuario_id AS doador_id, "
            + "u.id AS solicitante_id, u.nome_completo, u.email, u.telefone, u.endereco "
            + "FROM reserva r "
            + "JOIN livro l ON l.id = r.livro_id "
            + "JOIN usuario u ON u.id = r.usuario_id ";

    @Override
    public Reserva inserir(Reserva reserva) {
        String sql = "INSERT INTO reserva (livro_id, usuario_id, data_reserva) VALUES (?, ?, ?) RETURNING id";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherInsert(stmt, reserva);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                reserva.setId(rs.getInt(1));
            }
            return reserva;
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Registra a reserva e muda o livro para "reservado" numa única transação.
     * O SELECT ... FOR UPDATE trava a linha do livro, evitando que duas pessoas
     * reservem o mesmo livro ao mesmo tempo.
     */
    public Reserva registrarReserva(Reserva reserva) {
        try (Connection conexao = ConexaoBD.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                String statusAtual;
                try (PreparedStatement stmt = conexao.prepareStatement(
                        "SELECT status FROM livro WHERE id = ? FOR UPDATE")) {
                    stmt.setInt(1, reserva.getLivro().getId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new DaoException("Livro não encontrado.");
                        }
                        statusAtual = rs.getString("status");
                    }
                }
                if (!StatusLivro.DISPONIVEL.getValorBanco().equals(statusAtual)) {
                    throw new DaoException("O livro acabou de ser reservado por outra pessoa.");
                }

                try (PreparedStatement stmt = conexao.prepareStatement(
                        "INSERT INTO reserva (livro_id, usuario_id, data_reserva) VALUES (?, ?, ?) RETURNING id")) {
                    preencherInsert(stmt, reserva);
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        reserva.setId(rs.getInt(1));
                    }
                }

                try (PreparedStatement stmt = conexao.prepareStatement(
                        "UPDATE livro SET status = ? WHERE id = ?")) {
                    stmt.setString(1, StatusLivro.RESERVADO.getValorBanco());
                    stmt.setInt(2, reserva.getLivro().getId());
                    stmt.executeUpdate();
                }

                conexao.commit();
                return reserva;
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao registrar reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Cancela a reserva e devolve o livro para "disponivel" (se ainda não foi doado),
     * numa única transação.
     */
    public boolean cancelarReserva(int reservaId, int livroId) {
        try (Connection conexao = ConexaoBD.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                int removidas;
                try (PreparedStatement stmt = conexao.prepareStatement("DELETE FROM reserva WHERE id = ?")) {
                    stmt.setInt(1, reservaId);
                    removidas = stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conexao.prepareStatement(
                        "UPDATE livro SET status = ? WHERE id = ? AND status = ?")) {
                    stmt.setString(1, StatusLivro.DISPONIVEL.getValorBanco());
                    stmt.setInt(2, livroId);
                    stmt.setString(3, StatusLivro.RESERVADO.getValorBanco());
                    stmt.executeUpdate();
                }
                conexao.commit();
                return removidas > 0;
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Reserva> buscarPorId(int id) {
        List<Reserva> resultado = consultar(SELECT_COMPLETO + "WHERE r.id = ?", id);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    public List<Reserva> listarTodos() {
        return consultar(SELECT_COMPLETO + "ORDER BY r.id", null);
    }

    public List<Reserva> listarPorSolicitante(int solicitanteId) {
        return consultar(SELECT_COMPLETO + "WHERE r.usuario_id = ? ORDER BY r.data_reserva DESC, r.id DESC",
                solicitanteId);
    }

    /** Reservas feitas sobre os livros de um doador. */
    public List<Reserva> listarPorDoador(int doadorId) {
        return consultar(SELECT_COMPLETO + "WHERE l.usuario_id = ? ORDER BY r.data_reserva DESC, r.id DESC",
                doadorId);
    }

    @Override
    public boolean atualizar(Reserva reserva) {
        String sql = "UPDATE reserva SET livro_id = ?, usuario_id = ?, data_reserva = ? WHERE id = ?";
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherInsert(stmt, reserva);
            stmt.setInt(4, reserva.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(int id) {
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM reserva WHERE id = ?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir reserva: " + e.getMessage(), e);
        }
    }

    /** Remove todas as reservas de um livro (ex.: doador devolveu o livro para "disponível"). */
    public int excluirPorLivro(int livroId) {
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM reserva WHERE livro_id = ?")) {
            stmt.setInt(1, livroId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao excluir reservas do livro: " + e.getMessage(), e);
        }
    }

    private void preencherInsert(PreparedStatement stmt, Reserva reserva) throws SQLException {
        stmt.setInt(1, reserva.getLivro().getId());
        stmt.setInt(2, reserva.getSolicitante().getId());
        stmt.setDate(3, Date.valueOf(reserva.getDataReserva()));
    }

    private List<Reserva> consultar(String sql, Integer parametro) {
        List<Reserva> reservas = new ArrayList<>();
        try (Connection conexao = ConexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setInt(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapear(rs));
                }
            }
            return reservas;
        } catch (SQLException e) {
            throw new DaoException("Erro ao consultar reservas: " + e.getMessage(), e);
        }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Livro livro = new Livro(
                rs.getInt("livro_id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("categoria"),
                rs.getString("estado_conservacao"),
                StatusLivro.fromValorBanco(rs.getString("status")),
                rs.getInt("doador_id"));
        Solicitante solicitante = new Solicitante(
                rs.getInt("solicitante_id"),
                rs.getString("nome_completo"),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("endereco"));
        return new Reserva(
                rs.getInt("reserva_id"),
                livro,
                solicitante,
                rs.getDate("data_reserva").toLocalDate());
    }
}
