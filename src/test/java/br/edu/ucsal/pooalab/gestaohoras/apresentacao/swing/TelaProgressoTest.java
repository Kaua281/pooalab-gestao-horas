package br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ProgressoService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;

public final class TelaProgressoTest extends JPanel {
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final Color VERDE_META_ATINGIDA = new Color(0x2E7D32);

    private final ProgressoService progresso;
    private final JLabel total = new JLabel(" ");
    private final JLabel aviso = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Categoria", "Meta (h)", "Registradas (h)", "Restantes (h)", "Progresso"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    public TelaProgressoTest(ProgressoService progresso) {
        this.progresso = progresso;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        total.setFont(total.getFont().deriveFont(Font.BOLD, 15f));
        aviso.setForeground(new Color(0x555555));
        JPanel topo = new JPanel(new BorderLayout(0, 4));
        topo.add(total, BorderLayout.NORTH);
        topo.add(aviso, BorderLayout.CENTER);

        tabela.setRowHeight(28);
        tabela.getColumnModel().getColumn(4).setCellRenderer(new BarraDeProgresso());
        tabela.getColumnModel().getColumn(4).setPreferredWidth(260);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        recarregar();
    }

    public void recarregar() {
        modelo.setRowCount(0);
        for (ResumoCategoria resumo : progresso.calcularResumo()) {
            modelo.addRow(new Object[]{
                    resumo.nomeCategoria(),
                    formatar(resumo.metaHoras()),
                    formatar(resumo.horasRegistradas()),
                    formatar(resumo.horasRestantes()),
                    resumo.progressoPercentual()});
        }
        total.setText("Total de horas registradas: " + formatar(progresso.totalHorasRegistradas()) + " h");
        aviso.setText(modelo.getRowCount() == 0
                ? "Cadastre categorias e atividades para acompanhar o progresso."
                : " ");
    }

    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }

    private static final class BarraDeProgresso extends JProgressBar implements TableCellRenderer {
        private final Color corPadrao;

        BarraDeProgresso() {
            super(0, 100);
            setStringPainted(true);
            corPadrao = getForeground();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tabela, Object valor, boolean selecionada, boolean foco, int linha, int coluna) {
            BigDecimal percentual = (BigDecimal) valor;
            setValue(percentual.intValue());
            setString(formatar(percentual) + "%");
            setForeground(percentual.compareTo(CEM) >= 0 ? VERDE_META_ATINGIDA : corPadrao);
            return this;
        }
    }
}
