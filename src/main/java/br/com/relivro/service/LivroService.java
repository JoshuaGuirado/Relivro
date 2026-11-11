package br.com.relivro.service;

import br.com.relivro.dao.LivroDAO;
import br.com.relivro.dao.ReservaDAO;
import br.com.relivro.model.Doador;
import br.com.relivro.model.Livro;
import br.com.relivro.model.StatusLivro;

import java.util.List;

/**
 * Regras de negócio dos livros (RF2, RF3, RF5 e RF6).
 */
public class LivroService {

    private final LivroDAO livroDAO;
    private final ReservaDAO reservaDAO;

    public LivroService(LivroDAO livroDAO, ReservaDAO reservaDAO) {
        this.livroDAO = livroDAO;
        this.reservaDAO = reservaDAO;
    }

    /** RF2 - O doador cadastra um livro para doação. */
    public Livro cadastrar(Doador doador, Livro livro) {
        validar(livro);
        doador.cadastrarLivro(livro);
        return livroDAO.inserir(livro);
    }

    /** Carrega os livros do doador do banco e atualiza a composição Doador -> Livros. */
    public List<Livro> listarDoDoador(Doador doador) {
        List<Livro> livros = livroDAO.listarPorDoador(doador.getId());
        doador.setLivros(livros);
        return livros;
    }

    /** RF3 - Busca de livros disponíveis por categoria e estado de conservação. */
    public List<Livro> buscarDisponiveis(String categoria, String estadoConservacao) {
        return livroDAO.buscarDisponiveis(categoria, estadoConservacao);
    }

    public List<Livro> listarTodos() {
        return livroDAO.listarTodos();
    }

    public Livro buscarPorId(int id) {
        return livroDAO.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Livro #" + id + " não encontrado."));
    }

    /** Busca um livro garantindo que ele pertence ao doador logado. */
    public Livro buscarDoDoador(Doador doador, int livroId) {
        Livro livro = buscarPorId(livroId);
        if (livro.getDoadorId() != doador.getId()) {
            throw new ValidacaoException("O livro #" + livroId + " não pertence a você.");
        }
        return livro;
    }

    /** Edita título, autor, categoria e estado de conservação. */
    public void editar(Doador doador, Livro livroEditado) {
        buscarDoDoador(doador, livroEditado.getId());
        validar(livroEditado);
        livroDAO.atualizar(livroEditado);
    }

    /**
     * RF5 - Atualiza o status do livro (disponivel, reservado, doado).
     * Se o doador devolver o livro para "disponivel", as reservas pendentes são canceladas.
     */
    public void atualizarStatus(Doador doador, int livroId, StatusLivro novoStatus) {
        Livro livro = buscarDoDoador(doador, livroId);
        try {
            livro.atualizarStatus(novoStatus);
        } catch (IllegalStateException e) {
            throw new ValidacaoException(e.getMessage());
        }
        livroDAO.atualizarStatus(livroId, novoStatus);
        if (novoStatus == StatusLivro.DISPONIVEL) {
            reservaDAO.excluirPorLivro(livroId);
        }
    }

    /** RF6 - Só o doador responsável pode excluir o livro. */
    public void excluir(Doador doador, int livroId) {
        buscarDoDoador(doador, livroId);
        livroDAO.excluir(livroId);
    }

    private void validar(Livro livro) {
        UsuarioService.obrigatorio(livro.getTitulo(), "Título");
        UsuarioService.obrigatorio(livro.getAutor(), "Autor");
        UsuarioService.obrigatorio(livro.getCategoria(), "Categoria");
        UsuarioService.obrigatorio(livro.getEstadoConservacao(), "Estado de conservação");

        livro.setTitulo(livro.getTitulo().trim());
        livro.setAutor(livro.getAutor().trim());
        livro.setCategoria(livro.getCategoria().trim());
        livro.setEstadoConservacao(livro.getEstadoConservacao().trim());

        UsuarioService.limite(livro.getTitulo(), 200, "Título");
        UsuarioService.limite(livro.getAutor(), 150, "Autor");
        UsuarioService.limite(livro.getCategoria(), 80, "Categoria");
        UsuarioService.limite(livro.getEstadoConservacao(), 50, "Estado de conservação");
    }
}
