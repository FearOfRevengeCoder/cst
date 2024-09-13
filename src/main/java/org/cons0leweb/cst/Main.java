package org.cons0leweb.cst;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private MessageManager messageManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        FileConfiguration config = getConfig();
        String language = config.getString("language", "en");

        if (language == null) {
            getLogger().warning("Language configuration is null. Using default language: en");
            language = "en";
        }

        messageManager = new MessageManager(this, language);

        registerCommand("ctitle", new CommandHandler(this));
        registerCommand("cchat", new CommandHandler(this));
        registerCommand("chelp", new CommandHandler(this));
        registerCommand("cactionbar", new CommandHandler(this));

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye!");
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    private void registerCommand(String commandName, CommandHandler handler) {
        if (getCommand(commandName) != null) {
            getCommand(commandName).setExecutor(handler);
        } else {
            getLogger().log(Level.SEVERE, "Failed to register command: " + commandName);
        }
    }
}