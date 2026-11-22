package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServicoExtracaoPdfTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void extraiSugestoesDeCertificadoGenerico() throws Exception {
        Path pdf = pastaTemporaria.resolve("certificado-generico.pdf");
        criarPdf(pdf);

        ServicoExtracaoPdf servico = new ServicoExtracaoPdf(new ExtratorPdfBox());
        SugestaoAtividade sugestao = servico.sugerirDados(pdf);

        assertEquals("Oficina de Pesquisa Acadêmica", sugestao.titulo());
        assertEquals(LocalDate.of(2026, 10, 15), sugestao.data());
        assertEquals(0, new BigDecimal("8").compareTo(sugestao.cargaHoraria()));

        Path destino = Path.of("docs", "comprovantes", "certificado-generico.pdf");
        Files.createDirectories(destino.getParent());
        Files.copy(pdf, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void naoInventaDadosQuandoPdfNaoContemInformacoes() {
        ServicoExtracaoPdf servico = new ServicoExtracaoPdf(new ExtratorPdfBox());

        SugestaoAtividade sugestao = servico.sugerirDadosDoTexto(
                "CERTIFICADO DE PARTICIPAÇÃO\nParticipante fictício");

        assertNull(sugestao.titulo());
        assertNull(sugestao.data());
        assertNull(sugestao.cargaHoraria());
    }

    private void criarPdf(Path destino) throws Exception {
        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            try (PDPageContentStream conteudo =
                         new PDPageContentStream(documento, pagina)) {
                conteudo.beginText();
                conteudo.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                conteudo.newLineAtOffset(70, 720);
                conteudo.showText("CERTIFICADO DE PARTICIPAÇÃO");
                conteudo.newLineAtOffset(0, -40);
                conteudo.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                conteudo.showText("Atividade: Oficina de Pesquisa Acadêmica");
                conteudo.newLineAtOffset(0, -25);
                conteudo.showText("Participante: Estudante de Teste");
                conteudo.newLineAtOffset(0, -25);
                conteudo.showText("Data: 15/10/2026");
                conteudo.newLineAtOffset(0, -25);
                conteudo.showText("Carga horária: 8 horas");
                conteudo.endText();
            }
            documento.save(destino.toFile());
        }
    }
}
