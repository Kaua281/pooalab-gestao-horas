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
        List<Categoria> categorias = repository.listarTodas();
        for (Categoria cadastrada : categorias) {
            if (cadastrada.getNome().equalsIgnoreCase(categoria.getNome())) {
                throw new IllegalArgumentException("Já existe uma categoria com esse nome.");
            }
        }
        return repository.salvar(categoria);
    }

    public List<Categoria> listar() {
        return repository.listarTodas();
    }
}
