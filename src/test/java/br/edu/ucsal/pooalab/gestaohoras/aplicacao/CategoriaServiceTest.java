package br.edu.ucsal.pooalab.gestaohoras.aplicacao;

import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteCategoriaRepository;
import br.edu.ucsal.pooalab.gestaohoras.persistencia.SQLiteDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaServiceTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void naoPermiteCadastrarNomeDeCategoriaRepetido() throws Exception {
        SQLiteDatabase banco = new SQLiteDatabase(pastaTemporaria.resolve("teste.db"));
        CategoriaService servico = new CategoriaService(new SQLiteCategoriaRepository(banco));
        servico.cadastrar("Pesquisa", BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class,
                () -> servico.cadastrar("pesquisa", BigDecimal.ONE));
    }
}
