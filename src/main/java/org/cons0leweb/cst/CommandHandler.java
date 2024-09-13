package org.cons0leweb.cst;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler implements CommandExecutor {

    private final Main plugin;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public CommandHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ctitle")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("no_permission"));
                return true;
            }
            handleTitleCommand(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("cchat")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("no_permission"));
                return true;
            }
            handleChatCommand(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("chelp")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("no_permission"));
                return true;
            }
            sendHelpMessage(sender);
            return true;
        } else if (command.getName().equalsIgnoreCase("cactionbar")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("no_permission"));
                return true;
            }
            handleActionBarCommand(sender, args);
            return true;
        }
        return false;
    }

    private void handleTitleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getFormattedMessage("ctitle_usage"));
            return;
        }

        List<Player> targetPlayers = getTargetPlayer(sender, args[0]);
        if (targetPlayers == null || targetPlayers.isEmpty()) return;

        String title = "";
        String subtitle = "";
        int fadeIn = 10;
        int stay = 70;
        int fadeOut = 20;

        if (args.length >= 2) {
            String[] titleSubtitle = args[1].split(";", 2);
            title = titleSubtitle.length > 0 ? titleSubtitle[0] : "";
            subtitle = titleSubtitle.length > 1 ? titleSubtitle[1] : "";
        }

        if (args.length >= 3) {
            fadeIn = parseTime(sender, args[2], "fade_in");
            if (fadeIn == -1) return;
        }

        if (args.length >= 4) {
            stay = parseTime(sender, args[3], "stay");
            if (stay == -1) return;
        }

        if (args.length >= 5) {
            fadeOut = parseTime(sender, args[4], "fade_out");
            if (fadeOut == -1) return;
        }

        title = translateColorCodes(title);
        subtitle = translateColorCodes(subtitle);

        for (Player player : targetPlayers) {
            sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
        }
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("message_sent"));
    }

    private void handleChatCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getFormattedMessage("cchat_usage"));
            return;
        }

        List<Player> targetPlayers = getTargetPlayer(sender, args[0]);
        if (targetPlayers == null || targetPlayers.isEmpty()) return;

        String message = "";
        String action = "";
        String hoverText = "";

        if (args.length >= 2) {
            message = args[1];
        }

        if (args.length >= 3) {
            action = args[2];
        }

        if (args.length >= 4) {
            hoverText = args[3];
        }

        message = translateColorCodes(message);
        hoverText = translateColorCodes(hoverText);

        for (Player player : targetPlayers) {
            sendChatMessageToPlayer(player, message, action, hoverText);
        }
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("message_sent"));
    }

    private void handleActionBarCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().getFormattedMessage("cactionbar_usage"));
            return;
        }

        List<Player> targetPlayers = getTargetPlayer(sender, args[0]);
        if (targetPlayers == null || targetPlayers.isEmpty()) return;

        String message = args.length > 1 ? String.join(" ", args).substring(args[0].length() + 1) : "";
        message = translateColorCodes(message);

        for (Player player : targetPlayers) {
            sendActionBarToPlayer(player, message);
        }
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("message_sent"));
    }

    private List<Player> getTargetPlayer(CommandSender sender, String target) {
        List<Player> targetPlayers = new ArrayList<>();

        if (target.equalsIgnoreCase("-")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("player_only_command"));
                return targetPlayers; // Вернуть пустой список
            }
            targetPlayers.add((Player) sender);
        } else if (target.equalsIgnoreCase("*")) {
            targetPlayers.addAll(plugin.getServer().getOnlinePlayers());
        } else {
            Player targetPlayer = plugin.getServer().getPlayer(target);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getMessageManager().getFormattedMessage("player_not_found"));
                return targetPlayers;
            }
            targetPlayers.add(targetPlayer);
        }

        return targetPlayers;
    }

    private int parseTime(CommandSender sender, String timeStr, String timeType) {
        try {
            return Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getFormattedMessage("invalid_" + timeType));
            return -1;
        }
    }

    private void sendTitleToPlayer(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    private void sendChatMessageToPlayer(Player player, String message, String action, String hoverText) {
        TextComponent textComponent = new TextComponent(message);

        if (!action.isEmpty()) {
            try {
                ClickEvent.Action clickAction = ClickEvent.Action.valueOf(action.toUpperCase());
                textComponent.setClickEvent(new ClickEvent(clickAction, message));
            } catch (IllegalArgumentException e) {
                player.sendMessage(plugin.getMessageManager().getFormattedMessage("invalid_action"));
                return;
            }
        }

        if (!hoverText.isEmpty()) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        }

        player.spigot().sendMessage(textComponent);
    }

    private void sendActionBarToPlayer(Player player, String message) {
        player.sendActionBar(message);
    }

    private void sendHelpMessage(CommandSender sender) {
        if (!sender.hasPermission("cst.use")) {
            sender.sendMessage(plugin.getMessageManager().getFormattedMessage("no_permission"));
            return;
        }

        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_separator"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_title"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_separator"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_ctitle_usage"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_ctitle_description"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_ctitle_example"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cchat_usage"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cchat_description"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cchat_example"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cactionbar_usage"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cactionbar_description"));
        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_cactionbar_example"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getMessageManager().getFormattedMessage("help_separator"));
    }

    private String translateColorCodes(String text) {

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}