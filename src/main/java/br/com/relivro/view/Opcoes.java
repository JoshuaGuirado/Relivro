package br.com.relivro.view;

import java.util.List;

/**
 * Valores padronizados oferecidos nos menus.
 */
final class Opcoes {

    static final List<String> ESTADOS_CONSERVACAO = List.of(
            "Novo",
            "Seminovo",
            "Usado - bom estado",
            "Usado - desgastado");

    static final List<String> PERFIS = List.of(
            "Doador (quero doar livros)",
            "Solicitante (quero receber livros)");

    private Opcoes() {
    }
}
