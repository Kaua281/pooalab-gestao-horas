package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AplicacaoTerminalTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void cadastraListaEPersisteCategoriaAposReabrirBanco() throws Exception {
        Path arquivoBanco = pastaTemporaria.resolve("teste.db");
        SQLiteDatabase database = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository repositorio = new SQLiteCategoriaRepository(database);
        AplicacaoTerminal aplicacao = new AplicacaoTerminal(
                new CategoriaService(repositorio),
                new Scanner(new StringReader("1\nPesquisa\n10\n2\n0\n"))
        );

        aplicacao.executar();

        SQLiteDatabase bancoReaberto = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository repositorioReaberto = new SQLiteCategoriaRepository(bancoReaberto);
        assertEquals(1, repositorioReaberto.listarTodas().size());
        assertEquals("Pesquisa", repositorioReaberto.listarTodas().get(0).getNome());
        assertEquals(new BigDecimal("10"), repositorioReaberto.listarTodas().get(0).getMetaHoras());
    }

    @Test
    void entradaVaziaFinalizaSemCadastrarCategoria() throws Exception {
        SQLiteDatabase database = new SQLiteDatabase(pastaTemporaria.resolve("vazio.db"));
        SQLiteCategoriaRepository repositorio = new SQLiteCategoriaRepository(database);
        AplicacaoTerminal aplicacao = new AplicacaoTerminal(
                new CategoriaService(repositorio), new Scanner(new StringReader(""))
        );

        aplicacao.executar();

        assertTrue(repositorio.listarTodas().isEmpty());
    }
}
