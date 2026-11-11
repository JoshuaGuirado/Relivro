package br.com.relivro.service;

import br.com.relivro.dao.UsuarioDAO;
import br.com.relivro.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regras de negócio de usuários (RF1).
 */
public class UsuarioService {

    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(Usuario usuario) {
        validar(usuario);
        if (usuarioDAO.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new ValidacaoException("Já existe um usuário com o e-mail " + usuario.getEmail() + ".");
        }
        return usuarioDAO.inserir(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Usuário #" + id + " não encontrado."));
    }

    /** Usado para "entrar" no sistema pelo e-mail. */
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorEmail(email.trim());
    }

    public void atualizar(Usuario usuario) {
        validar(usuario);
        usuarioDAO.buscarPorEmail(usuario.getEmail())
                .filter(outro -> outro.getId() != usuario.getId())
                .ifPresent(outro -> {
                    throw new ValidacaoException("O e-mail " + usuario.getEmail() + " já pertence a outro usuário.");
                });
        if (!usuarioDAO.atualizar(usuario)) {
            throw new ValidacaoException("Usuário #" + usuario.getId() + " não encontrado.");
        }
    }

    /** Exclui o usuário. Livros e reservas ligados a ele são removidos pelo ON DELETE CASCADE. */
    public void excluir(int id) {
        if (!usuarioDAO.excluir(id)) {
            throw new ValidacaoException("Usuário #" + id + " não encontrado.");
        }
    }

    private void validar(Usuario usuario) {
        obrigatorio(usuario.getNomeCompleto(), "Nome completo");
        obrigatorio(usuario.getEmail(), "E-mail");
        obrigatorio(usuario.getTelefone(), "Telefone");
        obrigatorio(usuario.getEndereco(), "Endereço");

        usuario.setNomeCompleto(usuario.getNomeCompleto().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setTelefone(usuario.getTelefone().trim());
        usuario.setEndereco(usuario.getEndereco().trim());

        if (!EMAIL_VALIDO.matcher(usuario.getEmail()).matches()) {
            throw new ValidacaoException("E-mail inválido: " + usuario.getEmail());
        }
        limite(usuario.getNomeCompleto(), 150, "Nome completo");
        limite(usuario.getEmail(), 150, "E-mail");
        limite(usuario.getTelefone(), 20, "Telefone");
        limite(usuario.getEndereco(), 200, "Endereço");
    }

    static void obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(campo + " é obrigatório.");
        }
    }

    static void limite(String valor, int maximo, String campo) {
        if (valor.length() > maximo) {
            throw new ValidacaoException(campo + " pode ter no máximo " + maximo + " caracteres.");
        }
    }
}
