package br.com.relivro;

import br.com.relivro.dao.ConexaoBD;
import br.com.relivro.dao.DaoException;
import br.com.relivro.dao.LivroDAO;
import br.com.relivro.dao.ReservaDAO;
import br.com.relivro.dao.UsuarioDAO;
import br.com.relivro.service.LivroService;
import br.com.relivro.service.ReservaService;
import br.com.relivro.service.UsuarioService;
import br.com.relivro.view.Console;
import br.com.relivro.view.MenuPrincipal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ponto de entrada do RelivrO.
 * Monta as camadas (DAO -> Service -> View), testa o banco e abre o menu.
 */
public class Main {

    public static void main(String[] args) {
        Console.info("RelivrO - plataforma de doação e reaproveitamento de livros");
        Console.info("ODS 4 (Educação de Qualidade) e ODS 10 (Redução das Desigualdades)");

        if (!bancoPronto()) {
            System.exit(1);
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();
        ReservaDAO reservaDAO = new ReservaDAO();

        UsuarioService usuarioService = new UsuarioService(usuarioDAO);
        LivroService livroService = new LivroService(livroDAO, reservaDAO);
        ReservaService reservaService = new ReservaService(reservaDAO, livroDAO);

        new MenuPrincipal(usuarioService, livroService, reservaService).abrir();
    }

    /** Verifica a conexão e se as tabelas existem antes de abrir o menu. */
    private static boolean bancoPronto() {
        try {
            Console.info("Conectando ao banco: " + ConexaoBD.descreverDestino());
            try (Connection conexao = ConexaoBD.obterConexao();
                 Statement stmt = conexao.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM usuario LIMIT 1").close();
                stmt.executeQuery("SELECT 1 FROM livro LIMIT 1").close();
                stmt.executeQuery("SELECT 1 FROM reserva LIMIT 1").close();
            }
            Console.sucesso("Banco de dados conectado.");
            return true;
        } catch (DaoException e) {
            Console.erro(e.getMessage());
        } catch (SQLException e) {
            if ("42P01".equals(e.getSQLState())) {
                Console.erro("As tabelas não existem. Execute o arquivo database/script.sql no banco.");
            } else {
                Console.erro("Não foi possível conectar ao banco: " + e.getMessage());
                Console.info("Confira db.url, db.user e db.password no arquivo db.properties.");
            }
        }
        return false;
    }
}
