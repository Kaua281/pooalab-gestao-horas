package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.CategoriaRepository;

import java.math.BigDecimal;
import java.util.List;

public final class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Categoria cadastrar(String nome, BigDecimal metaHoras) {
        Categoria categoria = new Categoria(null, nome, metaHoras);
        validarNomeUnico(categoria);
        return repository.salvar(categoria);
    }

    public Categoria atualizar(Long id, String nome, BigDecimal metaHoras) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador da categoria é obrigatório.");
        }
        Categoria categoria = new Categoria(id, nome, metaHoras);
        if (repository.listarTodas().stream().noneMatch(cadastrada -> cadastrada.getId().equals(id))) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }
        validarNomeUnico(categoria);
        return repository.atualizar(categoria);
    }

    public void remover(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador da categoria é obrigatório.");
        }
        if (repository.listarTodas().stream().noneMatch(categoria -> categoria.getId().equals(id))) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }
        repository.remover(id);
    }

    public List<Categoria> listar() {
        return repository.listarTodas();
    }

    private void validarNomeUnico(Categoria categoria) {
        for (Categoria cadastrada : repository.listarTodas()) {
            if (!cadastrada.getId().equals(categoria.getId())
                    && cadastrada.getNome().equalsIgnoreCase(categoria.getNome())) {
                throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
            }
        }
    }
}
