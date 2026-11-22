package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class PainelAtividades extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL = new Color(67, 97, 238);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AtividadeService atividadeService;
    private final CategoriaService categoriaService;
    private final Runnable atualizarPainelInicial;

    private JTextField campoTitulo;
    private JTextField campoData;
    private JTextField campoCargaHoraria;
    private JComboBox<CategoriaOpcao> campoCategoria;
    private JTable tabela;
    private DefaultTableModel modelo;
    private Long idSelecionado;

    public PainelAtividades(
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
        atualizarTabela();
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(FUNDO);
        conteudo.add(criarIntroducao());
        conteudo.add(Box.createVerticalStrut(22));
        conteudo.add(criarFormulario());
        conteudo.add(Box.createVerticalStrut(22));
        conteudo.add(criarTabela());
        return conteudo;
    }

    private JPanel criarIntroducao() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(FUNDO);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Gerenciamento de Atividades");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel(
                "Cadastre, edite e organize suas atividades complementares.");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descricao.setForeground(SUAVE);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(7));
        painel.add(descricao);
        return painel;
    }

    private JPanel criarFormulario() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 18));
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Dados da atividade");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        campoTitulo = new JTextField();
        campoData = new JTextField(LocalDate.now().format(FORMATO_DATA));
        campoCargaHoraria = new JTextField();
        campoCategoria = criarSeletorCategorias();

        JPanel campos = new JPanel(new GridLayout(2, 2, 18, 14));
        campos.setBackground(Color.WHITE);
        campos.add(criarCampo("Título da atividade", campoTitulo));
        campos.add(criarCampo("Data (DD/MM/AAAA)", campoData));
        campos.add(criarCampo("Carga horária", campoCargaHoraria));
        campos.add(criarSeletorCategoria("Categoria", campoCategoria));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setBackground(Color.WHITE);
        JButton cadastrar = criarBotao("Cadastrar atividade", AZUL);
        JButton editar = criarBotao("Salvar edição", new Color(36, 150, 110));
        JButton limpar = criarBotao("Limpar", new Color(112, 124, 145));
        cadastrar.addActionListener(event -> cadastrar());
        editar.addActionListener(event -> editar());
        limpar.addActionListener(event -> limparCampos());
        botoes.add(cadastrar);
        botoes.add(editar);
        botoes.add(limpar);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(Color.WHITE);
        centro.add(campos, BorderLayout.CENTER);
        centro.add(botoes, BorderLayout.SOUTH);
        cartao.add(titulo, BorderLayout.NORTH);
        cartao.add(centro, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCampo(String titulo, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(200, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(5, 10, 5, 10)));
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private JComboBox<CategoriaOpcao> criarSeletorCategorias() {
        JComboBox<CategoriaOpcao> seletor = new JComboBox<>();
        seletor.addItem(new CategoriaOpcao(null, "Sem categoria"));
        for (Categoria categoria : categoriaService.listar()) {
            seletor.addItem(new CategoriaOpcao(categoria.getId(), categoria.getNome()));
        }
        return seletor;
    }

    private JPanel criarSeletorCategoria(String titulo, JComboBox<CategoriaOpcao> campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(200, 40));
        campo.setBackground(Color.WHITE);
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarTabela() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 15));
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Atividades cadastradas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Título", "Data", "Carga horária", "Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(38);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(new Color(242, 245, 250));
        tabela.setSelectionBackground(new Color(225, 233, 255));
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);
        tabela.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                selecionarAtividade();
            }
        });

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.setPreferredSize(new Dimension(500, 260));

        JButton remover = criarBotao("Remover atividade", new Color(205, 70, 70));
        remover.addActionListener(event -> remover());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setBackground(Color.WHITE);
        rodape.add(remover);

        cartao.add(titulo, BorderLayout.NORTH);
        cartao.add(rolagem, BorderLayout.CENTER);
        cartao.add(rodape, BorderLayout.SOUTH);
        return cartao;
    }

    private JPanel criarCartao() {
        JPanel cartao = new JPanel();
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                new EmptyBorder(20, 20, 20, 20)));
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

    private void cadastrar() {
        try {
            atividadeService.cadastrar(
                    campoTitulo.getText(),
                    lerData(),
                    lerCargaHoraria(),
                    categoriaSelecionada());
            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();
            JOptionPane.showMessageDialog(this, "Atividade cadastrada com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma atividade para editar.");
            return;
        }
        try {
            atividadeService.atualizar(
                    idSelecionado,
                    campoTitulo.getText(),
                    lerData(),
                    lerCargaHoraria(),
                    categoriaSelecionada());
            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();
            JOptionPane.showMessageDialog(this, "Atividade atualizada com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void remover() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma atividade para remover.");
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente remover esta atividade?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            atividadeService.remover(idSelecionado);
            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();
            JOptionPane.showMessageDialog(this, "Atividade removida com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void selecionarAtividade() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = ((Number) modelo.getValueAt(linha, 0)).longValue();
        campoTitulo.setText(modelo.getValueAt(linha, 1).toString());
        campoData.setText(modelo.getValueAt(linha, 2).toString());
        campoCargaHoraria.setText(modelo.getValueAt(linha, 3).toString()
                .replace(" h", "").replace(",", "."));

        String nomeCategoria = modelo.getValueAt(linha, 4).toString();
        for (int i = 0; i < campoCategoria.getItemCount(); i++) {
            if (campoCategoria.getItemAt(i).nome().equals(nomeCategoria)) {
                campoCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        List<AtividadeComplementar> atividades = atividadeService.listar();
        for (AtividadeComplementar atividade : atividades) {
            modelo.addRow(new Object[]{
                    atividade.getId(),
                    atividade.getTitulo(),
                    atividade.getData().format(FORMATO_DATA),
                    formatar(atividade.getCargaHoraria()) + " h",
                    atividade.getCategoria() == null
                            ? "Sem categoria"
                            : atividade.getCategoria().getNome()
            });
        }
    }

    private LocalDate lerData() {
        try {
            return LocalDate.parse(campoData.getText().trim(), FORMATO_DATA);
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Informe a data no formato DD/MM/AAAA.");
        }
    }

    private BigDecimal lerCargaHoraria() {
        String valor = campoCargaHoraria.getText().trim().replace(",", ".");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("Informe a carga horária.");
        }
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Informe uma carga horária válida.");
        }
    }

    private Long categoriaSelecionada() {
        CategoriaOpcao opcao = (CategoriaOpcao) campoCategoria.getSelectedItem();
        return opcao == null ? null : opcao.id();
    }

    private void limparCampos() {
        campoTitulo.setText("");
        campoData.setText(LocalDate.now().format(FORMATO_DATA));
        campoCargaHoraria.setText("");
        campoCategoria.setSelectedIndex(0);
        idSelecionado = null;
        tabela.clearSelection();
    }

    private void mostrarErro(RuntimeException erro) {
        JOptionPane.showMessageDialog(
                this,
                erro.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
    }

    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }

    private record CategoriaOpcao(Long id, String nome) {
        @Override
        public String toString() {
            return nome;
        }
    }
}
