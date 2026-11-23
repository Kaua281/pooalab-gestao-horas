package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Converte o texto digitado nos campos da interface em tipos do domínio (e o caminho inverso).
 *
 * <p>Esta classe só traduz formatos. Ela não decide regras de negócio: campo vazio resulta em
 * {@code null}, e quem decide se o valor é obrigatório ou aceitável é o domínio.
 */
public final class LeitorDeCampos {
    // STRICT recusa datas impossíveis (31/02/2026) em vez de "arredondá-las" para 28/02/2026.
    private static final DateTimeFormatter DATA_BR =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private LeitorDeCampos() {
    }

    /** Aceita dd/mm/aaaa e também aaaa-mm-dd (o formato usado no terminal). Vazio vira null. */
    public static LocalDate lerData(String texto) {
        String limpo = texto == null ? "" : texto.trim();
        if (limpo.isEmpty()) {
            return null;
        }
        try {
            return limpo.contains("/") ? LocalDate.parse(limpo, DATA_BR) : LocalDate.parse(limpo);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Informe uma data válida, por exemplo 24/10/2026.");
        }
    }

    /** Aceita vírgula ou ponto como separador decimal (2,5 ou 2.5). Vazio vira null. */
    public static BigDecimal lerCargaHoraria(String texto) {
        String limpo = texto == null ? "" : texto.trim().replace(',', '.');
        if (limpo.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Informe uma carga horária numérica, por exemplo 4 ou 2,5.");
        }
    }

    public static String formatarData(LocalDate data) {
        return data.format(DATA_BR);
    }

    /** Mostra 4 em vez de 4.00 e 100 em vez de 1E+2. */
    public static String formatarHoras(BigDecimal horas) {
        return horas.stripTrailingZeros().toPlainString();
    }
}
