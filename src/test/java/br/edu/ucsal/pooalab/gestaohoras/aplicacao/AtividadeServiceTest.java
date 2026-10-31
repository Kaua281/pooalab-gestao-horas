package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtividadeServiceTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void exigeCategoriaExistenteParaCadastrarAtividade() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(banco);
        AtividadeService servico = new AtividadeService(
                new SQLiteAtividadeRepository(banco), categorias);

        assertThrows(IllegalArgumentException.class, () -> servico.cadastrar(
                "Artigo", LocalDate.of(2026, 10, 30), BigDecimal.ONE, 99L));

        long categoriaId = new CategoriaService(categorias)
                .cadastrar("Pesquisa", BigDecimal.TEN)
                .getId();
        assertEquals(categoriaId, servico.cadastrar(
                "Artigo", LocalDate.of(2026, 10, 30), BigDecimal.ONE, categoriaId)
                .getCategoria().getId());
    }
}
