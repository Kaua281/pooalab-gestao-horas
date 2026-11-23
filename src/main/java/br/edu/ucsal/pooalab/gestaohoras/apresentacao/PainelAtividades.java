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
import javax.swing.JComponent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PainelAtividades extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL = new Color(67, 97, 238);
    private static final Color VERDE = new Color(36, 150, 110);
    private static final Color VERMELHO = new Color(205, 70, 70);
    private static final Color CINZA = new Color(112, 124, 145);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);

    private static final String SEM_CATEGORIA = "Sem categoria";

    private final AtividadeService atividades;
    private final CategoriaService categorias;

    private final JTextField campoTitulo = new JTextField();
    private final JTextField campoData = new JTextField();
    private final JTextField campoHoras = new JTextField();
    private final JComboBox<OpcaoCategoria> comboCategoria = new JComboBox<>();
    private final JLabel tituloFormulario = new JLabel("Nova atividade");
    private final JLabel resumoTabela = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Título", "Categoria", "Data", "Horas"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    // Espelha as linhas da tabela, na mesma ordem: a linha N da tabela é exibidas.get(N).
    private final List<AtividadeComplementar> exibidas = new ArrayList<>();
    private Long idSelecionado; // null = o formulário está cadastrando uma atividade nova

    public PainelAtividades(AtividadeService atividades, CategoriaService categorias) {
        this.atividades = atividades;
        this.categorias = categorias;

        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(26, 30, 30, 30));
        add(criarConteudo(), BorderLayout.CENTER);

        recarregar();
    }

    /**
     * Relê categorias e atividades do banco e limpa o formulário. A janela chama este método toda
     * vez que a seção é aberta, porque as outras telas podem ter alterado os dados.
     */
    public void recarregar() {
        carregarOpcoesDeCategoria();
        atualizarTabela();
        limparCampos();
    }

    private JPanel criarConteudo() {
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(FUNDO);
        topo.add(criarIntroducao());
        topo.add(Box.createVerticalStrut(22));
        topo.add(criarFormulario());
        topo.add(Box.createVerticalStrut(22));

        // O formulário fica no NORTE (altura natural) e a tabela ocupa todo o espaço que sobrar.
        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(FUNDO);
        conteudo.add(topo, BorderLayout.NORTH);
        conteudo.add(criarTabela(), BorderLayout.CENTER);
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
                "Registre suas atividades complementares. Clique em uma linha da tabela para editá-la.");
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

        tituloFormulario.setFont(new Font("SansSerif", Font.BOLD, 16));
        tituloFormulario.setForeground(TEXTO);

        JPanel campos = new JPanel();
        campos.setLayout(new BoxLayout(campos, BoxLayout.Y_AXIS));
        campos.setBackground(Color.WHITE);

        JPanel linhaTitulo = criarCampo("Título da atividade", campoTitulo);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campos.add(linhaTitulo);
        campos.add(Box.createVerticalStrut(14));

        JPanel linhaDetalhes = new JPanel(new GridLayout(1, 3, 18, 0));
        linhaDetalhes.setBackground(Color.WHITE);
        linhaDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaDetalhes.add(criarCampo("Data (dd/mm/aaaa)", campoData));
        linhaDetalhes.add(criarCampo("Carga horária (horas)", campoHoras));
        linhaDetalhes.add(criarCampo("Categoria (opcional)", comboCategoria));
        campos.add(linhaDetalhes);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setBackground(Color.WHITE);

        JButton cadastrar = criarBotao("Cadastrar atividade", AZUL);
        JButton salvarEdicao = criarBotao("Salvar edição", VERDE);
        JButton limpar = criarBotao("Limpar", CINZA);

        cadastrar.addActionListener(event -> cadastrar());
        salvarEdicao.addActionListener(event -> editar());
        limpar.addActionListener(event -> limparCampos());

        botoes.add(cadastrar);
        botoes.add(salvarEdicao);
        botoes.add(limpar);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(Color.WHITE);
        centro.add(campos, BorderLayout.CENTER);
        centro.add(botoes, BorderLayout.SOUTH);

        cartao.add(tituloFormulario, BorderLayout.NORTH);
        cartao.add(centro, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCampo(String titulo, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Color.WHITE);

        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        rotulo.setForeground(TEXTO);

        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(120, 40)); // o GridLayout expande até preencher a linha
        if (campo instanceof JTextField) {
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDA),
                    new EmptyBorder(5, 10, 5, 10)));
        } else {
            campo.setBackground(Color.WHITE);
        }

        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarTabela() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 15));

        JLabel titulo = new JLabel("Atividades cadastradas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        resumoTabela.setFont(new Font("SansSerif", Font.PLAIN, 12));
        resumoTabela.setForeground(SUAVE);

        JPanel cabecalho = new JPanel(new BorderLayout(0, 4));
        cabecalho.setBackground(Color.WHITE);
        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(resumoTabela, BorderLayout.CENTER);

        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(38);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(new Color(242, 245, 250));
        tabela.setSelectionBackground(new Color(225, 233, 255));
        tabela.setSelectionForeground(TEXTO);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(360);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabela.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                selecionarAtividade();
            }
        });

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.setPreferredSize(new Dimension(500, 170));

        JButton remover = criarBotao("Remover atividade", VERMELHO);
        remover.addActionListener(event -> remover());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setBackground(Color.WHITE);
        rodape.add(remover);

        cartao.add(cabecalho, BorderLayout.NORTH);
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
            atividades.cadastrar(
                    campoTitulo.getText(),
                    LeitorDeCampos.lerData(campoData.getText()),
                    LeitorDeCampos.lerCargaHoraria(campoHoras.getText()),
                    categoriaEscolhida());

            recarregar();
            JOptionPane.showMessageDialog(this, "Atividade cadastrada com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma atividade na tabela para editar.");
            return;
        }

        try {
            atividades.atualizar(
                    idSelecionado,
                    campoTitulo.getText(),
                    LeitorDeCampos.lerData(campoData.getText()),
                    LeitorDeCampos.lerCargaHoraria(campoHoras.getText()),
                    categoriaEscolhida());

            recarregar();
            JOptionPane.showMessageDialog(this, "Atividade atualizada com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void remover() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma atividade na tabela para remover.");
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
            atividades.remover(idSelecionado);

            recarregar();
            JOptionPane.showMessageDialog(this, "Atividade removida com sucesso!");
        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void selecionarAtividade() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= exibidas.size()) {
            return; // a seleção foi desfeita (por exemplo, ao recarregar a tabela)
        }

        AtividadeComplementar atividade = exibidas.get(tabela.convertRowIndexToModel(linha));
        idSelecionado = atividade.getId();
        campoTitulo.setText(atividade.getTitulo());
        campoData.setText(LeitorDeCampos.formatarData(atividade.getData()));
        campoHoras.setText(LeitorDeCampos.formatarHoras(atividade.getCargaHoraria()));
        selecionarOpcao(atividade.getCategoria() == null ? null : atividade.getCategoria().getId());
        tituloFormulario.setText("Editando atividade");
    }

    private void carregarOpcoesDeCategoria() {
        comboCategoria.removeAllItems();
        comboCategoria.addItem(new OpcaoCategoria(null, SEM_CATEGORIA));
        for (Categoria categoria : categorias.listar()) {
            comboCategoria.addItem(new OpcaoCategoria(categoria.getId(), categoria.getNome()));
        }
    }

    private void atualizarTabela() {
        exibidas.clear();
        modelo.setRowCount(0);

        BigDecimal total = BigDecimal.ZERO;
        for (AtividadeComplementar atividade : atividades.listar()) {
            exibidas.add(atividade);
            total = total.add(atividade.getCargaHoraria());
            modelo.addRow(new Object[]{
                    atividade.getTitulo(),
                    atividade.getCategoria() == null ? SEM_CATEGORIA : atividade.getCategoria().getNome(),
                    LeitorDeCampos.formatarData(atividade.getData()),
                    LeitorDeCampos.formatarHoras(atividade.getCargaHoraria()) + " h"
            });
        }

        if (exibidas.isEmpty()) {
            resumoTabela.setText("Nenhuma atividade cadastrada ainda.");
        } else {
            String quantidade = exibidas.size() == 1 ? "1 atividade" : exibidas.size() + " atividades";
            resumoTabela.setText(quantidade + " · " + LeitorDeCampos.formatarHoras(total) + " h no total");
        }
    }

    private void limparCampos() {
        idSelecionado = null;
        tabela.clearSelection();
        campoTitulo.setText("");
        campoData.setText("");
        campoHoras.setText("");
        comboCategoria.setSelectedIndex(0); // "Sem categoria"
        tituloFormulario.setText("Nova atividade");
        campoTitulo.requestFocusInWindow();
    }

    private Long categoriaEscolhida() {
        OpcaoCategoria opcao = (OpcaoCategoria) comboCategoria.getSelectedItem();
        return opcao == null ? null : opcao.id();
    }

    private void selecionarOpcao(Long categoriaId) {
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (Objects.equals(comboCategoria.getItemAt(i).id(), categoriaId)) {
                comboCategoria.setSelectedIndex(i);
                return;
            }
        }
        comboCategoria.setSelectedIndex(0);
    }

    private void mostrarErro(RuntimeException erro) {
        String mensagem = erro.getMessage() == null
                ? "Não foi possível concluir a operação."
                : erro.getMessage();
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Item do seletor de categoria: o JComboBox exibe o toString() de cada item.
    // O id null representa "Sem categoria".
    private record OpcaoCategoria(Long id, String nome) {
        @Override
        public String toString() {
            return nome;
        }
    }
}
