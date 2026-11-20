package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteDatabase {
    private final String url;

    public SQLiteDatabase(Path file) throws IOException, SQLException {
        Path absoluteFile = file.toAbsolutePath().normalize();
        Files.createDirectories(absoluteFile.getParent());
        this.url = "jdbc:sqlite:" + absoluteFile;
        initializeSchema();
    }

    Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            return connection;
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }

    private void initializeSchema() throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL COLLATE NOCASE UNIQUE,
                        meta_horas TEXT NOT NULL,
                        ordem_visualizacao INTEGER NOT NULL DEFAULT 0
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS atividades (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        titulo TEXT NOT NULL,
                        data TEXT NOT NULL,
                        carga_horaria TEXT NOT NULL,
                        categoria_id INTEGER,
                        FOREIGN KEY (categoria_id) REFERENCES categorias (id)
                    )
                    """);
        }
        migrarOrdemCategorias();
        migrarCategoriaOpcional();
    }

    private void migrarOrdemCategorias() throws SQLException {
        boolean colunaExiste = false;
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             var columns = statement.executeQuery("PRAGMA table_info(categorias)")) {
            while (columns.next()) {
                if ("ordem_visualizacao".equals(columns.getString("name"))) {
                    colunaExiste = true;
                    break;
                }
            }
        }
        if (colunaExiste) {
            return;
        }

        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            try {
                statement.execute("""
                        ALTER TABLE categorias
                        ADD COLUMN ordem_visualizacao INTEGER NOT NULL DEFAULT 0
                        """);
                List<Long> ids = new ArrayList<>();
                try (var categorias = statement.executeQuery(
                        "SELECT id FROM categorias ORDER BY nome, id")) {
                    while (categorias.next()) {
                        ids.add(categorias.getLong("id"));
                    }
                }
                try (var atualizar = connection.prepareStatement(
                        "UPDATE categorias SET ordem_visualizacao = ? WHERE id = ?")) {
                    for (int ordem = 0; ordem < ids.size(); ordem++) {
                        atualizar.setInt(1, ordem);
                        atualizar.setLong(2, ids.get(ordem));
                        atualizar.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void migrarCategoriaOpcional() throws SQLException {
        boolean categoriaObrigatoria = false;
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             var columns = statement.executeQuery("PRAGMA table_info(atividades)")) {
            while (columns.next()) {
                if ("categoria_id".equals(columns.getString("name"))) {
                    categoriaObrigatoria = columns.getInt("notnull") == 1;
                    break;
                }
            }
        }
        if (!categoriaObrigatoria) {
            return;
        }

        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            try {
                statement.execute("""
                        CREATE TABLE atividades_nova (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            titulo TEXT NOT NULL,
                            data TEXT NOT NULL,
                            carga_horaria TEXT NOT NULL,
                            categoria_id INTEGER,
                            FOREIGN KEY (categoria_id) REFERENCES categorias (id)
                        )
                        """);
                statement.execute("""
                        INSERT INTO atividades_nova (id, titulo, data, carga_horaria, categoria_id)
                        SELECT id, titulo, data, carga_horaria, categoria_id FROM atividades
                        """);
                statement.execute("DROP TABLE atividades");
                statement.execute("ALTER TABLE atividades_nova RENAME TO atividades");
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }
}
