package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;

import java.util.List;
import java.util.Optional;

public interface AtividadeRepository {
    AtividadeComplementar salvar(AtividadeComplementar atividade);

    List<AtividadeComplementar> listarTodas();

    Optional<AtividadeComplementar> buscarPorId(Long id);

    void remover(Long id);
}
