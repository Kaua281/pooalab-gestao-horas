package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.DadosPainelInicial;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PainelInicial {
    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL_ESCURO = new Color(24, 39, 68);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color TEXTO_SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);

    private final Supplier<DadosPainelInicial> carregarDados;
    private final Consumer<List<Long>> salvarOrdemCategorias;
    private final CategoriaService categoriaService;
    private final Map<String, JButton> botoesNavegacao = new LinkedHashMap<>();
    private JFrame janela;
    private JPanel conteudo;
    private JScrollPane painelGeral;

    public PainelInicial(Supplier<DadosPainelInicial> carregarDados,
            Consumer<List<Long>> salvarOrdemCategorias,
            CategoriaService categoriaService) {
        this.carregarDados = carregarDados;
        this.salvarOrdemCategorias = salvarOrdemCategorias;
        this.categoriaService = categoriaService;
    }

    public void mostrar() {
        janela = new JFrame("Gestão de Horas");
        conteudo = new JPanel(new BorderLayout());
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setMinimumSize(new Dimension(980, 640));
        janela.setSize(1280, 820);
        janela.setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarNavegacao(), BorderLayout.WEST);
        raiz.add(conteudo, BorderLayout.CENTER);
        janela.setContentPane(raiz);
        mostrarSecao("Visão geral");
        janela.setVisible(true);
    }

    private JPanel criarNavegacao() {
        JPanel navegacao = new JPanel(new BorderLayout());
        navegacao.setBackground(AZUL_ESCURO);
        navegacao.setPreferredSize(new Dimension(232, 0));
        navegacao.setBorder(BorderFactory.createEmptyBorder(28, 16, 20, 16));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new javax.swing.BoxLayout(topo, javax.swing.BoxLayout.Y_AXIS));
        topo.add(criarMarca());
        topo.add(javax.swing.Box.createVerticalStrut(38));

        JLabel tituloMenu = rotulo("MENU PRINCIPAL", new Color(144, 160, 188), 10, Font.BOLD);
        tituloMenu.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        topo.add(tituloMenu);

        JPanel botoes = new JPanel(new GridLayout(0, 1, 0, 8));
        botoes.setOpaque(false);
        for (String secao : List.of("Visão geral", "Atividades", "Categorias", "Comprovantes")) {
            JButton botao = criarBotaoNavegacao(secao);
            botoesNavegacao.put(secao, botao);
            botoes.add(botao);
        }
        topo.add(botoes);
        navegacao.add(topo, BorderLayout.NORTH);
        navegacao.add(criarRodape(), BorderLayout.SOUTH);
        return navegacao;
    }

    private JPanel criarMarca() {
        JPanel identidade = new JPanel(new BorderLayout(12, 0));
        identidade.setOpaque(false);
        JLabel monograma = rotulo("H", Color.WHITE, 20, Font.BOLD);
        monograma.setOpaque(true);
        monograma.setBackground(new Color(67, 97, 238));
        monograma.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        identidade.add(monograma, BorderLayout.WEST);

        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new javax.swing.BoxLayout(marca, javax.swing.BoxLayout.Y_AXIS));
        marca.add(rotulo("HORAS+", Color.WHITE, 19, Font.BOLD));
        marca.add(javax.swing.Box.createVerticalStrut(3));
        marca.add(rotulo("GESTÃO ACADÊMICA", new Color(174, 187, 211), 9, Font.BOLD));
        identidade.add(marca, BorderLayout.CENTER);
        return identidade;
    }

    private JButton criarBotaoNavegacao(String secao) {
        JButton botao = new JButton(secao);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        botao.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        botao.addActionListener(event -> mostrarSecao(secao));
        return botao;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout(0, 4));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 4, 0, 0));
        rodape.add(rotulo("●  Aplicação local", new Color(123, 210, 170), 11, Font.BOLD),
                BorderLayout.NORTH);
        rodape.add(rotulo("Dados salvos neste computador",
                new Color(133, 149, 176), 10, Font.PLAIN), BorderLayout.CENTER);
        return rodape;
    }

    private void mostrarSecao(String secao) {
        atualizarNavegacao(secao);
        conteudo.removeAll();
        conteudo.setBackground(FUNDO);
        conteudo.add(criarCabecalho(secao), BorderLayout.NORTH);

        JScrollPane rolagem = criarRolagem(secao);
        conteudo.add(rolagem, BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }

    private JScrollPane criarRolagem(String secao) {
        if ("Visão geral".equals(secao)) {
            painelGeral = new JScrollPane(
                    new PainelVisaoGeral(carregarDados.get(), salvarOrdemCategorias).criar());
            painelGeral.setBorder(null);
            painelGeral.getVerticalScrollBar().setUnitIncrement(16);
            return painelGeral;
        }

        if ("Categorias".equals(secao)) {
            JScrollPane rolagem = new JScrollPane(
                    new PainelCategorias(categoriaService, this::atualizarVisaoGeral));
            rolagem.setBorder(null);
            rolagem.getVerticalScrollBar().setUnitIncrement(16);
            return rolagem;
        }

        JScrollPane rolagem = new JScrollPane(criarPagina(secao));
        rolagem.setBorder(null);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        return rolagem;
    }

    private JPanel criarCabecalho(String secao) {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                BorderFactory.createEmptyBorder(18, 30, 18, 30)));
        cabecalho.add(rotulo(secao, TEXTO, 20, Font.BOLD), BorderLayout.WEST);

        String data = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("pt-BR")));
        String dataCapitalizada = data.substring(0, 1).toUpperCase(Locale.forLanguageTag("pt-BR"))
                + data.substring(1);
        JLabel dataLabel = rotulo(dataCapitalizada, TEXTO_SUAVE, 12, Font.PLAIN);
        dataLabel.setOpaque(true);
        dataLabel.setBackground(FUNDO);
        dataLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        cabecalho.add(dataLabel, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarPagina(String secao) {
        String proximaEtapa = switch (secao) {
            case "Atividades" -> "Cadastro, edição e consulta de atividades";
            case "Categorias" -> "Cadastro de categorias e configuração de metas";
            case "Comprovantes" -> "Anexos locais e leitura de comprovantes";
            default -> secao;
        };
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(FUNDO);
        pagina.setBorder(BorderFactory.createEmptyBorder(26, 30, 30, 30));
        JPanel cartao = new JPanel();
        cartao.setLayout(new javax.swing.BoxLayout(cartao, javax.swing.BoxLayout.Y_AXIS));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        cartao.add(rotulo(secao + " em construção", TEXTO, 20, Font.BOLD));
        cartao.add(javax.swing.Box.createVerticalStrut(10));
        cartao.add(rotulo("Próxima etapa: " + proximaEtapa + ".", TEXTO_SUAVE, 14, Font.PLAIN));
        pagina.add(cartao, BorderLayout.NORTH);
        return pagina;
    }

    private void atualizarNavegacao(String secaoSelecionada) {
        botoesNavegacao.forEach((secao, botao) -> {
            boolean selecionado = secao.equals(secaoSelecionada);
            botao.setBackground(selecionado ? new Color(49, 67, 105) : AZUL_ESCURO);
            botao.setForeground(selecionado ? Color.WHITE : new Color(190, 201, 220));
            botao.setFont(new Font(Font.SANS_SERIF, selecionado ? Font.BOLD : Font.PLAIN, 13));
        });
    }

    private void atualizarVisaoGeral() {
        painelGeral = null;
    }

    private static JLabel rotulo(String texto, Color cor, int tamanho, int estilo) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(cor);
        rotulo.setFont(new Font(Font.SANS_SERIF, estilo, tamanho));
        return rotulo;
    }
}
