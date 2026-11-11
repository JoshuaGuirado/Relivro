package br.com.relivro.model;

import java.time.LocalDate;

/**
 * Usuário que procura livros e registra pedidos de reserva.
 *
 * Herança: estende Usuario.
 */
public class Solicitante extends Usuario {

    public Solicitante(String nomeCompleto, String email, String telefone, String endereco) {
        super(nomeCompleto, email, telefone, endereco);
    }

    public Solicitante(int id, String nomeCompleto, String email, String telefone, String endereco) {
        super(id, nomeCompleto, email, telefone, endereco);
    }

    @Override
    public void exibirPainel() {
        System.out.println();
        System.out.println("==============================================");
        System.out.println(" PAINEL DO SOLICITANTE - " + getNomeCompleto());
        System.out.println("==============================================");
        System.out.println(" Busque livros disponíveis, faça reservas e");
        System.out.println(" combine a retirada com o doador.");
        System.out.println(" Entrega em: " + getEndereco());
        System.out.println("----------------------------------------------");
    }

    @Override
    public String getTipoPerfil() {
        return "solicitante";
    }

    /**
     * Cria um pedido de reserva para o livro (RF4).
     * Regra de negócio: só é possível reservar livros com status "disponivel".
     */
    public Reserva reservarLivro(Livro livro) {
        if (livro.getStatus() != StatusLivro.DISPONIVEL) {
            throw new IllegalStateException(
                    "O livro \"" + livro.getTitulo() + "\" não está disponível (status: " + livro.getStatus() + ").");
        }
        livro.atualizarStatus(StatusLivro.RESERVADO);
        return new Reserva(livro, this, LocalDate.now());
    }
}
