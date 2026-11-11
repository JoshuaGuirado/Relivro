package br.com.relivro.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Pedido de reserva que liga um Solicitante a um Livro (RF4).
 */
public class Reserva {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private Livro livro;
    private Solicitante solicitante;
    private LocalDate dataReserva;

    public Reserva(Livro livro, Solicitante solicitante, LocalDate dataReserva) {
        this.livro = livro;
        this.solicitante = solicitante;
        this.dataReserva = dataReserva;
    }

    public Reserva(int id, Livro livro, Solicitante solicitante, LocalDate dataReserva) {
        this(livro, solicitante, dataReserva);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    @Override
    public String toString() {
        return String.format("Reserva #%d | %s | Livro: \"%s\" (#%d, %s) | Solicitante: %s (%s, %s)",
                id, dataReserva.format(FORMATO_DATA),
                livro.getTitulo(), livro.getId(), livro.getStatus(),
                solicitante.getNomeCompleto(), solicitante.getEmail(), solicitante.getTelefone());
    }
}
