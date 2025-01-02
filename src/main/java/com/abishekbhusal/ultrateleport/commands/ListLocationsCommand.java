package com.abishekbhusal.ultrateleport.commands;

import com.abishekbhusal.ultrateleport.database.DatabaseManager;
import com.abishekbhusal.ultrateleport.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ListLocationsCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final MessageUtils messageUtils;

    public ListLocationsCommand(JavaPlugin plugin, DatabaseManager databaseManager, MessageUtils messageUtils) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.messageUtils = messageUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageUtils.getMessage("only_players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(plugin.getConfig().getString("commands.list_locations.permission"))) {
            messageUtils.sendMessage(player, "no_perms");
            return true;
        }

        List<String> locations = databaseManager.listLocationsAndNames(player.getUniqueId());

        if (locations.isEmpty()) {
            messageUtils.sendMessage(player, "list_locations.empty");
        } else {
            messageUtils.sendMessage(player, "list_locations.header");
            for (String entry : locations) {
                String[] parts = entry.split(" - ");
                String rawLocation = parts[0];
                String name = parts[1];

                String[] coords = rawLocation.split(",");
                String world = coords[0];
                int x = (int) Double.parseDouble(coords[1]);
                int y = (int) Double.parseDouble(coords[2]);
                int z = (int) Double.parseDouble(coords[3]);

                String formattedLocation = messageUtils.getMessage(
                        "list_locations.entry",
                        new String[]{"world", "x", "y", "z", "name"},
                        new String[]{world, String.valueOf(x), String.valueOf(y), String.valueOf(z), name}
                );
                player.sendMessage(formattedLocation);
            }
        }

        return true;
    }
}
