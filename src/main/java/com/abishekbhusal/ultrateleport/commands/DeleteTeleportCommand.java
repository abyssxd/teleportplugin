package com.abishekbhusal.ultrateleport.commands;

import com.abishekbhusal.ultrateleport.database.DatabaseManager;
import com.abishekbhusal.ultrateleport.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DeleteTeleportCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final MessageUtils messageUtils;

    public DeleteTeleportCommand(JavaPlugin plugin, DatabaseManager databaseManager, MessageUtils messageUtils) {
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
        if (!player.hasPermission(plugin.getConfig().getString("commands.delete_teleport.permission"))) {
            messageUtils.sendMessage(player, "no_perms");
            return true;
        }
        if (args.length < 1) {
            messageUtils.sendMessage(player, "delete_teleport.usage");
            return true;
        }

        String name = args[0];
        List<String> locations = databaseManager.listLocationsAndNames(player.getUniqueId());
        boolean locationExists = false;

        for (String entry : locations) {
            if (entry.contains(name)) {
                locationExists = true;
                break;
            }
        }
        if (!locationExists) {
            messageUtils.sendMessage(player, "delete_teleport.not_found", "name", name);
            return true;
        }

        databaseManager.deleteData(player.getUniqueId(), name);
        messageUtils.sendMessage(player, "delete_teleport.success");

        return true;
    }
}
