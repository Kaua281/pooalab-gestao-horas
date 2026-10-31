package br.edu.ucsal.pooalab.gestaohoras.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class AtividadeComplementar {
    private final Long id;
    private final String titulo;
    private final LocalDate data;
    private final BigDecimal cargaHoraria;
    private final Categoria categoria;

    public AtividadeComplementar(
            Long id, String titulo, LocalDate data, BigDecimal cargaHoraria, Categoria categoria) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da atividade é obrigatório.");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data da atividade é obrigatória.");
        }
        if (cargaHoraria == null || cargaHoraria.signum() <= 0) {
            throw new IllegalArgumentException("A carga horária deve ser maior que zero.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria da atividade é obrigatória.");
        }

        this.id = id;
        this.titulo = titulo.trim();
        this.data = data;
        this.cargaHoraria = cargaHoraria;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getCargaHoraria() {
        return cargaHoraria;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
