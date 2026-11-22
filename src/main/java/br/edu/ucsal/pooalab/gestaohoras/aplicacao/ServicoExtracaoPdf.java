package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServicoExtracaoPdf {
    private static final Pattern TITULO = Pattern.compile(
            "(?:Título|Atividade|Certificado de)\\s*[:\\-]\\s*(.+)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA = Pattern.compile(
            "(\\d{2}[/-]\\d{2}[/-]\\d{4}|\\d{4}[/-]\\d{2}[/-]\\d{2})");
    private static final Pattern HORAS = Pattern.compile(
            "(?:Carga(?: horária)?|Horas|Duração)\\s*[:\\-]?\\s*(\\d+(?:[,.]\\d+)?)",
            Pattern.CASE_INSENSITIVE);

    private final ExtratorTextoPdf extrator;

    public ServicoExtracaoPdf(ExtratorTextoPdf extrator) {
        this.extrator = Objects.requireNonNull(extrator, "O extrator é obrigatório.");
    }

    public String lerTexto(Path arquivo) {
        return extrator.extrairTexto(arquivo);
    }

    public SugestaoAtividade sugerirDados(Path arquivo) {
        return sugerirDadosDoTexto(lerTexto(arquivo));
    }

    SugestaoAtividade sugerirDadosDoTexto(String texto) {
        String titulo = buscarTitulo(texto);
        LocalDate data = buscarData(texto);
        BigDecimal carga = buscarCarga(texto);
        return new SugestaoAtividade(titulo, data, carga);
    }

    private String buscarTitulo(String texto) {
        Matcher titulo = TITULO.matcher(texto);
        if (titulo.find()) {
            return titulo.group(1).trim();
        }
        return null;
    }

    private LocalDate buscarData(String texto) {
        Matcher matcher = DATA.matcher(texto);
        if (!matcher.find()) {
            return null;
        }
        String valor = matcher.group(1).replace('/', '-');
        try {
            if (valor.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(valor, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            return LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private BigDecimal buscarCarga(String texto) {
        Matcher matcher = HORAS.matcher(texto);
        if (!matcher.find()) {
            return null;
        }
        try {
            BigDecimal carga = new BigDecimal(matcher.group(1).replace(",", "."));
            return carga.signum() > 0 ? carga : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
