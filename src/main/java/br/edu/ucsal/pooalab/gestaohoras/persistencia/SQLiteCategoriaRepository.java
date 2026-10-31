package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteCategoriaRepository implements CategoriaRepository {
    private final SQLiteDatabase database;

    public SQLiteCategoriaRepository(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, meta_horas) VALUES (?, ?)";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, categoria.getNome());
            statement.setString(2, categoria.getMetaHoras().toPlainString());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("O banco não retornou o identificador da categoria.");
                }
                return new Categoria(keys.getLong(1), categoria.getNome(), categoria.getMetaHoras());
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível salvar a categoria.", exception);
        }
    }

    @Override
    public List<Categoria> listarTodas() {
        String sql = "SELECT id, nome, meta_horas FROM categorias ORDER BY nome";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Categoria> categorias = new ArrayList<>();
            while (result.next()) {
                categorias.add(mapear(result));
            }
            return categorias;
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível listar as categorias.", exception);
        }
    }

    private Categoria mapear(ResultSet result) throws SQLException {
        return new Categoria(
                result.getLong("id"),
                result.getString("nome"),
                new BigDecimal(result.getString("meta_horas"))
        );
    }
}
