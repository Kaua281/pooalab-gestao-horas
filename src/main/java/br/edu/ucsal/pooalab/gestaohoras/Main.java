package br.edu.ucsal.pooalab.gestaohoras;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.PainelInicialService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.ProgressoService;
import br.edu.ucsal.pooalab.gestaohoras.apresentacao.AplicacaoTerminal;
import br.edu.ucsal.pooalab.gestaohoras.apresentacao.PainelInicial;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Path dataDirectory = Path.of(System.getProperty("user.home"), ".gestao-horas");

        try {
            SQLiteDatabase database = new SQLiteDatabase(dataDirectory.resolve("gestao-horas.db"));
            SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(database);
            SQLiteAtividadeRepository atividades = new SQLiteAtividadeRepository(database);
            CategoriaService categoriaService = new CategoriaService(categorias);
            AtividadeService atividadeService = new AtividadeService(atividades, categorias);
            if (args.length > 0 && "--terminal".equals(args[0])) {
                new AplicacaoTerminal(categoriaService, atividadeService, new Scanner(System.in)).executar();
            } else {
                PainelInicialService consultaPainel = new PainelInicialService(
                        categoriaService, atividadeService, new ProgressoService(categorias, atividades));
                PainelInicial painel = new PainelInicial(
                        consultaPainel::carregar, consultaPainel::salvarOrdemCategorias,
                        categoriaService, atividadeService);
                SwingUtilities.invokeLater(painel::mostrar);
            }
        } catch (IOException exception) {
            System.err.println("Não foi possível criar a pasta de dados: " + exception.getMessage());
            System.exit(1);
        } catch (SQLException exception) {
            System.err.println("Não foi possível iniciar o sistema: " + exception.getMessage());
            System.exit(1);
        }
    }
}
