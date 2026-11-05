package br.edu.ucsal.pooalab.gestaohoras;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ProgressoService;
import br.edu.ucsal.pooalab.gestaohoras.apresentacao.swing.JanelaPrincipalTest;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;

public final class MainSwingTest {
    public static void main(String[] args) throws Exception {
        Path pasta = Path.of(System.getProperty("user.home"), ".gestao-horas");
        SQLiteDatabase banco = new SQLiteDatabase(pasta.resolve("gestao-horas.db"));
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(banco);
        SQLiteAtividadeRepository atividades = new SQLiteAtividadeRepository(banco);
        ProgressoService progressoService = new ProgressoService(categorias, atividades);


        CategoriaService categoriaService = new CategoriaService(categorias);
        AtividadeService atividadeService = new AtividadeService(atividades, categorias);

        SwingUtilities.invokeLater(() ->
                new JanelaPrincipalTest(categoriaService, atividadeService, progressoService).setVisible(true)
        );
    }
}