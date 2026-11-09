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
    public Categoria atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, meta_horas = ? WHERE id = ?";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNome());
            statement.setString(2, categoria.getMetaHoras().toPlainString());
            statement.setLong(3, categoria.getId());
            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Categoria não encontrada.");
            }
            return categoria;
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível atualizar a categoria.", exception);
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

    @Override
    public void remover(Long id) {
        try (Connection connection = database.openConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement desvincular = connection.prepareStatement(
                    "UPDATE atividades SET categoria_id = NULL WHERE categoria_id = ?");
                 PreparedStatement remover = connection.prepareStatement(
                         "DELETE FROM categorias WHERE id = ?")) {
                desvincular.setLong(1, id);
                desvincular.executeUpdate();

                remover.setLong(1, id);
                if (remover.executeUpdate() == 0) {
                    throw new IllegalArgumentException("Categoria não encontrada.");
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível remover a categoria.", exception);
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
