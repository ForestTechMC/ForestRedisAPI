package cz.foresttech.forestredis.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import cz.foresttech.forestredis.velocity.ForestRedisVelocity;

/**
 * Velocity Command Class for handling ForestRedisAPI commands
 */
public class VelocityForestRedisCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            commandSender.sendRichMessage("§2["+ForestRedisVelocity.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisVelocity.getInstance().getDescription().getVersion());
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            ForestRedisVelocity.getInstance().load();
            commandSender.sendRichMessage("§2["+ForestRedisVelocity.getInstance().getDescription().getName()+"] §7ForestRedis successfully reloaded!");
            return;
        }

        commandSender.sendRichMessage("§2["+ForestRedisVelocity.getInstance().getDescription().getName()+"] §7You're currently running on §e" + ForestRedisVelocity.getInstance().getDescription().getVersion());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("forestredis.admin");
    }
}
