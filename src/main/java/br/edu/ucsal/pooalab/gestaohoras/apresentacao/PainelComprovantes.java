package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ExtratorPdfBox;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ServicoExtracaoPdf;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.SugestaoAtividade;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class PainelComprovantes extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL = new Color(67, 97, 238);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ServicoExtracaoPdf servicoPdf =
            new ServicoExtracaoPdf(new ExtratorPdfBox());
    private final AtividadeService atividadeService;
    private final CategoriaService categoriaService;
    private final Runnable atualizarPainelInicial;

    private JLabel arquivoSelecionado;
    private JTextArea textoPdf;
    private JTextField campoTitulo;
    private JTextField campoData;
    private JTextField campoCargaHoraria;
    private JComboBox<CategoriaOpcao> campoCategoria;
    private Path arquivoAtual;
    private Path arquivoImportado;

    public PainelComprovantes(
            AtividadeService atividadeService,
            CategoriaService categoriaService,
            Runnable atualizarPainelInicial) {
        this.atividadeService = atividadeService;
        this.categoriaService = categoriaService;
        this.atualizarPainelInicial = atualizarPainelInicial;
        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(26, 30, 30, 30));
        add(criarConteudo(), BorderLayout.CENTER);
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(FUNDO);

        JLabel titulo = new JLabel("Importar Comprovante");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO);
        JLabel descricao = new JLabel(
                "Selecione um PDF, confira os dados encontrados e adicione a atividade.");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descricao.setForeground(SUAVE);
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(7));
        conteudo.add(descricao);
        conteudo.add(Box.createVerticalStrut(22));

        JPanel selecao = criarCartao();
        selecao.setLayout(new BorderLayout(0, 14));
        arquivoSelecionado = new JLabel("Nenhum PDF selecionado.");
        arquivoSelecionado.setFont(new Font("SansSerif", Font.BOLD, 13));
        arquivoSelecionado.setForeground(TEXTO);
        selecao.add(arquivoSelecionado, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setBackground(Color.WHITE);
        JButton selecionar = criarBotao("Selecionar PDF", AZUL);
        JButton importar = criarBotao("Importar cópia", new Color(36, 150, 110));
        JButton abrir = criarBotao("Abrir PDF", new Color(112, 124, 145));
        selecionar.addActionListener(event -> selecionarPdf());
        importar.addActionListener(event -> importarPdf());
        abrir.addActionListener(event -> abrirPdf());
        botoes.add(selecionar);
        botoes.add(importar);
        botoes.add(abrir);
        selecao.add(botoes, BorderLayout.CENTER);
        conteudo.add(selecao);
        conteudo.add(Box.createVerticalStrut(22));

        JPanel resultados = new JPanel(new GridLayout(1, 2, 16, 0));
        resultados.setOpaque(false);
        resultados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 390));
        resultados.add(criarPainelTexto());
        resultados.add(criarFormularioAtividade());
        conteudo.add(resultados);
        return conteudo;
    }

    private JPanel criarPainelTexto() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 12));
        JLabel rotulo = new JLabel("Texto extraído do PDF");
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        rotulo.setForeground(TEXTO);
        textoPdf = new JTextArea();
        textoPdf.setEditable(false);
        textoPdf.setLineWrap(true);
        textoPdf.setWrapStyleWord(true);
        textoPdf.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textoPdf.setForeground(TEXTO);
        textoPdf.setBorder(new EmptyBorder(8, 8, 8, 8));
        textoPdf.setText("Nenhum texto extraído.");
        JScrollPane rolagem = new JScrollPane(textoPdf);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.setPreferredSize(new Dimension(400, 300));
        cartao.add(rotulo, BorderLayout.NORTH);
        cartao.add(rolagem, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarFormularioAtividade() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 14));

        JLabel titulo = new JLabel("Dados da atividade");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(TEXTO);
        cartao.add(titulo, BorderLayout.NORTH);

        campoTitulo = new JTextField();
        campoData = new JTextField(LocalDate.now().format(FORMATO_DATA));
        campoCargaHoraria = new JTextField();
        campoCategoria = new JComboBox<>();
        campoCategoria.addItem(new CategoriaOpcao(null, "Sem categoria"));
        for (Categoria categoria : categoriaService.listar()) {
            campoCategoria.addItem(
                    new CategoriaOpcao(categoria.getId(), categoria.getNome()));
        }

        JPanel campos = new JPanel(new GridLayout(2, 2, 12, 12));
        campos.setBackground(Color.WHITE);
        campos.add(criarCampo("Título da atividade", campoTitulo));
        campos.add(criarCampo("Data (DD/MM/AAAA)", campoData));
        campos.add(criarCampo("Carga horária", campoCargaHoraria));
        campos.add(criarCampoCategoria("Categoria", campoCategoria));
        cartao.add(campos, BorderLayout.CENTER);

        JButton adicionar = criarBotao("Adicionar atividade", AZUL);
        adicionar.addActionListener(event -> adicionarAtividade());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setBackground(Color.WHITE);
        rodape.add(adicionar);
        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    private JPanel criarCampo(String titulo, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 7));
        painel.setBackground(Color.WHITE);
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);
        campo.setPreferredSize(new Dimension(180, 38));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(5, 8, 5, 8)));
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarCampoCategoria(
            String titulo, JComboBox<CategoriaOpcao> campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 7));
        painel.setBackground(Color.WHITE);
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);
        campo.setPreferredSize(new Dimension(180, 38));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBackground(Color.WHITE);
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarCartao() {
        JPanel cartao = new JPanel();
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(20, 20, 20, 20)));
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cartao;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(11, 16, 11, 16));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void selecionarPdf() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecionar comprovante em PDF");
        seletor.setAcceptAllFileFilterUsed(false);
        seletor.setFileFilter(new FileNameExtensionFilter(
                "Arquivos PDF", "pdf"));
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path selecionado = seletor.getSelectedFile().toPath();
        try {
            String texto = servicoPdf.lerTexto(selecionado);
            SugestaoAtividade sugestao = servicoPdf.sugerirDados(selecionado);
            arquivoAtual = selecionado;
            arquivoSelecionado.setText(selecionado.getFileName().toString());
            textoPdf.setText(texto);
            preencherFormulario(sugestao);
        } catch (RuntimeException erro) {
            arquivoAtual = null;
            arquivoSelecionado.setText("Nenhum PDF selecionado.");
            textoPdf.setText("Não foi possível extrair o texto.");
            limparFormulario();
            JOptionPane.showMessageDialog(
                    this,
                    erro.getMessage(),
                    "Erro ao ler PDF",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario(SugestaoAtividade sugestao) {
        campoTitulo.setText(sugestao.titulo() == null ? "" : sugestao.titulo());
        campoData.setText(sugestao.data() == null
                ? LocalDate.now().format(FORMATO_DATA)
                : sugestao.data().format(FORMATO_DATA));
        campoCargaHoraria.setText(sugestao.cargaHoraria() == null
                ? ""
                : sugestao.cargaHoraria().stripTrailingZeros().toPlainString());
    }

    private void limparFormulario() {
        campoTitulo.setText("");
        campoData.setText(LocalDate.now().format(FORMATO_DATA));
        campoCargaHoraria.setText("");
        campoCategoria.setSelectedIndex(0);
    }

    private void adicionarAtividade() {
        if (arquivoAtual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um PDF antes de adicionar a atividade.");
            return;
        }
        try {
            LocalDate data = LocalDate.parse(
                    campoData.getText().trim(), FORMATO_DATA);
            String cargaTexto = campoCargaHoraria.getText().trim().replace(",", ".");
            BigDecimal carga = new BigDecimal(cargaTexto);
            CategoriaOpcao categoria = (CategoriaOpcao)
                    campoCategoria.getSelectedItem();

            importarPdfSilenciosamente();
            atividadeService.cadastrar(
                    campoTitulo.getText(),
                    data,
                    carga,
                    categoria == null ? null : categoria.id());
            atualizarPainelInicial.run();
            limparFormulario();
            JOptionPane.showMessageDialog(
                    this,
                    "Atividade adicionada à lista com sucesso!");
        } catch (DateTimeParseException erro) {
            mostrarErro("Informe a data no formato DD/MM/AAAA.");
        } catch (NumberFormatException erro) {
            mostrarErro("Informe uma carga horária válida.");
        } catch (IOException erro) {
            mostrarErro("Não foi possível importar o comprovante.");
        } catch (RuntimeException erro) {
            mostrarErro(erro.getMessage());
        }
    }

    private void importarPdf() {
        if (arquivoAtual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um PDF antes de importar.");
            return;
        }
        try {
            importarPdfSilenciosamente();
            JOptionPane.showMessageDialog(
                    this,
                    "Comprovante importado com sucesso!\n" + arquivoImportado);
        } catch (IOException erro) {
            mostrarErro("Não foi possível importar o comprovante.");
        }
    }

    private void importarPdfSilenciosamente() throws IOException {
        Path diretorio = Path.of(
                System.getProperty("user.home"), ".gestao-horas", "comprovantes");
        Files.createDirectories(diretorio);
        Path destino = diretorio.resolve(arquivoAtual.getFileName());
        Files.copy(arquivoAtual, destino, StandardCopyOption.REPLACE_EXISTING);
        arquivoImportado = destino;
    }

    private void abrirPdf() {
        Path arquivo = arquivoImportado != null ? arquivoImportado : arquivoAtual;
        if (arquivo == null) {
            JOptionPane.showMessageDialog(this, "Selecione um PDF antes de abrir.");
            return;
        }
        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(
                    this,
                    "A abertura automática não é suportada neste sistema.");
            return;
        }
        try {
            Desktop.getDesktop().open(arquivo.toFile());
        } catch (IOException erro) {
            mostrarErro("Não foi possível abrir o PDF.");
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    private record CategoriaOpcao(Long id, String nome) {
        @Override
        public String toString() {
            return nome;
        }
    }
}
