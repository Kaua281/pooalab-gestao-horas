package br.edu.ucsal.pooalab.gestaohoras.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
                        meta_horas TEXT NOT NULL
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
        migrarCategoriaOpcional();
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
