package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;

class ProgressoServiceTest {
    private static final LocalDate DATA = LocalDate.of(2026, 10, 24);

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

    private ResumoCategoria resumoDe(String nome) {
        return progresso.calcularResumo().stream()
                .filter(resumo -> resumo.nomeCategoria().equals(nome))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Sem resumo para " + nome));
    }

    @Test
    void somaAsHorasDeCadaCategoriaSeparadamente() {
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("50")).getId();
        long cursos = categorias.cadastrar("Cursos", new BigDecimal("60")).getId();
        atividades.cadastrar("IA", DATA, new BigDecimal("4"), palestras);
        atividades.cadastrar("Dados", DATA, new BigDecimal("6.5"), palestras);
        atividades.cadastrar("Java", DATA, new BigDecimal("12"), cursos);

        ResumoCategoria resumoPalestras = resumoDe("Palestras");
        assertHoras("10.5", resumoPalestras.horasRegistradas());
        assertHoras("39.5", resumoPalestras.horasRestantes());
        assertHoras("21", resumoPalestras.progressoPercentual());

        ResumoCategoria resumoCursos = resumoDe("Cursos");
        assertHoras("12", resumoCursos.horasRegistradas());
        assertHoras("48", resumoCursos.horasRestantes());
        assertHoras("20", resumoCursos.progressoPercentual());
    }

    @Test
    void categoriaSemAtividadesApareceComZeroHoras() {
        categorias.cadastrar("Monitoria", new BigDecimal("30"));

        ResumoCategoria resumo = resumoDe("Monitoria");
        assertHoras("0", resumo.horasRegistradas());
        assertHoras("30", resumo.horasRestantes());
        assertHoras("0", resumo.progressoPercentual());
    }

    @Test
    void totalGeralSomaTodasAsCategorias() {
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("50")).getId();
        long cursos = categorias.cadastrar("Cursos", new BigDecimal("60")).getId();
        atividades.cadastrar("IA", DATA, new BigDecimal("4"), palestras);
        atividades.cadastrar("Java", DATA, new BigDecimal("18.5"), cursos);

        assertHoras("22.5", progresso.totalHorasRegistradas());
    }

    @Test
    void semCategoriasNemAtividadesOResumoEstaVazio() {
        List<ResumoCategoria> resumos = progresso.calcularResumo();

        assertTrue(resumos.isEmpty());
        assertHoras("0", progresso.totalHorasRegistradas());
    }

    @Test
    void totaisSaoRecalculadosAposEditarEMoverERemover() {
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("50")).getId();
        long cursos = categorias.cadastrar("Cursos", new BigDecimal("60")).getId();
        long id = atividades.cadastrar("IA", DATA, new BigDecimal("10"), palestras).getId();
        assertHoras("10", resumoDe("Palestras").horasRegistradas());

        atividades.atualizar(id, "IA", DATA, new BigDecimal("4"), palestras); // edita a carga
        assertHoras("4", resumoDe("Palestras").horasRegistradas());

        atividades.atualizar(id, "IA", DATA, new BigDecimal("4"), cursos);    // muda de categoria
        assertHoras("0", resumoDe("Palestras").horasRegistradas());
        assertHoras("4", resumoDe("Cursos").horasRegistradas());

        atividades.remover(id);                                                // remove
        assertHoras("0", resumoDe("Cursos").horasRegistradas());
        assertHoras("0", progresso.totalHorasRegistradas());
    }

    @Test
    void metaSuperadaPorVariasAtividadesLimitaProgressoERestantes() {
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("10")).getId();
        atividades.cadastrar("A", DATA, new BigDecimal("8"), palestras);
        atividades.cadastrar("B", DATA, new BigDecimal("7"), palestras);

        ResumoCategoria resumo = resumoDe("Palestras");
        assertHoras("15", resumo.horasRegistradas());
        assertHoras("0", resumo.horasRestantes());
        assertHoras("100", resumo.progressoPercentual());
    }
}
