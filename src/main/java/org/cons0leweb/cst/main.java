package org.cons0leweb.cst;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class main extends JavaPlugin {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public void onEnable() {
        getLogger().info("\uD83D\uDEC8 cst был загружен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("\uD83D\uDEC8 cst был отключен");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ctitle")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(ChatColor.RED + "⚠ У вас нет прав на использование этой команды.");
                return true;
            }
            handleTitleCommand(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("cchat")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(ChatColor.RED + "⚠ У вас нет прав на использование этой команды.");
                return true;
            }
            handleChatCommand(sender, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("chelp")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(ChatColor.RED + "⚠ У вас нет прав на использование этой команды.");
                return true;
            }
            sendHelpMessage(sender);
            return true;
        } else if (command.getName().equalsIgnoreCase("cactionbar")) {
            if (!sender.hasPermission("cst.use")) {
                sender.sendMessage(ChatColor.RED + "⚠ У вас нет прав на использование этой команды.");
                return true;
            }
            handleActionBarCommand(sender, args);
            return true;
        }
        return false;
    }

    private void handleTitleCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "⚠ Использование: /ctitle {игрок} {заголовок};{подзаголовок} [время появления] [время задержки] [время исчезновения]");
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
            fadeIn = parseTime(sender, args[2], "время появления");
            if (fadeIn == -1) return;
        }

        if (args.length >= 4) {
            stay = parseTime(sender, args[3], "время задержки");
            if (stay == -1) return;
        }

        if (args.length >= 5) {
            fadeOut = parseTime(sender, args[4], "время исчезновения");
            if (fadeOut == -1) return;
        }

        title = translateColorCodes(title);
        subtitle = translateColorCodes(subtitle);

        for (Player player : targetPlayers) {
            sendTitleToPlayer(player, title, subtitle, fadeIn, stay, fadeOut);
        }
        sender.sendMessage(ChatColor.GREEN + "✓ Сообщение отправлено игрокам.");
    }


    private void handleChatCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "⚠ Использование: /cchat {игрок} {сообщение} [действие] [подсказка]");
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
        sender.sendMessage(ChatColor.GREEN + "✓ Сообщение отправлено игрокам.");
    }

    private void handleActionBarCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "⚠ Использование: /cactionbar {игрок} {сообщение}");
            return;
        }

        List<Player> targetPlayers = getTargetPlayer(sender, args[0]);
        if (targetPlayers == null || targetPlayers.isEmpty()) return;

        String message = args.length > 1 ? String.join(" ", args).substring(args[0].length() + 1) : "";
        message = translateColorCodes(message);

        for (Player player : targetPlayers) {
            sendActionBarToPlayer(player, message);
        }
        sender.sendMessage(ChatColor.GREEN + "✓ Сообщение отправлено игрокам.");
    }

    private List<Player> getTargetPlayer(CommandSender sender, String target) {
        List<Player> targetPlayers = new ArrayList<>();

        if (target.equalsIgnoreCase("-")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("⚠ Эта команда может быть выполнена только игроком.");
                return targetPlayers; // Вернуть пустой список
            }
            targetPlayers.add((Player) sender);
        } else if (target.equalsIgnoreCase("*")) {
            targetPlayers.addAll(getServer().getOnlinePlayers());
        } else {
            Player targetPlayer = getServer().getPlayer(target);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "⚠ Игрок не найден.");
                return targetPlayers; // Вернуть пустой список
            }
            targetPlayers.add(targetPlayer);
        }

        return targetPlayers;
    }

    private int parseTime(CommandSender sender, String timeStr, String timeType) {
        try {
            return Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "⚠ Неверное " + timeType + ".");
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
                player.sendMessage(ChatColor.RED + "⚠ Неверное действие: " + action);
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
            sender.sendMessage(ChatColor.RED + "⚠ У вас нет прав на просмотр справки.");
            return;
        }

        String separator = ChatColor.of("#FFD700") + "==================================================";
        String title = ChatColor.of("#FFFF00") + "                  cst Help сделано cons0leweb";
        String commandPrefix = ChatColor.of("#00FF00") + "❖ ";
        String commandColor = ChatColor.of("#00FF00").toString();
        String descriptionColor = ChatColor.of("#808080").toString();

        sender.sendMessage(separator);
        sender.sendMessage(title);
        sender.sendMessage(separator);
        sender.sendMessage("");

        sender.sendMessage(commandPrefix + commandColor + "/ctitle {игрок} {заголовок};{подзаголовок} [время появления] [время задержки] [время исчезновения]");
        sender.sendMessage(descriptionColor + "  - Отправляет заголовок и подзаголовок указанному игроку.");
        sender.sendMessage(descriptionColor + "  - Если игрок не указан или указан как '-', сообщение отправляется вам.");
        sender.sendMessage(descriptionColor + "  - Если игрок указан как '*', сообщение отправляется всем игрокам.");
        sender.sendMessage(descriptionColor + "  - Время появления, задержки и исчезновения по умолчанию: 10, 70, 20.");
        sender.sendMessage(descriptionColor + "  - Пример: /ctitle PlayerName Привет;Это тест 10 70 20");
        sender.sendMessage("");

        sender.sendMessage(commandPrefix + commandColor + "/cchat {игрок} {сообщение} [действие] [подсказка]");
        sender.sendMessage(descriptionColor + "  - Отправляет сообщение в чат указанному игроку.");
        sender.sendMessage(descriptionColor + "  - Если игрок не указан или указан как '-', сообщение отправляется вам.");
        sender.sendMessage(descriptionColor + "  - Если игрок указан как '*', сообщение отправляется всем игрокам.");
        sender.sendMessage(descriptionColor + "  - Действие может быть 'open_url', 'run_command', 'suggest_command'.");
        sender.sendMessage(descriptionColor + "  - Подсказка отображается при наведении на сообщение.");
        sender.sendMessage(descriptionColor + "  - Пример: /cchat PlayerName Привет open_url https://example.com Подсказка");
        sender.sendMessage("");

        sender.sendMessage(commandPrefix + commandColor + "/cactionbar {игрок} {сообщение}");
        sender.sendMessage(descriptionColor + "  - Отправляет сообщение в ActionBar указанному игроку.");
        sender.sendMessage(descriptionColor + "  - Если игрок не указан или указан как '-', сообщение отправляется вам.");
        sender.sendMessage(descriptionColor + "  - Если игрок указан как '*', сообщение отправляется всем игрокам.");
        sender.sendMessage(descriptionColor + "  - Пример: /cactionbar PlayerName Привет в ActionBar!");
        sender.sendMessage("");

        sender.sendMessage(separator);
    }

    private String translateColorCodes(String text) {
        // Переводим HEX-коды
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(buffer);

        // Переводим устаревшие цвета
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}