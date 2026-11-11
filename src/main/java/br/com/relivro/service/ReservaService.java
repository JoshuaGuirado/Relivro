package br.com.relivro.service;

import br.com.relivro.dao.DaoException;
import br.com.relivro.dao.LivroDAO;
import br.com.relivro.dao.ReservaDAO;
import br.com.relivro.model.Doador;
import br.com.relivro.model.Livro;
import br.com.relivro.model.Reserva;
import br.com.relivro.model.Solicitante;

import java.util.List;

/**
 * Regras de negócio das reservas (RF4).
 */
public class ReservaService {

    private final ReservaDAO reservaDAO;
    private final LivroDAO livroDAO;

    public ReservaService(ReservaDAO reservaDAO, LivroDAO livroDAO) {
        this.reservaDAO = reservaDAO;
        this.livroDAO = livroDAO;
    }

    /** RF4 - O solicitante reserva um livro disponível. */
    public Reserva reservar(Solicitante solicitante, int livroId) {
        Livro livro = livroDAO.buscarPorId(livroId)
                .orElseThrow(() -> new ValidacaoException("Livro #" + livroId + " não encontrado."));

        Reserva reserva;
        try {
            reserva = solicitante.reservarLivro(livro);
        } catch (IllegalStateException e) {
            throw new ValidacaoException(e.getMessage());
        }

        try {
            return reservaDAO.registrarReserva(reserva);
        } catch (DaoException e) {
            throw new ValidacaoException(e.getMessage());
        }
    }

    public List<Reserva> listarDoSolicitante(Solicitante solicitante) {
        return reservaDAO.listarPorSolicitante(solicitante.getId());
    }

    /** Reservas feitas nos livros do doador (para ele combinar a entrega). */
    public List<Reserva> listarDoDoador(Doador doador) {
        return reservaDAO.listarPorDoador(doador.getId());
    }

    /** O solicitante cancela a própria reserva; o livro volta a ficar disponível. */
    public void cancelar(Solicitante solicitante, int reservaId) {
        Reserva reserva = reservaDAO.buscarPorId(reservaId)
                .orElseThrow(() -> new ValidacaoException("Reserva #" + reservaId + " não encontrada."));
        if (reserva.getSolicitante().getId() != solicitante.getId()) {
            throw new ValidacaoException("A reserva #" + reservaId + " não é sua.");
        }
        reservaDAO.cancelarReserva(reservaId, reserva.getLivro().getId());
    }
}
