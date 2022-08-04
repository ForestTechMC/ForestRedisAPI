package cz.foresttech.forestredis.bungee.commands;

import cz.foresttech.forestredis.bungee.ForestRedisBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * BungeeCord Command Class for handling ForestRedisAPI commands
 */
public class BungeeForestRedisCommand extends Command {

    public BungeeForestRedisCommand() {
        super("forestredis");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if ((commandSender instanceof ProxiedPlayer)) {
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            if (p.hasPermission("forestredis.admin")) {
                return;
            }
        }

        if (args.length == 0) {
            commandSender.sendMessage("§2[ForestRedisAPI] §7You're currently running on §e" + ForestRedisBungee.getInstance().getDescription().getVersion());
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ForestRedisBungee.getInstance().load();
            commandSender.sendMessage("§2[ForestRedisAPI] §7ForestRedis successfully reloaded!");
            return;
        }

        commandSender.sendMessage("§2[ForestRedisAPI] §7You're currently running on §e" + ForestRedisBungee.getInstance().getDescription().getVersion());
    }
}
