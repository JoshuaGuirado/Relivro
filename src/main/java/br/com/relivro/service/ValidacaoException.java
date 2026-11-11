package br.com.relivro.service;

/**
 * Erro de regra de negócio ou de dado inválido informado pelo usuário.
 */
public class ValidacaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
