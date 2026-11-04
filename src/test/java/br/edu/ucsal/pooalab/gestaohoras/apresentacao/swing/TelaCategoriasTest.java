package br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
public final class TelaCategoriasTest extends JPanel {
    private final CategoriaService servico;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Meta (h)"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoMeta = new JTextField(6);

    public TelaCategoriasTest(CategoriaService servico) {
        this.servico = servico;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nome:"));
        formulario.add(campoNome);
        formulario.add(new JLabel("Meta (h):"));
        formulario.add(campoMeta);
        JButton cadastrar = new JButton("Cadastrar");
        cadastrar.addActionListener(e -> cadastrar());
        formulario.add(cadastrar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        recarregar();
    }

    private void cadastrar() {
        try {
            BigDecimal meta = new BigDecimal(campoMeta.getText().trim().replace(',', '.'));
            servico.cadastrar(campoNome.getText(), meta);
            campoNome.setText("");
            campoMeta.setText("");
            recarregar();
        } catch (NumberFormatException ex) {      // subclasse: vem antes
            erro("Informe uma meta numérica, por exemplo 20 ou 12,5.");
        } catch (IllegalArgumentException ex) {   // regras do domínio
            erro(ex.getMessage());
        } catch (IllegalStateException ex) {      // falha de banco
            erro("Erro ao acessar os dados: " + ex.getMessage());
        }
    }

    private void recarregar() {
        modelo.setRowCount(0);
        for (Categoria c : servico.listar()) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNome(), c.getMetaHoras().stripTrailingZeros().toPlainString()});
        }
    }

    private void erro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}