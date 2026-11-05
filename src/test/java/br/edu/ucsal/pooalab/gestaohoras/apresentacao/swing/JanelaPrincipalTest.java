package br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ProgressoService;

public final class JanelaPrincipalTest extends JFrame {
    private static final String MENU = "menu";
    private static final String CATEGORIAS = "categorias";
    private static final String ATIVIDADES = "atividades";
    private static final String PROGRESSO = "progresso";

    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final TelaAtividadesTest telaAtividades;
    private final TelaProgressoTest telaProgresso;

    public JanelaPrincipalTest(
            CategoriaService categorias, AtividadeService atividades, ProgressoService progresso) {
        super("Gestão de Horas Complementares");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        telaAtividades = new TelaAtividadesTest(atividades, categorias);
        telaProgresso = new TelaProgressoTest(progresso);

        conteudo.add(new TelaMenuTest(
                () -> mostrar(CATEGORIAS),
                this::abrirAtividades,
                this::abrirProgresso,
                this::fechar), MENU);
        conteudo.add(comBarraVoltar("Categorias", new TelaCategoriasTest(categorias)), CATEGORIAS);
        conteudo.add(comBarraVoltar("Atividades", telaAtividades), ATIVIDADES);
        conteudo.add(comBarraVoltar("Progresso", telaProgresso), PROGRESSO);

        setContentPane(conteudo);
        mostrar(MENU); 
        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    private void abrirAtividades() {
        telaAtividades.recarregar(); 
        mostrar(ATIVIDADES);
    }

    private void abrirProgresso() {
        telaProgresso.recarregar(); 
        mostrar(PROGRESSO);
    }

    private void mostrar(String nomeDaTela) {
        cartas.show(conteudo, nomeDaTela);
    }

    private void fechar() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private JPanel comBarraVoltar(String titulo, JComponent tela) {
        JButton voltar = new JButton("Voltar ao menu");
        voltar.addActionListener(e -> mostrar(MENU));

        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(rotulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel barra = new JPanel(new BorderLayout(12, 0));
        barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));
        barra.add(voltar, BorderLayout.WEST);
        barra.add(rotulo, BorderLayout.CENTER);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(barra, BorderLayout.NORTH);
        painel.add(tela, BorderLayout.CENTER);
        return painel;
    }
}
