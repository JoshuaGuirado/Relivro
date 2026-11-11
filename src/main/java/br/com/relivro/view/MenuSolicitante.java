package br.com.relivro.view;

import br.com.relivro.dao.DaoException;
import br.com.relivro.model.Livro;
import br.com.relivro.model.Reserva;
import br.com.relivro.model.Solicitante;
import br.com.relivro.service.LivroService;
import br.com.relivro.service.ReservaService;
import br.com.relivro.service.ValidacaoException;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu do solicitante: busca de livros (RF3) e reservas (RF4).
 */
public class MenuSolicitante {

    private final LivroService livroService;
    private final ReservaService reservaService;
    private final Solicitante solicitante;

    public MenuSolicitante(LivroService livroService, ReservaService reservaService, Solicitante solicitante) {
        this.livroService = livroService;
        this.reservaService = reservaService;
        this.solicitante = solicitante;
    }

    public void abrir() {
        while (true) {
            solicitante.exibirPainel();
            Console.info("  1. Buscar livros disponíveis");
            Console.info("  2. Reservar livro");
            Console.info("  3. Minhas reservas");
            Console.info("  4. Cancelar reserva");
            Console.info("  0. Sair do painel");

            String opcao = Console.lerLinha("Escolha: ").trim();
            if (opcao.equals("0")) {
                return;
            }
            try {
                switch (opcao) {
                    case "1" -> buscarLivros();
                    case "2" -> reservarLivro();
                    case "3" -> Console.listar(reservaService.listarDoSolicitante(solicitante),
                            "Você não tem reservas.");
                    case "4" -> cancelarReserva();
                    default -> Console.erro("Opção inválida.");
                }
            } catch (ValidacaoException e) {
                Console.erro(e.getMessage());
            } catch (DaoException e) {
                Console.erro("Problema no banco de dados: " + e.getMessage());
            }
        }
    }

    private void buscarLivros() {
        Console.titulo("Buscar livros disponíveis");
        String categoria = Console.lerOpcional("Categoria");

        List<String> opcoesEstado = new ArrayList<>(Opcoes.ESTADOS_CONSERVACAO);
        opcoesEstado.add("Qualquer estado");
        Console.info("Estado de conservação:");
        int escolha = Console.escolher("Estado", opcoesEstado);
        String estado = escolha < Opcoes.ESTADOS_CONSERVACAO.size() ? opcoesEstado.get(escolha) : null;

        List<Livro> livros = livroService.buscarDisponiveis(categoria, estado);
        Console.listar(livros, "Nenhum livro encontrado com esses filtros.");
    }

    private void reservarLivro() {
        Console.titulo("Reservar livro");
        int livroId = Console.lerInteiro("Id do livro (veja na busca)");
        Reserva reserva = reservaService.reservar(solicitante, livroId);
        Console.sucesso("Reserva #" + reserva.getId() + " registrada para \""
                + reserva.getLivro().getTitulo() + "\". Combine a retirada com o doador.");
    }

    private void cancelarReserva() {
        Console.titulo("Cancelar reserva");
        int reservaId = Console.lerInteiro("Id da reserva");
        if (Console.confirmar("Confirma o cancelamento da reserva #" + reservaId + "?")) {
            reservaService.cancelar(solicitante, reservaId);
            Console.sucesso("Reserva cancelada. O livro voltou a ficar disponível.");
        }
    }
}
