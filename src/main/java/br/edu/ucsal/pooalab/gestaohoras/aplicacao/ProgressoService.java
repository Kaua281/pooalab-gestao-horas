package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.AtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.CategoriaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProgressoService {
    private final CategoriaRepository categoriaRepository;
    private final AtividadeRepository atividadeRepository;

    public ProgressoService(
            CategoriaRepository categoriaRepository, AtividadeRepository atividadeRepository) {
        this.categoriaRepository = categoriaRepository;
        this.atividadeRepository = atividadeRepository;
    }

    // Um resumo por categoria cadastrada, inclusive as que ainda não têm atividades.
    public List<ResumoCategoria> calcularResumo() {
        Map<Long, BigDecimal> horasPorCategoria = new HashMap<>();
        for (AtividadeComplementar atividade : atividadeRepository.listarTodas()) {
            horasPorCategoria.merge(
                    atividade.getCategoria().getId(), atividade.getCargaHoraria(), BigDecimal::add);
        }

        List<ResumoCategoria> resumos = new ArrayList<>();
        for (Categoria categoria : categoriaRepository.listarTodas()) {
            BigDecimal horas = horasPorCategoria.getOrDefault(categoria.getId(), BigDecimal.ZERO);
            resumos.add(ResumoCategoria.calcular(categoria, horas));
        }
        return resumos;
    }

    public BigDecimal totalHorasRegistradas() {
        BigDecimal total = BigDecimal.ZERO;
        for (AtividadeComplementar atividade : atividadeRepository.listarTodas()) {
            total = total.add(atividade.getCargaHoraria());
        }
        return total;
    }
}
