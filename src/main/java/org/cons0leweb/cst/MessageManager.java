package org.cons0leweb.cst;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MessageManager {

    private final Main plugin;
    private FileConfiguration messages;

    public MessageManager(Main plugin, String language) {
        this.plugin = plugin;
        loadMessages(language);
    }

    private void loadMessages(String language) {
        File messagesFile = new File(plugin.getDataFolder(), "messages_" + language + ".yml");


        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!messagesFile.exists()) {
            plugin.saveResource("messages_" + language + ".yml", false);
        }

        try {
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load messages file: " + messagesFile.getPath(), e);
            messages = new YamlConfiguration();
        }
    }

    public String getMessage(String key) {
        String message = messages.getString(key);
        if (message == null) {
            plugin.getLogger().warning("Message not found: " + key);
            return "Message not found: " + key;
        }
        return message;
    }

    public String getFormattedMessage(String key) {
        String message = getMessage(key);
        if (message == null) {
            return "Message not found: " + key;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}