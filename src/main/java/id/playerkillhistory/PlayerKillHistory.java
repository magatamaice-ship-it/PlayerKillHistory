
package id.playerkillhistory;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class PlayerKillHistory extends JavaPlugin {

    private static PlayerKillHistory instance;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.connect();
        } catch (SQLException exception) {
            getLogger().severe("Gagal mengaktifkan database!");
            exception.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(
                new PlayerKillListener(this),
                this
        );

        if (getCommand("riwayat") != null) {
            getCommand("riwayat").setExecutor(
                    new HistoryCommand(this)
            );
        } else {
            getLogger().severe("Command riwayat tidak ditemukan di plugin.yml!");
        }

        getLogger().info("================================");
        getLogger().info("PlayerKillHistory berhasil aktif!");
        getLogger().info("Database SQLite: Aktif");
        getLogger().info("Player Kill Detector: Aktif");
        getLogger().info("Player Head Drop: Aktif");
        getLogger().info("Kill History: Aktif");
        getLogger().info("================================");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("PlayerKillHistory dinonaktifkan.");
    }

    public static PlayerKillHistory getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
