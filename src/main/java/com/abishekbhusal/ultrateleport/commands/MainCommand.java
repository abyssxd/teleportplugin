package com.abishekbhusal.ultrateleport.commands;

import com.abishekbhusal.ultrateleport.commands.DeleteTeleportCommand;
import com.abishekbhusal.ultrateleport.commands.ListLocationsCommand;
import com.abishekbhusal.ultrateleport.commands.SetTeleportCommand;
import com.abishekbhusal.ultrateleport.database.DatabaseManager;
import com.abishekbhusal.ultrateleport.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class MainCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final MessageUtils messageUtils;

    private final SetTeleportCommand setTeleportCommand;
    private final ListLocationsCommand listLocationsCommand;
    private final DeleteTeleportCommand deleteTeleportCommand;

    public MainCommand(JavaPlugin plugin, DatabaseManager databaseManager, MessageUtils messageUtils) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.messageUtils = messageUtils;

        this.setTeleportCommand = new SetTeleportCommand(plugin, databaseManager, messageUtils);
        this.listLocationsCommand = new ListLocationsCommand(plugin, databaseManager, messageUtils);
        this.deleteTeleportCommand = new DeleteTeleportCommand(plugin, databaseManager, messageUtils);

        plugin.getCommand(plugin.getConfig().getString("commands.teleport.command")).setExecutor(this);
        plugin.getCommand(plugin.getConfig().getString("commands.set_teleport.command")).setExecutor(setTeleportCommand);
        plugin.getCommand(plugin.getConfig().getString("commands.list_locations.command")).setExecutor(listLocationsCommand);
        plugin.getCommand(plugin.getConfig().getString("commands.delete_teleport.command")).setExecutor(deleteTeleportCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageUtils.getMessage("only_players"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        String teleportCommand = plugin.getConfig().getString("commands.teleport.command");

        if (label.equalsIgnoreCase(teleportCommand)) {
            handleTeleport(player, args);
        }

        return true;
    }

    private void handleTeleport(Player player, String[] args) {

        if (!player.hasPermission(plugin.getConfig().getString("commands.teleport.permission"))) {
            messageUtils.sendMessage(player, "no_perms");
            return;
        }
        if (args.length < 1) {
            messageUtils.sendMessage(player, "teleport.usage");
            return;
        }

        String name = args[0];
        List<String> locations = databaseManager.listLocationsAndNames(player.getUniqueId());

        for (String entry : locations) {
            if (entry.contains(name)) {
                String[] parts = entry.split(" - ");
                Location location = parseLocation(parts[0]);
                if (location != null) {
                    player.teleport(location);
                    messageUtils.sendMessage(player, "teleported");
                }
                return;
            }
        }
        messageUtils.sendMessage(player, "teleport.not_found");
    }

    private Location parseLocation(String serializedLocation) {
        try {
            String[] parts = serializedLocation.split(",");
            String world = parts[0];
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            return new Location(plugin.getServer().getWorld(world), x, y, z);
        } catch (Exception e) {
            return null;
        }
    }
}
