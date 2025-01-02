package com.abishekbhusal.ultrateleport;

import com.abishekbhusal.ultrateleport.commands.MainCommand;
import com.abishekbhusal.ultrateleport.database.DatabaseManager;
import com.abishekbhusal.ultrateleport.utils.MessageUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UltraTeleport extends JavaPlugin {

    private DatabaseManager dbManager;
    private MessageUtils messageUtils;
    private MainCommand mainCommand;
    private JavaPlugin plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        extractMessagesFile();
        String databaseFilePath = getDataFolder().getAbsolutePath() + "/ultrateleport.db";
        dbManager = new DatabaseManager(databaseFilePath);
        messageUtils = new MessageUtils(this);
        mainCommand = new MainCommand(plugin,dbManager, messageUtils);
        plugin.getLogger().info("UltraTeleport has loaded sucessfully!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (dbManager != null) {
            dbManager.close();
        }
    }


    public void extractMessagesFile() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            try {
                plugin.saveResource("messages.yml", true);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to extract messages.yml: " + e.getMessage());
            }
        }
    }

}
