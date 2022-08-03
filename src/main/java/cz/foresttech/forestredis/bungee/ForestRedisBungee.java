package cz.foresttech.forestredis.bungee;

import cz.foresttech.forestredis.bungee.commands.BungeeForestRedisCommand;
import cz.foresttech.forestredis.bungee.config.BungeeConfigAdapter;
import cz.foresttech.forestredis.bungee.events.RedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.*;
import cz.foresttech.forestredis.shared.config.IConfigurationAdapter;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

public class ForestRedisBungee extends Plugin implements IForestRedisPlugin {

    private static ForestRedisBungee instance;
    private static RedisManager redisManager;

    @Override
    public void onEnable() {
        instance = this;
        redisManager = load(null);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeeForestRedisCommand());
    }

    @Override
    public void onDisable() {
        // Close the RedisManager
        if (redisManager == null) {
            return;
        }
        redisManager.close();
    }

    @Override
    public void runAsync(Runnable task) {
        ProxyServer.getInstance().getScheduler().runAsync(instance, task);
    }

    @Override
    public void callEvent(String channel, MessageTransferObject messageTransferObject) {
        ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageReceivedEvent(channel, messageTransferObject));
    }

    @Override
    public Logger logger() {
        return this.getLogger();
    }

    @Override
    public IConfigurationAdapter getConfigAdapter() {
        BungeeConfigAdapter bungeeConfigAdapter = new BungeeConfigAdapter();
        bungeeConfigAdapter.setup("config");
        return bungeeConfigAdapter;
    }

    /**
     * Gets the singleton instance of {@link RedisManager} object. This is the only
     * recommended approach to access the API methods.
     *
     * @return Singleton instance of {@link RedisManager}
     */
    public static RedisManager getAPI() {
        return redisManager;
    }

    public static ForestRedisBungee getInstance() {
        return instance;
    }
}
