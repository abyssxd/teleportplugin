package com.abishekbhusal.ultrateleport.commands;

import com.abishekbhusal.ultrateleport.database.DatabaseManager;
import com.abishekbhusal.ultrateleport.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SetTeleportCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final MessageUtils messageUtils;

    public SetTeleportCommand(JavaPlugin plugin, DatabaseManager databaseManager, MessageUtils messageUtils) {
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
        if (!player.hasPermission(plugin.getConfig().getString("commands.set_teleport.permission"))) {
            messageUtils.sendMessage(player, "no_perms");
            return true;
        }
        if (args.length < 1) {
            messageUtils.sendMessage(player, "set_teleport.usage");
            return true;
        }

        String name = args[0];
        int maxTeleports = plugin.getConfig().getInt("max-teleports");
        int currentCount = databaseManager.countEntries(player.getUniqueId());
        if (currentCount >= maxTeleports) {
            messageUtils.sendMessage(player, "set_teleport.limit_reached");
            return true;
        }

        List<String> locations = databaseManager.listLocationsAndNames(player.getUniqueId());
        for (String entry : locations) {
            if (entry.contains(name)) {
                messageUtils.sendMessage(player, "set_teleport.already_exists", "name", name);
                return true;
            }
        }
        Location location = player.getLocation();
        databaseManager.insertData(player.getUniqueId(), serializeLocation(location), name);
        messageUtils.sendMessage(player, "set_teleport.success", "name", name);

        return true;
    }

    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }
}
