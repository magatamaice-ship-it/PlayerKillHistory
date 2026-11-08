
package id.playerkillhistory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerKillListener implements Listener {

    private final PlayerKillHistory plugin;

    public PlayerKillListener(PlayerKillHistory plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Hanya berlaku jika dibunuh pemain lain.
        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        // Membuat kepala dengan skin asli korban.
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(victim);
            meta.setDisplayName("Kepala " + victim.getName());
            head.setItemMeta(meta);
        }

        // Menjatuhkan kepala korban.
        event.getDrops().add(head);

        // Menyimpan data untuk database.
        String victimName = victim.getName();
        String killerName = killer.getName();
        String worldName = victim.getWorld().getName();

        var victimUUID = victim.getUniqueId();
        var killerUUID = killer.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    try {
                        plugin.getDatabaseManager().addKill(
                                victimUUID,
                                victimName,
                                killerUUID,
                                killerName,
                                worldName
                        );
                    } catch (Exception exception) {
                        plugin.getLogger().severe(
                                "Gagal menyimpan riwayat kill: "
                                        + exception.getMessage()
                        );
                    }
                }
        );

        killer.sendMessage(
                "§aKamu berhasil membunuh §e" + victimName + "§a!"
        );

        victim.sendMessage(
                "§cKamu dibunuh oleh §e" + killerName + "§c!"
        );
    }
}
