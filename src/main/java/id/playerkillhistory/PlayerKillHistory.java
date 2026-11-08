
package id.playerkillhistory;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerKillHistory extends JavaPlugin {

    private static PlayerKillHistory instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("================================");
        getLogger().info("PlayerKillHistory berhasil aktif!");
        getLogger().info("Player Head Drop: Dalam pengembangan");
        getLogger().info("Kill History: Dalam pengembangan");
        getLogger().info("================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerKillHistory dinonaktifkan.");
    }

    public static PlayerKillHistory getInstance() {
        return instance;
    }
}
