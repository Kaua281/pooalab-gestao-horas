package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteAtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PainelInicialServiceTest {
    @TempDir
    Path pastaTemporaria;

    private CategoriaService categorias;
    private AtividadeService atividades;
    private PainelInicialService painel;

    @BeforeEach
    void prepararServicos() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        SQLiteCategoriaRepository repositorioCategorias = new SQLiteCategoriaRepository(banco);
        SQLiteAtividadeRepository repositorioAtividades = new SQLiteAtividadeRepository(banco);
        categorias = new CategoriaService(repositorioCategorias);
        atividades = new AtividadeService(repositorioAtividades, repositorioCategorias);
        painel = new PainelInicialService(
                categorias, atividades,
                new ProgressoService(repositorioCategorias, repositorioAtividades));
    }

    @Test
    void carregaIndicadoresRecentesEProgressoComDadosPersistidos() {
        long categoriaId = categorias.cadastrar("Cursos", new BigDecimal("20")).getId();
        atividades.cadastrar("Curso antigo", LocalDate.of(2026, 1, 1), new BigDecimal("3"), categoriaId);
        atividades.cadastrar("Atividade sem categoria", LocalDate.of(2026, 2, 1),
                new BigDecimal("2"), null);

        DadosPainelInicial dados = painel.carregar();

        assertEquals(2, dados.quantidadeAtividades());
        assertEquals(0, new BigDecimal("5").compareTo(dados.totalHoras()));
        assertEquals(0, new BigDecimal("2").compareTo(dados.horasSemCategoria()));
        assertEquals("Atividade sem categoria", dados.atividadesRecentes().get(0).getTitulo());
        assertEquals(1, dados.categorias().size());
        assertEquals(0, new BigDecimal("3").compareTo(dados.categorias().get(0).horasRegistradas()));
    }

    @Test
    void limitaListaDeAtividadesRecentesASeis() {
        for (int i = 1; i <= 8; i++) {
            atividades.cadastrar(
                    "Atividade " + i,
                    LocalDate.of(2026, 1, i),
                    BigDecimal.ONE,
                    null);
        }

        DadosPainelInicial dados = painel.carregar();

        assertEquals(8, dados.quantidadeAtividades());
        assertEquals(6, dados.atividadesRecentes().size());
        assertEquals("Atividade 8", dados.atividadesRecentes().get(0).getTitulo());
        assertEquals("Atividade 3", dados.atividadesRecentes().get(5).getTitulo());
    }

    @Test
    void mantemOrdemDasCategoriasDepoisDeRecarregarPainel() {
        long palestras = categorias.cadastrar("Palestras", new BigDecimal("10")).getId();
        long cursos = categorias.cadastrar("Cursos", new BigDecimal("20")).getId();
        painel.salvarOrdemCategorias(List.of(palestras, cursos));

        DadosPainelInicial dados = painel.carregar();

        assertEquals(List.of("Palestras", "Cursos"),
                dados.categorias().stream().map(ResumoCategoria::nomeCategoria).toList());
    }
}
