package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaServiceTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void naoPermiteCadastrarNomeDeCategoriaRepetido() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        CategoriaService servico = new CategoriaService(new SQLiteCategoriaRepository(banco));
        servico.cadastrar("Pesquisa", BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class,
                () -> servico.cadastrar("pesquisa", BigDecimal.ONE));
    }

    @Test
    void atualizaCategoriaSemPermitirNomeDuplicado() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("atualizar.db"));
        CategoriaService servico = new CategoriaService(new SQLiteCategoriaRepository(banco));
        var categoria = servico.cadastrar("Pesquisa", BigDecimal.TEN);
        servico.cadastrar("Extensão", BigDecimal.ONE);

        var atualizada = servico.atualizar(categoria.getId(), "Iniciação científica", BigDecimal.valueOf(20));

        assertEquals("Iniciação científica", atualizada.getNome());
        assertEquals(BigDecimal.valueOf(20), atualizada.getMetaHoras());
        assertThrows(IllegalArgumentException.class,
                () -> servico.atualizar(categoria.getId(), "Extensão", BigDecimal.ONE));
    }

    @Test
    void removeCategoriaMantemAtividadesSemCategoria() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("remover.db"));
        SQLiteCategoriaRepository categorias = new SQLiteCategoriaRepository(banco);
        CategoriaService servico = new CategoriaService(categorias);
        var categoria = servico.cadastrar("Pesquisa", BigDecimal.TEN);
        var atividades = new br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository(banco);
        atividades.salvar(new br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar(
                null, "Artigo", java.time.LocalDate.now(), BigDecimal.ONE, categoria));

        servico.remover(categoria.getId());

        assertEquals(1, atividades.listarTodas().size());
        assertEquals(null, atividades.listarTodas().get(0).getCategoria());
        assertEquals(0, servico.listar().size());
    }
}
