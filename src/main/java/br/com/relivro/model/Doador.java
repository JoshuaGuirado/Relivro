package br.com.relivro.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Usuário que disponibiliza livros para doação.
 *
 * Herança: estende Usuario.
 * Composição (1:N): um Doador possui 0..* Livros. Os livros pertencem ao doador
 * e são excluídos junto com ele (ON DELETE CASCADE no banco).
 */
public class Doador extends Usuario {

    private final List<Livro> livros = new ArrayList<>();

    public Doador(String nomeCompleto, String email, String telefone, String endereco) {
        super(nomeCompleto, email, telefone, endereco);
    }

    public Doador(int id, String nomeCompleto, String email, String telefone, String endereco) {
        super(id, nomeCompleto, email, telefone, endereco);
    }

    @Override
    public void exibirPainel() {
        long disponiveis = contarPorStatus(StatusLivro.DISPONIVEL);
        long reservados = contarPorStatus(StatusLivro.RESERVADO);
        long doados = contarPorStatus(StatusLivro.DOADO);

        System.out.println();
        System.out.println("==============================================");
        System.out.println(" PAINEL DO DOADOR - " + getNomeCompleto());
        System.out.println("==============================================");
        System.out.println(" Livros cadastrados: " + livros.size());
        System.out.println("   Disponíveis: " + disponiveis
                + " | Reservados: " + reservados
                + " | Doados: " + doados);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getTipoPerfil() {
        return "doador";
    }

    /** Vincula um livro a este doador (o livro passa a fazer parte da composição). */
    public void cadastrarLivro(Livro livro) {
        livro.setDoadorId(getId());
        livros.add(livro);
    }

    public List<Livro> getLivros() {
        return Collections.unmodifiableList(livros);
    }

    /** Recarrega a lista de livros (usado depois de buscar no banco). */
    public void setLivros(List<Livro> novosLivros) {
        livros.clear();
        livros.addAll(novosLivros);
    }

    private long contarPorStatus(StatusLivro status) {
        return livros.stream().filter(l -> l.getStatus() == status).count();
    }
}
