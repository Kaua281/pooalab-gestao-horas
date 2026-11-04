package br.edu.ucsal.pooalab.gestaohoras.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumoCategoriaTest {

    private static Categoria categoria(String meta) {
        return new Categoria(1L, "Palestras", new BigDecimal(meta));
    }

    // compareTo ignora a escala: 30 e 30.00 valem o mesmo para estes testes.
    private static void assertHoras(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                "esperado " + esperado + " mas foi " + atual);
    }

    @Test
    void semAtividadesTodaAMetaFicaPendente() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("50"), BigDecimal.ZERO);

        assertHoras("0", resumo.horasRegistradas());
        assertHoras("50", resumo.horasRestantes());
        assertHoras("0", resumo.progressoPercentual());
    }

    @Test
    void calculaProgressoParcial() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("50"), new BigDecimal("20"));

        assertHoras("30", resumo.horasRestantes());
        assertHoras("40", resumo.progressoPercentual());
    }

    @Test
    void metaExatamenteAtingidaZeraRestantesEMostraCemPorCento() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("50"), new BigDecimal("50"));

        assertHoras("0", resumo.horasRestantes());
        assertHoras("100", resumo.progressoPercentual());
    }

    @Test
    void metaSuperadaNaoGeraRestantesNegativosNemProgressoAcimaDeCem() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("50"), new BigDecimal("65"));

        assertHoras("65", resumo.horasRegistradas()); // o total real continua visível
        assertHoras("0", resumo.horasRestantes());
        assertHoras("100", resumo.progressoPercentual());
    }

    @Test
    void aceitaHorasFracionadas() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("60"), new BigDecimal("2.5"));

        assertHoras("57.5", resumo.horasRestantes());
        assertHoras("4.16", resumo.progressoPercentual()); // 4,1666... truncado em 2 casas
    }

    @Test
    void nuncaMostraCemPorCentoAntesDeCumprirAMeta() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("1000"), new BigDecimal("999.99"));

        assertHoras("0.01", resumo.horasRestantes());
        assertHoras("99.99", resumo.progressoPercentual());
        assertTrue(resumo.progressoPercentual().compareTo(new BigDecimal("100")) < 0);
    }

    @Test
    void copiaIdentificacaoEMetaDaCategoria() {
        ResumoCategoria resumo = ResumoCategoria.calcular(categoria("50"), BigDecimal.ONE);

        assertEquals(1L, resumo.categoriaId());
        assertEquals("Palestras", resumo.nomeCategoria());
        assertHoras("50", resumo.metaHoras());
    }

    @Test
    void recusaEntradasInvalidas() {
        assertThrows(IllegalArgumentException.class,
                () -> ResumoCategoria.calcular(categoria("50"), new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
                () -> ResumoCategoria.calcular(categoria("50"), null));
        assertThrows(IllegalArgumentException.class,
                () -> ResumoCategoria.calcular(null, BigDecimal.ONE));
    }
}
