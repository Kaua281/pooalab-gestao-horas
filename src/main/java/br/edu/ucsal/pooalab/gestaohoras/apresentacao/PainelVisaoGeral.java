package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.DadosPainelInicial;
import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class PainelVisaoGeral {
    private static final Color FUNDO = new Color(246, 248, 251);
    private static final Color AZUL = new Color(67, 97, 238);
    private static final Color AZUL_CLARO = new Color(235, 240, 255);
    private static final Color TEXTO = new Color(36, 48, 68);
    private static final Color TEXTO_SUAVE = new Color(112, 124, 145);
    private static final Color BORDA = new Color(228, 233, 241);
    private static final Color VERDE = new Color(17, 143, 111);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DadosPainelInicial dados;
    private final Consumer<List<Long>> salvarOrdemCategorias;

    PainelVisaoGeral(DadosPainelInicial dados, Consumer<List<Long>> salvarOrdemCategorias) {
        this.dados = dados;
        this.salvarOrdemCategorias = salvarOrdemCategorias;
    }

    JPanel criar() {
        JPanel pagina = new JPanel();
        pagina.setBackground(FUNDO);
        pagina.setBorder(BorderFactory.createEmptyBorder(26, 30, 30, 30));
        pagina.setLayout(new javax.swing.BoxLayout(pagina, javax.swing.BoxLayout.Y_AXIS));
        JPanel boasVindas = criarBoasVindas();
        boasVindas.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagina.add(boasVindas);
        pagina.add(javax.swing.Box.createVerticalStrut(20));

        JPanel indicadores = criarIndicadores();
        indicadores.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagina.add(indicadores);
        pagina.add(javax.swing.Box.createVerticalStrut(20));

        JPanel secoes = criarSecoes();
        secoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pagina.add(secoes);

        if (dados.horasSemCategoria().signum() > 0) {
            JLabel aviso = new JLabel("Há " + formatar(dados.horasSemCategoria())
                    + " h sem categoria. Você pode reclassificá-las ao editar a atividade.");
            aviso.setForeground(TEXTO_SUAVE);
            aviso.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            pagina.add(javax.swing.Box.createVerticalStrut(16));
            aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
            pagina.add(aviso);
        }
        return pagina;
    }

    private JPanel criarBoasVindas() {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(AZUL_CLARO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, AZUL),
                BorderFactory.createEmptyBorder(18, 20, 18, 22)));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new javax.swing.BoxLayout(texto, javax.swing.BoxLayout.Y_AXIS));
        texto.add(rotulo("GESTÃO DO SEMESTRE", AZUL, 10, Font.BOLD));
        texto.add(javax.swing.Box.createVerticalStrut(6));
        texto.add(rotulo("Seu semestre, em dia.", TEXTO, 21, Font.BOLD));
        texto.add(javax.swing.Box.createVerticalStrut(5));
        texto.add(rotulo("Acompanhe suas atividades e horas complementares em um só lugar.",
                new Color(82, 97, 122), 13, Font.PLAIN));
        cartao.add(texto, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarIndicadores() {
        JPanel indicadores = new JPanel(new GridLayout(1, 3, 14, 0));
        indicadores.setOpaque(false);
        indicadores.add(criarIndicador("Horas registradas", formatar(dados.totalHoras()) + " h"));
        indicadores.add(criarIndicador(
                "Atividades cadastradas", Integer.toString(dados.quantidadeAtividades())));
        indicadores.add(criarIndicador(
                "Categorias configuradas", Integer.toString(dados.categorias().size())));
        indicadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        return indicadores;
    }

    private JPanel criarIndicador(String titulo, String valor) {
        JPanel cartao = criarCartao();
        cartao.setLayout(new javax.swing.BoxLayout(cartao, javax.swing.BoxLayout.Y_AXIS));
        cartao.add(rotulo(titulo, TEXTO_SUAVE, 13, Font.PLAIN));
        cartao.add(javax.swing.Box.createVerticalStrut(12));
        cartao.add(rotulo(valor, TEXTO, 25, Font.BOLD));
        return cartao;
    }

    private JPanel criarSecoes() {
        JPanel secoes = new JPanel(new GridLayout(1, 2, 16, 0));
        secoes.setOpaque(false);
        secoes.add(criarCartaoAtividades());
        secoes.add(criarCartaoCategorias());
        secoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 370));
        return secoes;
    }

    private JPanel criarCartaoAtividades() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 14));
        cartao.add(tituloCartao("Atividades recentes"), BorderLayout.NORTH);
        if (dados.atividadesRecentes().isEmpty()) {
            cartao.add(vazio("Suas atividades aparecerão aqui."), BorderLayout.CENTER);
            return cartao;
        }

        DefaultTableModel modelo = new DefaultTableModel(
                new String[] {"Atividade", "Categoria", "Data", "Horas"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        for (AtividadeComplementar atividade : dados.atividadesRecentes()) {
            modelo.addRow(new Object[] {
                    atividade.getTitulo(),
                    atividade.getCategoria() == null
                            ? "Sem categoria"
                            : atividade.getCategoria().getNome(),
                    atividade.getData().format(FORMATO_DATA),
                    formatar(atividade.getCargaHoraria()) + " h"
            });
        }

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(BORDA);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        tabela.getTableHeader().setForeground(TEXTO_SUAVE);
        tabela.getTableHeader().setBackground(Color.WHITE);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabela.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        tabela.setForeground(TEXTO);
        tabela.setSelectionBackground(AZUL_CLARO);
        tabela.setSelectionForeground(TEXTO);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(85);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(60);
        cartao.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarCartaoCategorias() {
        JPanel cartao = criarCartao();
        cartao.setLayout(new BorderLayout(0, 14));
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(tituloCartao("Progresso por categoria"), BorderLayout.WEST);
        if (!dados.categorias().isEmpty()) {
            cabecalho.add(rotulo("Arraste para reordenar", TEXTO_SUAVE, 10, Font.PLAIN),
                    BorderLayout.EAST);
        }
        cartao.add(cabecalho, BorderLayout.NORTH);
        if (dados.categorias().isEmpty()) {
            cartao.add(vazio("Configure uma categoria para acompanhar suas metas."), BorderLayout.CENTER);
            return cartao;
        }

        DefaultListModel<ResumoCategoria> modelo = new DefaultListModel<>();
        for (ResumoCategoria resumo : dados.categorias()) {
            modelo.addElement(resumo);
        }

        JList<ResumoCategoria> lista = new JList<>(modelo);
        lista.setCellRenderer(new CategoriaRenderer());
        lista.setVisibleRowCount(3);
        lista.setFixedCellHeight(92);
        lista.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lista.setDragEnabled(true);
        lista.setDropMode(DropMode.INSERT);
        lista.setTransferHandler(new ReordenarCategoriasHandler(
                lista, modelo, salvarOrdemCategorias));
        lista.setToolTipText("Arraste uma categoria para alterar a ordem de exibição.");

        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setBorder(null);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.getViewport().setOpaque(false);
        estilizarBarraRolagem(rolagem.getVerticalScrollBar());
        cartao.add(rolagem, BorderLayout.CENTER);
        return cartao;
    }

    private void estilizarBarraRolagem(JScrollBar barra) {
        barra.setPreferredSize(new Dimension(9, 0));
        barra.setOpaque(false);
        barra.setUnitIncrement(16);
        barra.setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics grafico, JComponent componente, Rectangle area) {
            }

            @Override
            protected void paintThumb(Graphics grafico, JComponent componente, Rectangle area) {
                if (area.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) grafico.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isDragging ? AZUL : new Color(190, 199, 215));
                g2.fillRoundRect(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 8, 8);
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientacao) {
                return botaoVazio();
            }

            @Override
            protected JButton createIncreaseButton(int orientacao) {
                return botaoVazio();
            }

            private JButton botaoVazio() {
                JButton botao = new JButton();
                botao.setPreferredSize(new Dimension(0, 0));
                botao.setMinimumSize(new Dimension(0, 0));
                botao.setMaximumSize(new Dimension(0, 0));
                botao.setBorderPainted(false);
                botao.setContentAreaFilled(false);
                return botao;
            }
        });
    }

    private static class CategoriaRenderer extends JPanel implements ListCellRenderer<ResumoCategoria> {
        private final JLabel nome = rotulo("", TEXTO, 13, Font.BOLD);
        private final JLabel percentual = rotulo("", AZUL, 11, Font.BOLD);
        private final JProgressBar barra = new JProgressBar(0, 100);
        private final JLabel horas = rotulo("", TEXTO_SUAVE, 11, Font.PLAIN);
        private final JLabel restantes = rotulo("", TEXTO_SUAVE, 11, Font.BOLD);

        CategoriaRenderer() {
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
            setOpaque(true);

            JPanel cabecalho = new JPanel(new BorderLayout());
            cabecalho.setOpaque(false);
            cabecalho.add(nome, BorderLayout.WEST);
            percentual.setOpaque(true);
            percentual.setBackground(AZUL_CLARO);
            percentual.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            cabecalho.add(percentual, BorderLayout.EAST);
            add(cabecalho);
            add(javax.swing.Box.createVerticalStrut(9));

            barra.setStringPainted(false);
            barra.setForeground(AZUL);
            barra.setBackground(new Color(235, 239, 246));
            barra.setBorderPainted(false);
            barra.setPreferredSize(new Dimension(100, 9));
            barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));
            add(barra);
            add(javax.swing.Box.createVerticalStrut(7));

            JPanel detalhes = new JPanel(new BorderLayout());
            detalhes.setOpaque(false);
            detalhes.add(horas, BorderLayout.WEST);
            detalhes.add(restantes, BorderLayout.EAST);
            add(detalhes);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ResumoCategoria> lista,
                ResumoCategoria resumo,
                int indice,
                boolean selecionado,
                boolean temFoco) {
            nome.setText(resumo.nomeCategoria());
            nome.setToolTipText(resumo.nomeCategoria());
            percentual.setText(resumo.progressoPercentual().stripTrailingZeros().toPlainString() + "%");
            barra.setValue(resumo.progressoPercentual().intValue());
            horas.setText(formatar(resumo.horasRegistradas()) + " de "
                    + formatar(resumo.metaHoras()) + " h");
            boolean concluida = resumo.horasRestantes().signum() == 0;
            restantes.setText(concluida
                    ? "Meta atingida"
                    : "Faltam " + formatar(resumo.horasRestantes()) + " h");
            restantes.setForeground(concluida ? VERDE : TEXTO_SUAVE);
            setBackground(selecionado ? AZUL_CLARO : Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                    BorderFactory.createEmptyBorder(11, 4, 13, 4)));
            return this;
        }
    }

    private static class ReordenarCategoriasHandler extends TransferHandler {
        private final JList<ResumoCategoria> lista;
        private final DefaultListModel<ResumoCategoria> modelo;
        private final Consumer<List<Long>> salvarOrdem;

        ReordenarCategoriasHandler(JList<ResumoCategoria> lista,
                DefaultListModel<ResumoCategoria> modelo,
                Consumer<List<Long>> salvarOrdem) {
            this.lista = lista;
            this.modelo = modelo;
            this.salvarOrdem = salvarOrdem;
        }

        @Override
        public int getSourceActions(JComponent componente) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent componente) {
            return new StringSelection(Integer.toString(lista.getSelectedIndex()));
        }

        @Override
        public boolean canImport(TransferSupport suporte) {
            return suporte.isDrop()
                    && suporte.getComponent() == lista
                    && suporte.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport suporte) {
            if (!canImport(suporte)) {
                return false;
            }

            try {
                int origem = Integer.parseInt((String) suporte.getTransferable()
                        .getTransferData(DataFlavor.stringFlavor));
                int destino = ((JList.DropLocation) suporte.getDropLocation()).getIndex();
                if (origem < 0 || origem >= modelo.size() || destino < 0) {
                    return false;
                }
                destino = Math.min(destino, modelo.size());
                if (origem < destino) {
                    destino--;
                }
                if (origem == destino) {
                    return true;
                }

                List<ResumoCategoria> ordemAnterior = new ArrayList<>();
                for (int i = 0; i < modelo.size(); i++) {
                    ordemAnterior.add(modelo.get(i));
                }
                ResumoCategoria categoria = modelo.remove(origem);
                modelo.add(destino, categoria);
                lista.setSelectedIndex(destino);

                List<Long> ids = new ArrayList<>();
                for (int i = 0; i < modelo.size(); i++) {
                    ids.add(modelo.get(i).categoriaId());
                }
                try {
                    salvarOrdem.accept(ids);
                } catch (IllegalArgumentException | IllegalStateException exception) {
                    modelo.clear();
                    ordemAnterior.forEach(modelo::addElement);
                    JOptionPane.showMessageDialog(
                            lista,
                            "Não foi possível salvar a nova ordem das categorias.\n"
                                    + exception.getMessage(),
                            "Erro ao salvar",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return true;
            } catch (java.awt.datatransfer.UnsupportedFlavorException | IOException exception) {
                return false;
            } catch (NumberFormatException exception) {
                return false;
            }
        }
    }

    private JPanel criarCartao() {
        JPanel cartao = new JPanel();
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return cartao;
    }

    private JLabel tituloCartao(String texto) {
        return rotulo(texto, TEXTO, 15, Font.BOLD);
    }

    private JLabel vazio(String texto) {
        JLabel vazio = rotulo(texto, TEXTO_SUAVE, 13, Font.PLAIN);
        vazio.setHorizontalAlignment(SwingConstants.CENTER);
        return vazio;
    }

    private static JLabel rotulo(String texto, Color cor, int tamanho, int estilo) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(cor);
        rotulo.setFont(new Font(Font.SANS_SERIF, estilo, tamanho));
        return rotulo;
    }

    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
