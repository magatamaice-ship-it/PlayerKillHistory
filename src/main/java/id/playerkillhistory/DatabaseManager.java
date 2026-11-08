
package id.playerkillhistory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final PlayerKillHistory plugin;
    private Connection connection;

    public DatabaseManager(PlayerKillHistory plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        File folder = plugin.getDataFolder();

        if (!folder.exists() && !folder.mkdirs()) {
            throw new SQLException("Gagal membuat folder plugin.");
        }

        File database = new File(folder, "killhistory.db");

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + database.getAbsolutePath()
        );

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS kill_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "victim_uuid TEXT NOT NULL," +
                    "victim_name TEXT NOT NULL," +
                    "killer_uuid TEXT NOT NULL," +
                    "killer_name TEXT NOT NULL," +
                    "world TEXT NOT NULL," +
                    "timestamp BIGINT NOT NULL)"
            );

            statement.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_victim " +
                    "ON kill_history(victim_uuid)"
            );
        }

        plugin.getLogger().info("Database SQLite berhasil terhubung.");
    }

    public synchronized void addKill(
            UUID victimUUID,
            String victimName,
            UUID killerUUID,
            String killerName,
            String world
    ) throws SQLException {

        String sql = "INSERT INTO kill_history " +
                "(victim_uuid, victim_name, killer_uuid, killer_name, world, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, victimUUID.toString());
            statement.setString(2, victimName);
            statement.setString(3, killerUUID.toString());
            statement.setString(4, killerName);
            statement.setString(5, world);
            statement.setLong(6, System.currentTimeMillis());

            statement.executeUpdate();
        }
    }

    public synchronized List<KillRecord> getRecentDeaths(
            UUID victimUUID,
            int limit
    ) throws SQLException {

        List<KillRecord> records = new ArrayList<>();

        String sql = "SELECT * FROM kill_history " +
                "WHERE victim_uuid = ? " +
                "ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, victimUUID.toString());
            statement.setInt(2, Math.max(1, limit));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    records.add(new KillRecord(
                            result.getString("victim_name"),
                            result.getString("killer_name"),
                            result.getString("world"),
                            result.getLong("timestamp")
                    ));
                }
            }
        }

        return records;
    }

    public synchronized void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
            plugin.getLogger().info("Database SQLite ditutup.");
        } catch (SQLException exception) {
            plugin.getLogger().severe(
                    "Gagal menutup database: " + exception.getMessage()
            );
        }
    }

    public record KillRecord(
            String victimName,
            String killerName,
            String world,
            long timestamp
    ) {
    }
}
