
package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public final class PainelCategorias extends JPanel {
	private static final long serialVersionUID = 1L;

    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL = new Color(67, 97, 238);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);

    private final CategoriaService service;
    private final Runnable atualizarPainelInicial;

    private JTextField campoNome;
    private JTextField campoMeta;
    private JTable tabela;
    private DefaultTableModel modelo;

    private Long idSelecionado;

    public PainelCategorias(CategoriaService service) {
        this(service, () -> {});
    }

    public PainelCategorias(CategoriaService service, Runnable atualizarPainelInicial) {
        this.service = service;
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

        JLabel titulo = new JLabel("Gerenciamento de Categorias");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO);

        JLabel descricao = new JLabel(
                "Cadastre e organize as categorias de horas complementares."
        );
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

        JLabel titulo = new JLabel("Dados da categoria");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        JPanel campos = new JPanel(new GridLayout(1, 2, 18, 0));
        campos.setBackground(Color.WHITE);

        campoNome = new JTextField();
        campoMeta = new JTextField();

        campos.add(criarCampo("Nome da categoria", campoNome));
        campos.add(criarCampo("Meta de horas", campoMeta));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setBackground(Color.WHITE);

        JButton cadastrar = criarBotao("Cadastrar categoria", AZUL);
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
                new EmptyBorder(5, 10, 5, 10)
        ));

        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarTabela() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 15));
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Categorias cadastradas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nome da categoria", "Meta de horas"}, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(38);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        tabela.getTableHeader().setBackground(new Color(242, 245, 250));
        tabela.setSelectionBackground(new Color(225, 233, 255));

        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        tabela.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                selecionarCategoria();
            }
        });

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.setPreferredSize(new Dimension(500, 260));

        JButton remover = criarBotao(
                "Remover categoria", new Color(205, 70, 70)
        );
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
                new EmptyBorder(20, 20, 20, 20)
        ));
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
            String nome = campoNome.getText();
            BigDecimal meta = lerMeta();

            service.cadastrar(nome, meta);

            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();

            JOptionPane.showMessageDialog(
                    this,
                    "Categoria cadastrada com sucesso!"
            );

        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma categoria para editar."
            );
            return;
        }

        try {
            service.atualizar(
                    idSelecionado,
                    campoNome.getText(),
                    lerMeta()
            );

            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();

            JOptionPane.showMessageDialog(
                    this,
                    "Categoria atualizada com sucesso!"
            );

        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void remover() {
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma categoria para remover."
            );
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente remover esta categoria?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.remover(idSelecionado);

            atualizarTabela();
            limparCampos();
            atualizarPainelInicial.run();

            JOptionPane.showMessageDialog(
                    this,
                    "Categoria removida com sucesso!"
            );

        } catch (RuntimeException erro) {
            mostrarErro(erro);
        }
    }

    private void selecionarCategoria() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            return;
        }

        idSelecionado = ((Number) modelo.getValueAt(linha, 0)).longValue();
        campoNome.setText(modelo.getValueAt(linha, 1).toString());
        campoMeta.setText(modelo.getValueAt(linha, 2).toString());
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);

        List<Categoria> categorias = service.listar();

        for (Categoria categoria : categorias) {
            modelo.addRow(new Object[]{
                    categoria.getId(),
                    categoria.getNome(),
                    categoria.getMetaHoras().toPlainString()
            });
        }
    }

    private BigDecimal lerMeta() {
        String valor = campoMeta.getText().trim().replace(",", ".");

        if (valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe a meta de horas."
            );
        }

        return new BigDecimal(valor);
    }

    private void limparCampos() {
        campoNome.setText("");
        campoMeta.setText("");
        idSelecionado = null;
        tabela.clearSelection();
    }

    private void mostrarErro(RuntimeException erro) {
        JOptionPane.showMessageDialog(
                this,
                erro.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
