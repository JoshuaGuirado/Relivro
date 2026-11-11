package br.com.relivro.dao;

/**
 * Erro de acesso ao banco de dados (encapsula a SQLException).
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public DaoException(String mensagem) {
        super(mensagem);
    }
}
