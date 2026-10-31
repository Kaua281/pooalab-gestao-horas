package br.edu.ucsal.pooalab.gestaohoras;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.apresentacao.AplicacaoTerminal;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Path dataDirectory = Path.of(System.getProperty("user.home"), ".gestao-horas");

        try {
            SQLiteDatabase database = new SQLiteDatabase(dataDirectory.resolve("gestao-horas.db"));
            SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(database);
            AplicacaoTerminal aplicacao = new AplicacaoTerminal(
                    new CategoriaService(categorias), new Scanner(System.in));
            aplicacao.executar();
        } catch (IOException exception) {
            System.err.println("Não foi possível criar a pasta de dados: " + exception.getMessage());
            System.exit(1);
        } catch (SQLException exception) {
            System.err.println("Não foi possível iniciar o sistema: " + exception.getMessage());
            System.exit(1);
        }
    }
}
