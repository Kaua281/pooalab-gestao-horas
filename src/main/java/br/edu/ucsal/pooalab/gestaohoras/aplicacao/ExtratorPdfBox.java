package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;

public final class ExtratorPdfBox implements ExtratorTextoPdf {
    @Override
    public String extrairTexto(Path arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("Selecione um arquivo PDF.");
        }
        if (!arquivo.toString().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Selecione um arquivo com extensão PDF.");
        }
        try (PDDocument documento = Loader.loadPDF(arquivo.toFile())) {
            String texto = new PDFTextStripper().getText(documento);
            if (texto == null || texto.isBlank()) {
                throw new IllegalArgumentException(
                        "O PDF não possui texto selecionável.");
            }
            return texto;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível ler o arquivo PDF.", exception);
        }
    }
}
