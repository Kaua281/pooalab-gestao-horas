package br.edu.ucsal.pooalab.gestaohoras.dominio;

import java.math.BigDecimal;

public class Categoria {
    private final Long id;
    private final String nome;
    private final BigDecimal metaHoras;

    public Categoria(Long id, String nome, BigDecimal metaHoras) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }
        if (metaHoras == null || metaHoras.signum() <= 0) {
            throw new IllegalArgumentException("A meta de horas deve ser maior que zero.");
        }

        this.id = id;
        this.nome = nome.trim();
        this.metaHoras = metaHoras;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getMetaHoras() {
        return metaHoras;
    }
}
