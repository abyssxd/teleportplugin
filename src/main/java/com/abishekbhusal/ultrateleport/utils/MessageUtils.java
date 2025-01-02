package com.abishekbhusal.ultrateleport.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessageUtils {
    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;

    public MessageUtils(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessagesFile();
    }

    public void loadMessagesFile() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String message = messagesConfig.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path, String placeholder, String value) {
        String message = getMessage(path);
        if (message.contains("<" + placeholder + ">")) {
            message = message.replace("<" + placeholder + ">", value);
        }
        return message;
    }

    public String getMessage(String path, String[] placeholders, String[] values) {
        String message = getMessage(path);
        if (placeholders.length != values.length) {
            return message;
        }
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("<" + placeholders[i] + ">", values[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(CommandSender sender, String path) {
        sender.sendMessage(getMessage(path));
    }

    public void sendMessage(CommandSender sender, String path, String placeholder, String value) {
        sender.sendMessage(getMessage(path, placeholder, value));
    }
}
