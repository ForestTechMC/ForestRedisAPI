package cz.foresttech.forestredis.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import cz.foresttech.forestredis.bungee.ForestRedisBungee;
import net.kyori.adventure.text.Component;

/**
 * BungeeCord Command Class for handling ForestRedisAPI commands
 */
public class VelocityForestRedisCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (player.hasPermission("forestredis.admin")) {
            return;
        }


        if (args.length == 0) {
            invocation.source().sendMessage(Component.text("§2["+ForestRedisBungee.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisBungee.getInstance().getDescription().getVersion()));
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ForestRedisBungee.getInstance().load();
            invocation.source().sendMessage(Component.text("§2["+ForestRedisBungee.getInstance().getDescription().getName()+"] §7ForestRedis successfully reloaded!"));
            return;
        }

        invocation.source().sendMessage(Component.text("§2["+ForestRedisBungee.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisBungee.getInstance().getDescription().getVersion()));
    }
}
