package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import java.util.List;

public interface CategoriaRepository {
    Categoria salvar(Categoria categoria);

    List<Categoria> listarTodas();
}
