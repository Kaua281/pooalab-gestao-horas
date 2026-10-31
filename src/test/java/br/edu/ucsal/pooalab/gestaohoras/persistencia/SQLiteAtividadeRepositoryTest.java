package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
