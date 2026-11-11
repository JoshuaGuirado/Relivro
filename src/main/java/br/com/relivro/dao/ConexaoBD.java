package br.com.relivro.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fábrica de conexões JDBC com o PostgreSQL (local ou Supabase).
 *
 * A configuração é lida, nesta ordem de prioridade:
 *  1. Variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD;
 *  2. Arquivo db.properties na pasta onde o programa é executado
 *     (chaves db.url, db.user e db.password).
 *
 * O db.properties está no .gitignore para que a senha nunca vá para o GitHub.
 */
public final class ConexaoBD {

    private static final String ARQUIVO_CONFIG = "db.properties";

    private static String url;
    private static String usuario;
    private static String senha;

    private ConexaoBD() {
    }

    public static Connection obterConexao() throws SQLException {
        carregarConfiguracao();
        return DriverManager.getConnection(url, usuario, senha);
    }

    /** Abre e fecha uma conexão só para verificar se o banco está acessível. */
    public static void testarConexao() throws SQLException {
        try (Connection conexao = obterConexao()) {
            conexao.isValid(5);
        }
    }

    public static String descreverDestino() {
        carregarConfiguracao();
        return url;
    }

    private static synchronized void carregarConfiguracao() {
        if (url != null) {
            return;
        }

        Properties props = new Properties();
        Path arquivo = Path.of(ARQUIVO_CONFIG);
        if (Files.exists(arquivo)) {
            try (InputStream entrada = Files.newInputStream(arquivo)) {
                props.load(entrada);
            } catch (IOException e) {
                throw new DaoException("Não foi possível ler o arquivo " + arquivo.toAbsolutePath(), e);
            }
        }

        String urlLida = valor("DB_URL", props.getProperty("db.url"));
        String usuarioLido = valor("DB_USER", props.getProperty("db.user"));
        String senhaLida = valor("DB_PASSWORD", props.getProperty("db.password"));

        if (urlLida == null || urlLida.isBlank()) {
            throw new DaoException("Configuração do banco não encontrada. Crie o arquivo db.properties "
                    + "(copie de db.properties.example) ou defina as variáveis DB_URL, DB_USER e DB_PASSWORD.");
        }

        url = urlLida.trim();
        usuario = usuarioLido == null ? "" : usuarioLido.trim();
        senha = senhaLida == null ? "" : senhaLida;
    }

    private static String valor(String variavelAmbiente, String valorArquivo) {
        String doAmbiente = System.getenv(variavelAmbiente);
        return (doAmbiente != null && !doAmbiente.isBlank()) ? doAmbiente : valorArquivo;
    }
}
