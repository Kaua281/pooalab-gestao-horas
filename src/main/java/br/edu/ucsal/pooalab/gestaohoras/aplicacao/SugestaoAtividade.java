package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SugestaoAtividade(String titulo, LocalDate data, BigDecimal cargaHoraria) {
}
