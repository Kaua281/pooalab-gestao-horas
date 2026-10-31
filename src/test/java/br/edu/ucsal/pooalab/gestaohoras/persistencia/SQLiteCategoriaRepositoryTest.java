package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

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
}
