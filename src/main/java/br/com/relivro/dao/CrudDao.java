package br.com.relivro.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica de CRUD (Create, Read, Update, Delete).
 * Cada DAO implementa estes métodos com @Override para a sua tabela.
 *
 * @param <T> tipo da entidade (Usuario, Livro, Reserva)
 */
public interface CrudDao<T> {

    /** Create: insere a entidade e devolve-a com o id gerado pelo banco. */
    T inserir(T entidade);

    /** Read: busca uma entidade pelo id. */
    Optional<T> buscarPorId(int id);

    /** Read: lista todas as entidades. */
    List<T> listarTodos();

    /** Update: atualiza os dados. Retorna true se alguma linha foi alterada. */
    boolean atualizar(T entidade);

    /** Delete: exclui pelo id. Retorna true se alguma linha foi removida. */
    boolean excluir(int id);
}
