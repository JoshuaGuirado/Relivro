package br.com.relivro.model;

/**
 * Classe abstrata base para os perfis do sistema (Doador e Solicitante).
 *
 * Pilares de POO:
 *  - Abstração: não existe "usuário genérico"; só as subclasses podem ser instanciadas.
 *  - Encapsulamento: atributos privados, acesso por getters/setters.
 *  - Polimorfismo: exibirPainel() e getTipoPerfil() são implementados de forma
 *    diferente por cada subclasse (@Override).
 */
public abstract class Usuario {

    private int id;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String endereco;

    protected Usuario(String nomeCompleto, String email, String telefone, String endereco) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    protected Usuario(int id, String nomeCompleto, String email, String telefone, String endereco) {
        this(nomeCompleto, email, telefone, endereco);
        this.id = id;
    }

    /** Cada perfil exibe um painel diferente (polimorfismo). */
    public abstract void exibirPainel();

    /** Valor gravado na coluna usuario.tipo_perfil ("doador" ou "solicitante"). */
    public abstract String getTipoPerfil();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return String.format("#%d | %-11s | %s | %s | %s | %s",
                id, getTipoPerfil(), nomeCompleto, email, telefone, endereco);
    }
}
