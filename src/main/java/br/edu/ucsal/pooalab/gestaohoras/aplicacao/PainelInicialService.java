package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PainelInicialService {
    private static final int LIMITE_ATIVIDADES_RECENTES = 6;

    private final CategoriaService categorias;
    private final AtividadeService atividades;
    private final ProgressoService progresso;

    public PainelInicialService(
            CategoriaService categorias, AtividadeService atividades, ProgressoService progresso) {
        this.categorias = categorias;
        this.atividades = atividades;
        this.progresso = progresso;
    }

    public DadosPainelInicial carregar() {
        List<AtividadeComplementar> todasAtividades = atividades.listar();
        BigDecimal totalHoras = BigDecimal.ZERO;
        BigDecimal horasSemCategoria = BigDecimal.ZERO;

        for (AtividadeComplementar atividade : todasAtividades) {
            totalHoras = totalHoras.add(atividade.getCargaHoraria());
            if (atividade.getCategoria() == null) {
                horasSemCategoria = horasSemCategoria.add(atividade.getCargaHoraria());
            }
        }

        List<AtividadeComplementar> recentes = todasAtividades.stream()
                .sorted(Comparator.comparing(AtividadeComplementar::getData).reversed())
                .limit(LIMITE_ATIVIDADES_RECENTES)
                .toList();

        return new DadosPainelInicial(
                totalHoras,
                horasSemCategoria,
                todasAtividades.size(),
                recentes,
                progresso.calcularResumo());
    }

    public void salvarOrdemCategorias(List<Long> ids) {
        categorias.atualizarOrdem(Objects.requireNonNull(ids, "A ordem das categorias é obrigatória."));
    }
}
