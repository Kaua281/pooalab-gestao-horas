package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtividadeServiceEdicaoTest {
    private static final LocalDate DATA = LocalDate.of(2026, 10, 30);

    @TempDir
    Path pastaTemporaria;

    private CategoriaService categorias;
    private AtividadeService atividades;
    private ProgressoService progresso;

    @BeforeEach
    void prepararServicos() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        SQLiteCategoriaRepository repositorioCategorias = new SQLiteCategoriaRepository(banco);
        SQLiteAtividadeRepository repositorioAtividades = new SQLiteAtividadeRepository(banco);
        categorias = new CategoriaService(repositorioCategorias);
        atividades = new AtividadeService(repositorioAtividades, repositorioCategorias);
        progresso = new ProgressoService(repositorioCategorias, repositorioAtividades);
    }

    private static void assertHoras(String esperado, BigDecimal atual) {
        assertEquals(0, new BigDecimal(esperado).compareTo(atual),
                "esperado " + esperado + " mas foi " + atual);
    }

    private static void assertMensagem(String esperada, Executable acao) {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, acao);
        assertEquals(esperada, erro.getMessage());
    }

    private BigDecimal horasDaCategoria(String nome) {
        return progresso.calcularResumo().stream()
                .filter(resumo -> resumo.nomeCategoria().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Sem resumo para " + nome))
                .horasRegistradas();
    }

    @Test
    void atualizaTodosOsCamposMantendoOIdentificador() {
        long pesquisa = categorias.cadastrar("Pesquisa", BigDecimal.TEN).getId();
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("20")).getId();
        long id = atividades.cadastrar("Artigo", DATA, new BigDecimal("2"), pesquisa).getId();

        AtividadeComplementar atualizada = atividades.atualizar(
                id, "  Artigo revisado ", DATA.plusDays(1), new BigDecimal("3.5"), palestras);

        assertEquals(id, atualizada.getId());
        List<AtividadeComplementar> lista = atividades.listar();
        assertEquals(1, lista.size());
        assertEquals("Artigo revisado", lista.get(0).getTitulo());
        assertEquals(DATA.plusDays(1), lista.get(0).getData());
        assertHoras("3.5", lista.get(0).getCargaHoraria());
        assertEquals(palestras, lista.get(0).getCategoria().getId());
    }

    @Test
    void reclassificaDeSemCategoriaParaCategoriaEDeVolta() {
        long pesquisa = categorias.cadastrar("Pesquisa", BigDecimal.TEN).getId();
        long id = atividades.cadastrar("Artigo", DATA, BigDecimal.ONE, null).getId();
        assertNull(atividades.listar().get(0).getCategoria());

        atividades.atualizar(id, "Artigo", DATA, BigDecimal.ONE, pesquisa);
        assertEquals(pesquisa, atividades.listar().get(0).getCategoria().getId());

        atividades.atualizar(id, "Artigo", DATA, BigDecimal.ONE, null);
        assertNull(atividades.listar().get(0).getCategoria());
    }

    @Test
    void atualizarRecusaDadosInvalidosSemAlterarORegistro() {
        long id = atividades.cadastrar("Artigo", DATA, BigDecimal.ONE, null).getId();

        assertMensagem("O identificador da atividade é obrigatório.",
                () -> atividades.atualizar(null, "Artigo", DATA, BigDecimal.ONE, null));
        assertMensagem("Categoria não encontrada.",
                () -> atividades.atualizar(id, "Artigo", DATA, BigDecimal.ONE, 99L));
        assertMensagem("Atividade não encontrada.",
                () -> atividades.atualizar(999L, "Artigo", DATA, BigDecimal.ONE, null));
        assertMensagem("O título da atividade é obrigatório.",
                () -> atividades.atualizar(id, "   ", DATA, BigDecimal.ONE, null));
        assertMensagem("A data da atividade é obrigatória.",
                () -> atividades.atualizar(id, "Artigo", null, BigDecimal.ONE, null));
        assertMensagem("A carga horária deve ser maior que zero.",
                () -> atividades.atualizar(id, "Artigo", DATA, BigDecimal.ZERO, null));

        AtividadeComplementar intacta = atividades.listar().get(0);
        assertEquals("Artigo", intacta.getTitulo());
        assertHoras("1", intacta.getCargaHoraria());
    }

    @Test
    void removeAtividadeERecusaIdentificadorInvalido() {
        long id = atividades.cadastrar("Artigo", DATA, BigDecimal.ONE, null).getId();

        atividades.remover(id);

        assertTrue(atividades.listar().isEmpty());
        assertMensagem("O identificador da atividade é obrigatório.", () -> atividades.remover(null));
        assertMensagem("Atividade não encontrada.", () -> atividades.remover(id));
    }

    @Test
    void totaisAcompanhamEdicaoReclassificacaoERemocao() {
        long pesquisa = categorias.cadastrar("Pesquisa", BigDecimal.TEN).getId();
        long id = atividades.cadastrar("Artigo", DATA, new BigDecimal("4"), pesquisa).getId();
        atividades.cadastrar("Curso", DATA, new BigDecimal("3"), null);
        assertHoras("4", horasDaCategoria("Pesquisa"));
        assertHoras("3", progresso.horasSemCategoria());
        assertHoras("7", progresso.totalHorasRegistradas());

        atividades.atualizar(id, "Artigo", DATA, new BigDecimal("6"), pesquisa); // edição
        assertHoras("6", horasDaCategoria("Pesquisa"));
        assertHoras("9", progresso.totalHorasRegistradas());

        atividades.atualizar(id, "Artigo", DATA, new BigDecimal("6"), null); // reclassificação
        assertHoras("0", horasDaCategoria("Pesquisa"));
        assertHoras("9", progresso.horasSemCategoria());
        assertHoras("9", progresso.totalHorasRegistradas());

        atividades.remover(id); // remoção
        assertHoras("3", progresso.horasSemCategoria());
        assertHoras("3", progresso.totalHorasRegistradas());
    }
}
