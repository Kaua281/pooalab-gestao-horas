package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.AtividadeRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.CategoriaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class AtividadeService {
    private final AtividadeRepository atividadeRepository;
    private final CategoriaRepository categoriaRepository;

    public AtividadeService(
            AtividadeRepository atividadeRepository, CategoriaRepository categoriaRepository) {
        this.atividadeRepository = atividadeRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public AtividadeComplementar cadastrar(
            String titulo, LocalDate data, BigDecimal cargaHoraria, Long categoriaId) {
        Categoria categoria = buscarCategoria(categoriaId);
        return atividadeRepository.salvar(
                new AtividadeComplementar(null, titulo, data, cargaHoraria, categoria));
    }

    public AtividadeComplementar atualizar(
            Long id, String titulo, LocalDate data, BigDecimal cargaHoraria, Long categoriaId) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador da atividade é obrigatório.");
        }
        Categoria categoria = buscarCategoria(categoriaId);
        return atividadeRepository.salvar(
                new AtividadeComplementar(id, titulo, data, cargaHoraria, categoria));
    }

    public List<AtividadeComplementar> listar() {
        return atividadeRepository.listarTodas();
    }

    public void remover(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador da atividade é obrigatório.");
        }
        atividadeRepository.remover(id);
    }

    private Categoria buscarCategoria(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione uma categoria.");
        }
        for (Categoria categoria : categoriaRepository.listarTodas()) {
            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada.");
    }
}
