package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.StringReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
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
                new AtividadeService(new SQLiteAtividadeRepository(database), repositorio),
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
                new CategoriaService(repositorio),
                new AtividadeService(new SQLiteAtividadeRepository(database), repositorio),
                new Scanner(new StringReader(""))
        );

        aplicacao.executar();

        assertTrue(repositorio.listarTodas().isEmpty());
    }

    @Test
    void separaRespostaDaProximaExibicaoDoMenu() throws Exception {
        SQLiteDatabase database = new SQLiteDatabase(pastaTemporaria.resolve("separador.db"));
        SQLiteCategoriaRepository repositorio = new SQLiteCategoriaRepository(database);
        AplicacaoTerminal aplicacao = new AplicacaoTerminal(
                new CategoriaService(repositorio),
                new AtividadeService(new SQLiteAtividadeRepository(database), repositorio),
                new Scanner(new StringReader("opcao-invalida\n0\n"))
        );
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();

        try (PrintStream captura = new PrintStream(saidaCapturada, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            aplicacao.executar();
        } finally {
            System.setOut(saidaOriginal);
        }

        String separador = System.lineSeparator();
        assertTrue(saidaCapturada.toString(StandardCharsets.UTF_8)
                .contains("Opção inválida." + separador + separador + "1. Cadastrar categoria"));
    }

    @Test
    void cadastraListaEditaERemoveAtividade() throws Exception {
        SQLiteDatabase database = new SQLiteDatabase(pastaTemporaria.resolve("atividades.db"));
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(database);
        AplicacaoTerminal aplicacao = new AplicacaoTerminal(
                new CategoriaService(categorias),
                new AtividadeService(new SQLiteAtividadeRepository(database), categorias),
                new Scanner(new StringReader("""
                        1
                        Pesquisa
                        20
                        3
                        Artigo
                        2026-10-30
                        4
                        1
                        4
                        5
                        1
                        Artigo revisado
                        2026-10-31
                        5
                        1
                        4
                        6
                        1
                        0
                        """))
        );

        aplicacao.executar();

        SQLiteAtividadeRepository atividades = new SQLiteAtividadeRepository(database);
        assertTrue(atividades.listarTodas().isEmpty());
    }
}
