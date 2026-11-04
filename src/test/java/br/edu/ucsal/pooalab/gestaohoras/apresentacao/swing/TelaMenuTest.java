package br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class TelaMenuTest extends JPanel {

    public TelaMenuTest(
            Runnable abrirCategorias,
            Runnable abrirAtividades,
            Runnable abrirProgresso,
            Runnable sair) {
        setLayout(new GridBagLayout());

        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestão de Horas Complementares");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Escolha uma opção");
        subtitulo.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        bloco.add(titulo);
        bloco.add(Box.createVerticalStrut(6));
        bloco.add(subtitulo);
        bloco.add(Box.createVerticalStrut(24));
        bloco.add(criarBotao("Categorias", abrirCategorias));
        bloco.add(Box.createVerticalStrut(10));
        bloco.add(criarBotao("Atividades", abrirAtividades));
        bloco.add(Box.createVerticalStrut(10));
        bloco.add(criarBotao("Progresso", abrirProgresso));
        bloco.add(Box.createVerticalStrut(10));
        bloco.add(criarBotao("Sair", sair));

        bloco.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(bloco);
    }

    private JButton criarBotao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        Dimension tamanho = new Dimension(260, 40);
        botao.setPreferredSize(tamanho);
        botao.setMaximumSize(tamanho);
        if (acao == null) {
            botao.setEnabled(false);
            botao.setToolTipText("Em desenvolvimento");
        } else {
            botao.addActionListener(e -> acao.run());
        }
        return botao;
    }
}