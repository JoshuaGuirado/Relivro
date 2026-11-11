package br.com.relivro.model;

/**
 * Livro disponibilizado para doação (entidade principal do sistema).
 */
public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private String categoria;
    private String estadoConservacao;
    private StatusLivro status = StatusLivro.DISPONIVEL;
    private int doadorId;

    public Livro(String titulo, String autor, String categoria, String estadoConservacao) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.estadoConservacao = estadoConservacao;
    }

    public Livro(int id, String titulo, String autor, String categoria,
                 String estadoConservacao, StatusLivro status, int doadorId) {
        this(titulo, autor, categoria, estadoConservacao);
        this.id = id;
        this.status = status;
        this.doadorId = doadorId;
    }

    /**
     * Atualiza o status do livro no fluxo de doação (RF5).
     * Regra de negócio: um livro já doado não volta a ficar disponível ou reservado.
     */
    public void atualizarStatus(StatusLivro novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status é obrigatório.");
        }
        if (this.status == StatusLivro.DOADO && novoStatus != StatusLivro.DOADO) {
            throw new IllegalStateException("Este livro já foi doado e não pode mudar de status.");
        }
        this.status = novoStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public StatusLivro getStatus() {
        return status;
    }

    public int getDoadorId() {
        return doadorId;
    }

    public void setDoadorId(int doadorId) {
        this.doadorId = doadorId;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s - %s | Categoria: %s | Estado: %s | Status: %s",
                id, titulo, autor, categoria, estadoConservacao, status);
    }
}
