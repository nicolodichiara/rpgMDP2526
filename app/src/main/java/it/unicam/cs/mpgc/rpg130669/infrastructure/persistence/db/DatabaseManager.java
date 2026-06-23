package it.unicam.cs.mpgc.rpg130669.infrastructure.persistence.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/**
 * Gestisce la connessione SQLite e la creazione dello schema.
 * Una sola istanza per applicazione — passala per costruttore ai repository.
 */
public class DatabaseManager {

    private static final String DB_FILENAME = "fishing_rpg.db";
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        Path dbPath = Path.of(System.getProperty("user.home"), ".fishingrpg", DB_FILENAME);
        try {
            Files.createDirectories(dbPath.getParent());
        } catch (Exception e) {
            throw new SQLException("Impossibile creare la directory del database", e);
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        connection.createStatement().execute("PRAGMA journal_mode=WAL");
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS catch_log (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_id TEXT    NOT NULL,
                    fish_id   TEXT    NOT NULL,
                    weight    INTEGER NOT NULL,
                    caught_at TEXT    DEFAULT (datetime('now'))
                )
            """);
            stmt.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_catch_player_fish
                    ON catch_log(player_id, fish_id)
            """);
        }
    }

    public Connection getConnection() { return connection; }

    public void close() throws SQLException { connection.close(); }
}