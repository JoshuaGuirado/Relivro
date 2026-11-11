package br.com.relivro.view;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Utilitário de leitura e escrita no terminal.
 *
 * Usa a codificação do próprio terminal para que acentos funcionem
 * tanto no Windows (CMD/PowerShell) quanto no Linux e no macOS.
 */
public final class Console {

    private static final Scanner ENTRADA;

    static {
        java.io.Console terminal = System.console();
        Charset charset = terminal != null ? terminal.charset() : Charset.defaultCharset();
        if (terminal != null) {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, charset));
        }
        ENTRADA = new Scanner(System.in, charset);
    }

    private Console() {
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println("---- " + texto + " ----");
    }

    public static void sucesso(String texto) {
        System.out.println("[OK] " + texto);
    }

    public static void erro(String texto) {
        System.out.println("[ERRO] " + texto);
    }

    public static void info(String texto) {
        System.out.println(texto);
    }

    /** Lê uma linha. Encerra o programa de forma limpa se a entrada acabar (Ctrl+D / Ctrl+Z). */
    public static String lerLinha(String rotulo) {
        System.out.print(rotulo);
        System.out.flush();
        try {
            return ENTRADA.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Entrada encerrada. Até logo!");
            System.exit(0);
            return "";
        }
    }

    /** Lê um texto obrigatório (repete a pergunta se vier vazio). */
    public static String lerTexto(String rotulo) {
        while (true) {
            String valor = lerLinha(rotulo + ": ").trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            erro("Campo obrigatório.");
        }
    }

    /** Lê um texto opcional. Retorna null se o usuário só apertar Enter. */
    public static String lerOpcional(String rotulo) {
        String valor = lerLinha(rotulo + " (Enter para pular): ").trim();
        return valor.isEmpty() ? null : valor;
    }

    /** Usado na edição: Enter mantém o valor atual. */
    public static String lerOuManter(String rotulo, String valorAtual) {
        String valor = lerLinha(rotulo + " [" + valorAtual + "]: ").trim();
        return valor.isEmpty() ? valorAtual : valor;
    }

    public static int lerInteiro(String rotulo) {
        while (true) {
            String valor = lerLinha(rotulo + ": ").trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                erro("Digite um número inteiro.");
            }
        }
    }

    /** Mostra opções numeradas (1..n) e devolve o índice escolhido (0..n-1). */
    public static int escolher(String rotulo, List<String> opcoes) {
        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opcoes.get(i));
        }
        while (true) {
            int escolha = lerInteiro(rotulo);
            if (escolha >= 1 && escolha <= opcoes.size()) {
                return escolha - 1;
            }
            erro("Opção inválida.");
        }
    }

    public static boolean confirmar(String pergunta) {
        String resposta = lerLinha(pergunta + " (s/n): ").trim().toLowerCase();
        return resposta.equals("s") || resposta.equals("sim");
    }

    public static <T> void listar(List<T> itens, String mensagemVazia) {
        if (itens.isEmpty()) {
            info(mensagemVazia);
            return;
        }
        itens.forEach(item -> System.out.println("  " + item));
        info("  (" + itens.size() + " registro(s))");
    }
}
