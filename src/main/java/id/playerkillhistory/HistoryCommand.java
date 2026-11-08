
package id.playerkillhistory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HistoryCommand implements CommandExecutor {

    private final PlayerKillHistory plugin;

    public HistoryCommand(PlayerKillHistory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command ini hanya bisa digunakan pemain.");
            return true;
        }

        if (!player.hasPermission("playerkillhistory.riwayat")) {
            player.sendMessage("§cKamu tidak memiliki izin!");
            return true;
        }

        player.sendMessage("§8§m--------------------------------");
        player.sendMessage("§6§l          RIWAYAT KEMATIAN");
        player.sendMessage("§7Memuat riwayat kematian...");

        Bukkit.getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    try {
                        List<DatabaseManager.KillRecord> records =
                                plugin.getDatabaseManager().getRecentDeaths(
                                        player.getUniqueId(),
                                        10
                                );

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (!player.isOnline()) {
                                return;
                            }

                            player.sendMessage("§8§m--------------------------------");

                            if (records.isEmpty()) {
                                player.sendMessage("§eBelum ada riwayat kematian.");
                            } else {
                                SimpleDateFormat dateFormat =
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                dateFormat.setTimeZone(
                                        TimeZone.getDefault()
                                );

                                int number = 1;

                                for (DatabaseManager.KillRecord record : records) {
                                    player.sendMessage(
                                            "§e#" + number + " §7Korban: §f"
                                                    + record.victimName()
                                    );

                                    player.sendMessage(
                                            "  §cDibunuh oleh: §f"
                                                    + record.killerName()
                                    );

                                    player.sendMessage(
                                            "  §7Dunia: §f" + record.world()
                                    );

                                    player.sendMessage(
                                            "  §7Waktu: §f"
                                                    + dateFormat.format(
                                                    new Date(record.timestamp())
                                            )
                                    );

                                    number++;
                                }
                            }

                            player.sendMessage("§8§m--------------------------------");
                        });

                    } catch (Exception exception) {
                        plugin.getLogger().severe(
                                "Gagal membaca riwayat: "
                                        + exception.getMessage()
                        );

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (player.isOnline()) {
                                player.sendMessage(
                                        "§cGagal mengambil riwayat kematian."
                                );
                            }
                        });
                    }
                }
        );

        return true;
    }
}
