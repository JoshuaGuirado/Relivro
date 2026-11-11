package br.com.relivro.view;

import br.com.relivro.dao.DaoException;
import br.com.relivro.model.Doador;
import br.com.relivro.model.Livro;
import br.com.relivro.model.StatusLivro;
import br.com.relivro.service.LivroService;
import br.com.relivro.service.ReservaService;
import br.com.relivro.service.ValidacaoException;

import java.util.Arrays;
import java.util.List;

/**
 * Menu do doador: CRUD completo de livros (RF2, RF5, RF6) e acompanhamento das reservas.
 */
public class MenuDoador {

    private final LivroService livroService;
    private final ReservaService reservaService;
    private final Doador doador;

    public MenuDoador(LivroService livroService, ReservaService reservaService, Doador doador) {
        this.livroService = livroService;
        this.reservaService = reservaService;
        this.doador = doador;
    }

    public void abrir() {
        while (true) {
            livroService.listarDoDoador(doador);
            doador.exibirPainel();
            Console.info("  1. Cadastrar livro para doação");
            Console.info("  2. Meus livros");
            Console.info("  3. Editar dados de um livro");
            Console.info("  4. Atualizar status de um livro");
            Console.info("  5. Excluir livro");
            Console.info("  6. Ver reservas dos meus livros");
            Console.info("  0. Sair do painel");

            String opcao = Console.lerLinha("Escolha: ").trim();
            if (opcao.equals("0")) {
                return;
            }
            try {
                switch (opcao) {
                    case "1" -> cadastrarLivro();
                    case "2" -> Console.listar(livroService.listarDoDoador(doador),
                            "Você ainda não cadastrou livros.");
                    case "3" -> editarLivro();
                    case "4" -> atualizarStatus();
                    case "5" -> excluirLivro();
                    case "6" -> Console.listar(reservaService.listarDoDoador(doador),
                            "Nenhuma reserva nos seus livros.");
                    default -> Console.erro("Opção inválida.");
                }
            } catch (ValidacaoException e) {
                Console.erro(e.getMessage());
            } catch (DaoException e) {
                Console.erro("Problema no banco de dados: " + e.getMessage());
            }
        }
    }

    private void cadastrarLivro() {
        Console.titulo("Cadastrar livro");
        String titulo = Console.lerTexto("Título");
        String autor = Console.lerTexto("Autor");
        String categoria = Console.lerTexto("Categoria/gênero (ex.: Romance, Didático, Infantil)");
        Console.info("Estado de conservação:");
        String estado = Opcoes.ESTADOS_CONSERVACAO.get(Console.escolher("Estado", Opcoes.ESTADOS_CONSERVACAO));

        Livro livro = new Livro(titulo, autor, categoria, estado);
        livroService.cadastrar(doador, livro);
        Console.sucesso("Livro cadastrado com o id #" + livro.getId() + " e status \"" + livro.getStatus() + "\".");
    }

    private void editarLivro() {
        Console.titulo("Editar livro");
        Livro livro = livroService.buscarDoDoador(doador, Console.lerInteiro("Id do livro"));
        Console.info("Aperte Enter para manter o valor atual.");
        livro.setTitulo(Console.lerOuManter("Título", livro.getTitulo()));
        livro.setAutor(Console.lerOuManter("Autor", livro.getAutor()));
        livro.setCategoria(Console.lerOuManter("Categoria", livro.getCategoria()));
        if (Console.confirmar("Alterar o estado de conservação (atual: " + livro.getEstadoConservacao() + ")?")) {
            livro.setEstadoConservacao(
                    Opcoes.ESTADOS_CONSERVACAO.get(Console.escolher("Estado", Opcoes.ESTADOS_CONSERVACAO)));
        }
        livroService.editar(doador, livro);
        Console.sucesso("Livro #" + livro.getId() + " atualizado.");
    }

    private void atualizarStatus() {
        Console.titulo("Atualizar status");
        Livro livro = livroService.buscarDoDoador(doador, Console.lerInteiro("Id do livro"));
        Console.info("  " + livro);
        Console.info("Novo status:");
        List<StatusLivro> status = Arrays.asList(StatusLivro.values());
        List<String> rotulos = status.stream().map(StatusLivro::getValorBanco).toList();
        StatusLivro novo = status.get(Console.escolher("Status", rotulos));

        if (novo == StatusLivro.DISPONIVEL && livro.getStatus() == StatusLivro.RESERVADO
                && !Console.confirmar("As reservas deste livro serão canceladas. Continuar?")) {
            Console.info("Operação cancelada.");
            return;
        }
        livroService.atualizarStatus(doador, livro.getId(), novo);
        Console.sucesso("Status do livro #" + livro.getId() + " alterado para \"" + novo + "\".");
    }

    private void excluirLivro() {
        Console.titulo("Excluir livro");
        Livro livro = livroService.buscarDoDoador(doador, Console.lerInteiro("Id do livro"));
        Console.info("  " + livro);
        if (Console.confirmar("Confirma a exclusão?")) {
            livroService.excluir(doador, livro.getId());
            Console.sucesso("Livro #" + livro.getId() + " excluído.");
        } else {
            Console.info("Exclusão cancelada.");
        }
    }
}
