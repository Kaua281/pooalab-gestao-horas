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
        return DriverManager.getConnection(url);
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
        }
    }
}
