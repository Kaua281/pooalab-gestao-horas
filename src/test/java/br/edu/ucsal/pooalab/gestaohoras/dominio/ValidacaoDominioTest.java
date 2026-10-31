package br.edu.ucsal.pooalab.gestaohoras.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacaoDominioTest {
    @Test
    void categoriaExigeNomeEMetaPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new Categoria(null, " ", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new Categoria(null, "Pesquisa", BigDecimal.ZERO));
        assertDoesNotThrow(() -> new Categoria(null, " Pesquisa ", BigDecimal.ONE));
    }
}
