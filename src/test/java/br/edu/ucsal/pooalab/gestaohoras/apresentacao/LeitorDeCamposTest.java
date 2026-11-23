package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeitorDeCamposTest {

    @Test
    void leDataNoFormatoBrasileiroEnoFormatoIsoDoTerminal() {
        assertEquals(LocalDate.of(2026, 10, 24), LeitorDeCampos.lerData("24/10/2026"));
        assertEquals(LocalDate.of(2026, 10, 24), LeitorDeCampos.lerData("2026-10-24"));
        assertEquals(LocalDate.of(2026, 2, 28), LeitorDeCampos.lerData("  28/02/2026  "));
    }

    @Test
    void campoVazioOuNuloViraNullParaOdominioDecidir() {
        assertNull(LeitorDeCampos.lerData(null));
        assertNull(LeitorDeCampos.lerData("   "));
        assertNull(LeitorDeCampos.lerCargaHoraria(null));
        assertNull(LeitorDeCampos.lerCargaHoraria(""));
    }

    @Test
    void recusaDatasImpossiveisOuMalFormatadas() {
        for (String invalida : new String[]{
                "31/02/2026", "32/01/2026", "24/13/2026", "24-10-2026", "24/10/26", "ontem"}) {
            IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                    () -> LeitorDeCampos.lerData(invalida), "deveria recusar: " + invalida);
            assertTrue(erro.getMessage().contains("data válida"), erro.getMessage());
        }
    }

    @Test
    void leCargaHorariaComVirgulaOuPonto() {
        assertEquals(0, new BigDecimal("2.5").compareTo(LeitorDeCampos.lerCargaHoraria("2,5")));
        assertEquals(0, new BigDecimal("2.5").compareTo(LeitorDeCampos.lerCargaHoraria("2.5")));
        assertEquals(0, new BigDecimal("4").compareTo(LeitorDeCampos.lerCargaHoraria(" 4 ")));
    }

    @Test
    void recusaCargaHorariaNaoNumerica() {
        for (String invalida : new String[]{"abc", "4h", "1.000,5", "2,5,1"}) {
            IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                    () -> LeitorDeCampos.lerCargaHoraria(invalida), "deveria recusar: " + invalida);
            assertTrue(erro.getMessage().contains("numérica"), erro.getMessage());
        }
    }

    @Test
    void leitorNaoDecideRegrasDeNegocio() {
        // Zero e negativo são números válidos; quem os recusa é o domínio (carga > 0).
        assertEquals(0, BigDecimal.ZERO.compareTo(LeitorDeCampos.lerCargaHoraria("0")));
        assertEquals(0, new BigDecimal("-3").compareTo(LeitorDeCampos.lerCargaHoraria("-3")));
    }

    @Test
    void formataDataEHorasParaExibicao() {
        assertEquals("24/10/2026", LeitorDeCampos.formatarData(LocalDate.of(2026, 10, 24)));
        assertEquals("4", LeitorDeCampos.formatarHoras(new BigDecimal("4.00")));
        assertEquals("4.5", LeitorDeCampos.formatarHoras(new BigDecimal("4.50")));
        assertEquals("100", LeitorDeCampos.formatarHoras(new BigDecimal("1E+2")));
    }

    @Test
    void textoFormatadoPodeSerLidoDeNovo() {
        LocalDate data = LocalDate.of(2026, 1, 5);
        BigDecimal horas = new BigDecimal("12.5");

        assertEquals(data, LeitorDeCampos.lerData(LeitorDeCampos.formatarData(data)));
        assertEquals(0, horas.compareTo(
                LeitorDeCampos.lerCargaHoraria(LeitorDeCampos.formatarHoras(horas))));
    }
}
