package br.edu.ucsal.pooalab.gestaohoras.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtividadeComplementarTest {
    private final Categoria categoria = new Categoria(null, "Pesquisa", BigDecimal.TEN);

    @Test
    void validaCamposObrigatoriosECargaHorariaPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new AtividadeComplementar(null, " ", LocalDate.now(), BigDecimal.ONE, categoria));
        assertThrows(IllegalArgumentException.class,
                () -> new AtividadeComplementar(null, "Artigo", null, BigDecimal.ONE, categoria));
        assertThrows(IllegalArgumentException.class,
                () -> new AtividadeComplementar(null, "Artigo", LocalDate.now(), BigDecimal.ZERO, categoria));
        assertThrows(IllegalArgumentException.class,
                () -> new AtividadeComplementar(null, "Artigo", LocalDate.now(), BigDecimal.ONE, null));
    }

    @Test
    void removeEspacosDoTitulo() {
        AtividadeComplementar atividade = new AtividadeComplementar(
                null, " Artigo ", LocalDate.of(2026, 10, 30), BigDecimal.valueOf(4), categoria);

        assertEquals("Artigo", atividade.getTitulo());
    }
}
