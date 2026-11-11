package br.com.relivro.model;

/**
 * Status possíveis de um livro no processo de doação (RF5).
 * Cada constante guarda o valor exato usado na coluna livro.status do banco.
 */
public enum StatusLivro {

    DISPONIVEL("disponivel"),
    RESERVADO("reservado"),
    DOADO("doado");

    private final String valorBanco;

    StatusLivro(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    /** Converte o texto gravado no banco de volta para o enum. */
    public static StatusLivro fromValorBanco(String valor) {
        for (StatusLivro status : values()) {
            if (status.valorBanco.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de livro desconhecido: " + valor);
    }

    @Override
    public String toString() {
        return valorBanco;
    }
}
