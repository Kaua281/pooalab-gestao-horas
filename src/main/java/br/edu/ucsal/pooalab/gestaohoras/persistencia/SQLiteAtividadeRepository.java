package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SQLiteAtividadeRepository implements AtividadeRepository {
    private static final String SELECT_ATIVIDADES = """
            SELECT a.id, a.titulo, a.data, a.carga_horaria,
                   c.id AS categoria_id, c.nome AS categoria_nome, c.meta_horas
            FROM atividades a
            JOIN categorias c ON c.id = a.categoria_id
            """;

    private final SQLiteDatabase database;

    public SQLiteAtividadeRepository(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public AtividadeComplementar salvar(AtividadeComplementar atividade) {
        if (atividade.getId() == null) {
            return inserir(atividade);
        }
        return atualizar(atividade);
    }

    @Override
    public List<AtividadeComplementar> listarTodas() {
        String sql = SELECT_ATIVIDADES + " ORDER BY a.data DESC, a.id DESC";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<AtividadeComplementar> atividades = new ArrayList<>();
            while (result.next()) {
                atividades.add(mapear(result));
            }
            return atividades;
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível listar as atividades.", exception);
        }
    }

    @Override
    public Optional<AtividadeComplementar> buscarPorId(Long id) {
        String sql = SELECT_ATIVIDADES + " WHERE a.id = ?";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível buscar a atividade.", exception);
        }
    }

    @Override
    public void remover(Long id) {
        String sql = "DELETE FROM atividades WHERE id = ?";
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Atividade não encontrada.");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível remover a atividade.", exception);
        }
    }

    private AtividadeComplementar inserir(AtividadeComplementar atividade) {
        String sql = """
                INSERT INTO atividades (titulo, data, carga_horaria, categoria_id)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(statement, atividade);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("O banco não retornou o identificador da atividade.");
                }
                return new AtividadeComplementar(
                        keys.getLong(1), atividade.getTitulo(), atividade.getData(),
                        atividade.getCargaHoraria(), atividade.getCategoria());
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível salvar a atividade.", exception);
        }
    }

    private AtividadeComplementar atualizar(AtividadeComplementar atividade) {
        String sql = """
                UPDATE atividades
                SET titulo = ?, data = ?, carga_horaria = ?, categoria_id = ?
                WHERE id = ?
                """;
        try (Connection connection = database.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preencher(statement, atividade);
            statement.setLong(5, atividade.getId());
            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Atividade não encontrada.");
            }
            return atividade;
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível atualizar a atividade.", exception);
        }
    }

    private void preencher(PreparedStatement statement, AtividadeComplementar atividade) throws SQLException {
        statement.setString(1, atividade.getTitulo());
        statement.setString(2, atividade.getData().toString());
        statement.setString(3, atividade.getCargaHoraria().toPlainString());
        statement.setLong(4, atividade.getCategoria().getId());
    }

    private AtividadeComplementar mapear(ResultSet result) throws SQLException {
        Categoria categoria = new Categoria(
                result.getLong("categoria_id"),
                result.getString("categoria_nome"),
                new BigDecimal(result.getString("meta_horas"))
        );
        return new AtividadeComplementar(
                result.getLong("id"),
                result.getString("titulo"),
                LocalDate.parse(result.getString("data")),
                new BigDecimal(result.getString("carga_horaria")),
                categoria
        );
    }
}
