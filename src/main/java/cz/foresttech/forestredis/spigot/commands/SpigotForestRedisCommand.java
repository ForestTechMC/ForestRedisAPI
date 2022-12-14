package cz.foresttech.forestredis.spigot.commands;

import cz.foresttech.forestredis.spigot.ForestRedisSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Spigot Command Class for handling ForestRedisAPI commands
 */
public class SpigotForestRedisCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("forestredis.admin")) {
                return true;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage("§2["+ForestRedisSpigot.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisSpigot.getInstance().getDescription().getVersion());
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ForestRedisSpigot.getInstance().load();
            commandSender.sendMessage("§2["+ForestRedisSpigot.getInstance().getDescription().getName()+"] §7ForestRedis successfully reloaded!");
            return true;
        }

        commandSender.sendMessage("§2["+ForestRedisSpigot.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisSpigot.getInstance().getDescription().getVersion());
        return true;
    }
}
