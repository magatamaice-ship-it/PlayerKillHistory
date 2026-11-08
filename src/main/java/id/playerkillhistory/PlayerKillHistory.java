
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

        getLogger().info("================================");
        getLogger().info("PlayerKillHistory berhasil aktif!");
        getLogger().info("Database SQLite: Aktif");
        getLogger().info("Player Head Drop: Dalam pengembangan");
        getLogger().info("Kill History: Dalam pengembangan");
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
