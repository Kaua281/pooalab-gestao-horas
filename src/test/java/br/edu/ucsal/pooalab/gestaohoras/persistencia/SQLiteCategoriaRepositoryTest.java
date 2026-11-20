package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteCategoriaRepositoryTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void salvaCategoriaERecuperaAposReabrirBanco() throws Exception {
        Path arquivoBanco = pastaTemporaria.resolve("teste.db");
        SQLiteDatabase banco = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository repositorio = new SQLiteCategoriaRepository(banco);

        Categoria salva = repositorio.salvar(new Categoria(null, "Pesquisa", new BigDecimal("10")));

        SQLiteDatabase bancoReaberto = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository repositorioReaberto = new SQLiteCategoriaRepository(bancoReaberto);
        Categoria recuperada = repositorioReaberto.listarTodas().get(0);

        assertEquals(salva.getId(), recuperada.getId());
        assertEquals("Pesquisa", recuperada.getNome());
        assertEquals(new BigDecimal("10"), recuperada.getMetaHoras());
    }

    @Test
    void salvaOrdemDasCategoriasAposReabrirBanco() throws Exception {
        Path arquivoBanco = pastaTemporaria.resolve("ordem.db");
        SQLiteDatabase banco = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository repositorio = new SQLiteCategoriaRepository(banco);
        Categoria pesquisa = repositorio.salvar(
                new Categoria(null, "Pesquisa", new BigDecimal("10")));
        Categoria cursos = repositorio.salvar(
                new Categoria(null, "Cursos", new BigDecimal("20")));
        Categoria eventos = repositorio.salvar(
                new Categoria(null, "Eventos", new BigDecimal("30")));

        repositorio.atualizarOrdem(List.of(eventos.getId(), pesquisa.getId(), cursos.getId()));

        SQLiteCategoriaRepository repositorioReaberto =
                new SQLiteCategoriaRepository(new SQLiteDatabase(arquivoBanco));
        assertEquals(
                List.of("Eventos", "Pesquisa", "Cursos"),
                repositorioReaberto.listarTodas().stream().map(Categoria::getNome).toList());
    }

    @Test
    void adicionaOrdemAoBancoExistenteSemAlterarCategorias() throws Exception {
        Path arquivoBanco = pastaTemporaria.resolve("legado.db");
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + arquivoBanco);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL COLLATE NOCASE UNIQUE,
                        meta_horas TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    INSERT INTO categorias (nome, meta_horas) VALUES
                    ('Zoologia', '20'), ('Astronomia', '30')
                    """);
        }

        SQLiteCategoriaRepository repositorio =
                new SQLiteCategoriaRepository(new SQLiteDatabase(arquivoBanco));

        assertEquals(
                List.of("Astronomia", "Zoologia"),
                repositorio.listarTodas().stream().map(Categoria::getNome).toList());
    }
}
