package cz.foresttech.forestredis.bungee;

import cz.foresttech.forestredis.bungee.commands.BungeeForestRedisCommand;
import cz.foresttech.forestredis.bungee.adapter.BungeeConfigAdapter;
import cz.foresttech.forestredis.bungee.events.RedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.*;
import cz.foresttech.forestredis.shared.adapter.IConfigurationAdapter;
import cz.foresttech.forestredis.shared.adapter.ILoggerAdapter;
import cz.foresttech.forestredis.shared.adapter.JUtilLoggerAdapter;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Bootstrap BungeeCord plugin to setup the {@link RedisManager} using configuration file.
 * Also provides server with reload and version command.
 */
public class ForestRedisBungee extends Plugin implements IForestRedisPlugin {

    private static ForestRedisBungee instance;

    private ILoggerAdapter loggerAdapter;

    @Override
    public void onEnable() {
        instance = this;
        loggerAdapter = new JUtilLoggerAdapter(getLogger());

        load();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeeForestRedisCommand());
    }

    @Override
    public void onDisable() {
        // Close the RedisManager
        if (RedisManager.getAPI() == null) {
            return;
        }
        RedisManager.getAPI().close();
    }

    @Override
    public void runAsync(Runnable task) {
        ProxyServer.getInstance().getScheduler().runAsync(instance, task);
    }

    @Override
    public void onMessageReceived(String channel, MessageTransferObject messageTransferObject) {
        ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageReceivedEvent(channel, messageTransferObject));
    }

    @Override
    public ILoggerAdapter logger() {
        return loggerAdapter;
    }

    @Override
    public IConfigurationAdapter getConfigAdapter() {
        BungeeConfigAdapter bungeeConfigAdapter = new BungeeConfigAdapter(this);
        bungeeConfigAdapter.setup("config");
        return bungeeConfigAdapter;
    }

    /**
     * Obtains the instance of the plugin
     *
     * @return  Instance of {@link ForestRedisBungee}
     */
    public static ForestRedisBungee getInstance() {
        return instance;
    }
}
