package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.ResumoCategoria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record DadosPainelInicial(
        BigDecimal totalHoras,
        BigDecimal horasSemCategoria,
        int quantidadeAtividades,
        List<AtividadeComplementar> atividadesRecentes,
        List<ResumoCategoria> categorias) {

    public DadosPainelInicial {
        Objects.requireNonNull(totalHoras, "O total de horas é obrigatório.");
        Objects.requireNonNull(horasSemCategoria, "As horas sem categoria são obrigatórias.");
        atividadesRecentes = List.copyOf(atividadesRecentes);
        categorias = List.copyOf(categorias);
    }
}
