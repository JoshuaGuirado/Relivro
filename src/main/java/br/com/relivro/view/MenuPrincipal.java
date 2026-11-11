package br.com.relivro.view;

import br.com.relivro.dao.DaoException;
import br.com.relivro.model.Doador;
import br.com.relivro.model.Solicitante;
import br.com.relivro.model.Usuario;
import br.com.relivro.service.LivroService;
import br.com.relivro.service.ReservaService;
import br.com.relivro.service.UsuarioService;
import br.com.relivro.service.ValidacaoException;

import java.util.Optional;

/**
 * Menu inicial: CRUD de usuários (RF1), entrada no sistema e vitrine de livros.
 */
public class MenuPrincipal {

    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final ReservaService reservaService;

    public MenuPrincipal(UsuarioService usuarioService, LivroService livroService, ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.livroService = livroService;
        this.reservaService = reservaService;
    }

    public void abrir() {
        while (true) {
            Console.titulo("RELIVRO - Doação de Livros");
            Console.info("  1. Cadastrar usuário");
            Console.info("  2. Listar usuários");
            Console.info("  3. Editar usuário");
            Console.info("  4. Excluir usuário");
            Console.info("  5. Entrar no sistema (por e-mail)");
            Console.info("  6. Ver livros disponíveis");
            Console.info("  0. Sair");

            String opcao = Console.lerLinha("Escolha: ").trim();
            if (opcao.equals("0")) {
                Console.info("Até logo!");
                return;
            }
            try {
                switch (opcao) {
                    case "1" -> cadastrarUsuario();
                    case "2" -> Console.listar(usuarioService.listarTodos(), "Nenhum usuário cadastrado.");
                    case "3" -> editarUsuario();
                    case "4" -> excluirUsuario();
                    case "5" -> entrar();
                    case "6" -> Console.listar(livroService.buscarDisponiveis(null, null),
                            "Nenhum livro disponível no momento.");
                    default -> Console.erro("Opção inválida.");
                }
            } catch (ValidacaoException e) {
                Console.erro(e.getMessage());
            } catch (DaoException e) {
                Console.erro("Problema no banco de dados: " + e.getMessage());
            }
        }
    }

    private void cadastrarUsuario() {
        Console.titulo("Novo usuário");
        String nome = Console.lerTexto("Nome completo");
        String email = Console.lerTexto("E-mail");
        String telefone = Console.lerTexto("Telefone");
        String endereco = Console.lerTexto("Endereço");
        Console.info("Tipo de perfil:");
        int perfil = Console.escolher("Perfil", Opcoes.PERFIS);

        // A variável é do tipo Usuario, mas o objeto é Doador ou Solicitante (polimorfismo).
        Usuario usuario = perfil == 0
                ? new Doador(nome, email, telefone, endereco)
                : new Solicitante(nome, email, telefone, endereco);

        usuarioService.cadastrar(usuario);
        Console.sucesso("Usuário cadastrado com o id #" + usuario.getId() + " (" + usuario.getTipoPerfil() + ").");
    }

    private void editarUsuario() {
        Console.titulo("Editar usuário");
        int id = Console.lerInteiro("Id do usuário");
        Usuario usuario = usuarioService.buscarPorId(id);
        Console.info("Aperte Enter para manter o valor atual.");
        usuario.setNomeCompleto(Console.lerOuManter("Nome completo", usuario.getNomeCompleto()));
        usuario.setEmail(Console.lerOuManter("E-mail", usuario.getEmail()));
        usuario.setTelefone(Console.lerOuManter("Telefone", usuario.getTelefone()));
        usuario.setEndereco(Console.lerOuManter("Endereço", usuario.getEndereco()));
        usuarioService.atualizar(usuario);
        Console.sucesso("Usuário #" + id + " atualizado.");
    }

    private void excluirUsuario() {
        Console.titulo("Excluir usuário");
        int id = Console.lerInteiro("Id do usuário");
        Usuario usuario = usuarioService.buscarPorId(id);
        Console.info("  " + usuario);
        Console.info("Atenção: os livros e reservas deste usuário também serão excluídos.");
        if (Console.confirmar("Confirma a exclusão?")) {
            usuarioService.excluir(id);
            Console.sucesso("Usuário #" + id + " excluído.");
        } else {
            Console.info("Exclusão cancelada.");
        }
    }

    private void entrar() {
        String email = Console.lerTexto("E-mail");
        Optional<Usuario> encontrado = usuarioService.buscarPorEmail(email);
        if (encontrado.isEmpty()) {
            Console.erro("Nenhum usuário com esse e-mail. Cadastre-se pela opção 1.");
            return;
        }

        Usuario usuario = encontrado.get();
        if (usuario instanceof Doador doador) {
            new MenuDoador(livroService, reservaService, doador).abrir();
        } else if (usuario instanceof Solicitante solicitante) {
            new MenuSolicitante(livroService, reservaService, solicitante).abrir();
        }
    }
}
