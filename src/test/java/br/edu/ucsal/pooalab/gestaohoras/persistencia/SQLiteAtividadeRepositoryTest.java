package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class SQLiteAtividadeRepositoryTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void salvaAtualizaListaERemoveAtividade() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(banco);
        Categoria categoria = categorias.salvar(new Categoria(null, "Pesquisa", BigDecimal.TEN));
        SQLiteAtividadeRepository atividades = new SQLiteAtividadeRepository(banco);

        AtividadeComplementar salva = atividades.salvar(new AtividadeComplementar(
                null, "Artigo", LocalDate.of(2026, 10, 30), BigDecimal.valueOf(4), categoria));
        AtividadeComplementar atualizada = new AtividadeComplementar(
                salva.getId(), "Artigo revisado", LocalDate.of(2026, 10, 31),
                BigDecimal.valueOf(5), categoria);
        atividades.salvar(atualizada);

        SQLiteDatabase bancoReaberto = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        SQLiteAtividadeRepository repositorioReaberto = new SQLiteAtividadeRepository(bancoReaberto);
        AtividadeComplementar recuperada = repositorioReaberto.buscarPorId(salva.getId()).orElseThrow();
        assertEquals("Artigo revisado", recuperada.getTitulo());
        assertEquals(LocalDate.of(2026, 10, 31), recuperada.getData());
        assertEquals(new BigDecimal("5"), recuperada.getCargaHoraria());
        assertEquals(categoria.getId(), recuperada.getCategoria().getId());
        assertEquals(1, repositorioReaberto.listarTodas().size());

        repositorioReaberto.remover(salva.getId());

        assertFalse(repositorioReaberto.buscarPorId(salva.getId()).isPresent());
    }

    @Test
    void migraBancoAntigoPreservandoAtividades() throws Exception {
        Path arquivoBanco = pastaTemporaria.resolve("legado.db");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + arquivoBanco);
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL COLLATE NOCASE UNIQUE,
                        meta_horas TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE atividades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        titulo TEXT NOT NULL,
                        data TEXT NOT NULL,
                        carga_horaria TEXT NOT NULL,
                        categoria_id INTEGER NOT NULL,
                        FOREIGN KEY (categoria_id) REFERENCES categorias (id)
                    )
                    """);
            statement.execute("INSERT INTO categorias (id, nome, meta_horas) VALUES (1, 'Pesquisa', '10')");
            statement.execute("""
                    INSERT INTO atividades (id, titulo, data, carga_horaria, categoria_id)
                    VALUES (1, 'Artigo', '2026-10-30', '4', 1)
                    """);
        }

        SQLiteDatabase banco = new SQLiteDatabase(arquivoBanco);
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(banco);
        SQLiteAtividadeRepository atividades = new SQLiteAtividadeRepository(banco);
        categorias.remover(1L);

        assertNull(atividades.buscarPorId(1L).orElseThrow().getCategoria());
    }
}
