package br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

public final class TelaAtividadesTest extends JPanel {
    private static final DateTimeFormatter DATA_BR =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final AtividadeService atividades;
    private final CategoriaService categorias;

    private final JTextField campoTitulo = new JTextField(30);
    private final JTextField campoData = new JTextField(10);
    private final JTextField campoCarga = new JTextField(6);
    private final DefaultComboBoxModel<Categoria> modeloCategorias = new DefaultComboBoxModel<>();
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>(modeloCategorias);
    private final JButton botaoSalvar = new JButton("Salvar");
    private final JButton botaoLimpar = new JButton("Limpar");
    private final JButton botaoRemover = new JButton("Remover");
    private final JLabel aviso = new JLabel(" ");
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Título", "Data", "Carga (h)", "Categoria"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modeloTabela);

    private final List<AtividadeComplementar> naTabela = new ArrayList<>();
    private Long idEmEdicao;

    public TelaAtividadesTest(AtividadeService atividades, CategoriaService categorias) {
        this.atividades = atividades;
        this.categorias = categorias;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        comboCategoria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice, boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                if (valor instanceof Categoria categoria) {
                    setText(categoria.getNome());
                }
                return this;
            }
        });

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionada();
            }
        });

        botaoSalvar.addActionListener(e -> salvar());
        botaoLimpar.addActionListener(e -> limparFormulario());
        botaoRemover.addActionListener(e -> remover());
        aviso.setForeground(new Color(0xB00020));

        add(montarFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        recarregar();
    }

    public void recarregar() {
        Categoria anterior = (Categoria) comboCategoria.getSelectedItem();
        Long idAnterior = anterior == null ? null : anterior.getId();

        modeloCategorias.removeAllElements();
        for (Categoria categoria : categorias.listar()) {
            modeloCategorias.addElement(categoria);
        }
        selecionarCategoria(idAnterior);

        boolean semCategorias = modeloCategorias.getSize() == 0;
        botaoSalvar.setEnabled(!semCategorias);
        aviso.setText(semCategorias ? "Cadastre uma categoria antes de registrar atividades." : " ");

        recarregarTabela();
        limparFormulario();
    }

    private JPanel montarFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridy = 0;
        g.gridx = 0; g.weightx = 0; g.gridwidth = 1;
        form.add(new JLabel("Título:"), g);
        g.gridx = 1; g.weightx = 1; g.gridwidth = 5;
        form.add(campoTitulo, g);

        g.gridy = 1; g.gridwidth = 1;
        g.gridx = 0; g.weightx = 0;
        form.add(new JLabel("Data (dd/mm/aaaa):"), g);
        g.gridx = 1;
        form.add(campoData, g);
        g.gridx = 2;
        form.add(new JLabel("Carga (h):"), g);
        g.gridx = 3;
        form.add(campoCarga, g);
        g.gridx = 4;
        form.add(new JLabel("Categoria:"), g);
        g.gridx = 5; g.weightx = 1;
        form.add(comboCategoria, g);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.add(botaoSalvar);
        botoes.add(botaoLimpar);
        botoes.add(botaoRemover);
        g.gridy = 2; g.gridx = 0; g.gridwidth = 6; g.weightx = 1;
        form.add(botoes, g);

        g.gridy = 3;
        form.add(aviso, g);
        return form;
    }

    private void salvar() {
        try {
            LocalDate data = lerData();
            BigDecimal carga = lerCarga();
            Categoria categoria = (Categoria) comboCategoria.getSelectedItem();
            Long categoriaId = categoria == null ? null : categoria.getId();

            if (idEmEdicao == null) {
                atividades.cadastrar(campoTitulo.getText(), data, carga, categoriaId);
            } else {
                atividades.atualizar(idEmEdicao, campoTitulo.getText(), data, carga, categoriaId);
            }
            recarregar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(ex);
        }
    }

    private void remover() {
        if (idEmEdicao == null) {
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(
                this, "Remover a atividade selecionada?", "Confirmar remoção",
                JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            atividades.remover(idEmEdicao);
            recarregar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(ex);
        }
    }

    private LocalDate lerData() {
        String texto = campoData.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("A data da atividade é obrigatória.");
        }
        try {
           
            return texto.contains("/") ? LocalDate.parse(texto, DATA_BR) : LocalDate.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Informe uma data válida, por exemplo 24/10/2026.");
        }
    }

    private BigDecimal lerCarga() {
        String texto = campoCarga.getText().trim().replace(',', '.');
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Informe uma carga horária numérica, por exemplo 4 ou 2,5.");
        }
    }

    private void recarregarTabela() {
        naTabela.clear();
        modeloTabela.setRowCount(0);
        for (AtividadeComplementar atividade : atividades.listar()) {
            naTabela.add(atividade);
            modeloTabela.addRow(new Object[]{
                    atividade.getId(),
                    atividade.getTitulo(),
                    atividade.getData().format(DATA_BR),
                    formatar(atividade.getCargaHoraria()),
                    atividade.getCategoria().getNome()});
        }
    }

    private void carregarSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= naTabela.size()) {
            return; 
        }
        AtividadeComplementar atividade = naTabela.get(linha);
        idEmEdicao = atividade.getId();
        campoTitulo.setText(atividade.getTitulo());
        campoData.setText(atividade.getData().format(DATA_BR));
        campoCarga.setText(formatar(atividade.getCargaHoraria()));
        selecionarCategoria(atividade.getCategoria().getId());
        botaoSalvar.setText("Salvar alteração");
        botaoRemover.setEnabled(true);
    }

    private void limparFormulario() {
        idEmEdicao = null;
        tabela.clearSelection();
        campoTitulo.setText("");
        campoData.setText("");
        campoCarga.setText("");
        botaoSalvar.setText("Salvar");
        botaoRemover.setEnabled(false);
    }

    private void selecionarCategoria(Long id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < modeloCategorias.getSize(); i++) {
            Categoria categoria = modeloCategorias.getElementAt(i);
            if (id.equals(categoria.getId())) {
                comboCategoria.setSelectedItem(categoria);
                return;
            }
        }
    }

    private void mostrarErro(RuntimeException ex) {
        String mensagem = ex instanceof IllegalStateException
                ? "Erro ao acessar os dados: " + ex.getMessage()
                : ex.getMessage();
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}