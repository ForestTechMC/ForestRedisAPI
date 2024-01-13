package cz.foresttech.forestredis.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import cz.foresttech.forestredis.bungee.events.RedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.IForestRedisPlugin;
import cz.foresttech.forestredis.shared.RedisManager;
import cz.foresttech.forestredis.shared.adapter.IConfigurationAdapter;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.velocity.adapter.VelocityConfigAdapter;
import cz.foresttech.forestredis.velocity.command.VelocityForestRedisCommand;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Bootstrap BungeeCord plugin to setup the {@link RedisManager} using configuration file.
 * Also provides server with reload and version command.
 */

@Plugin(id = "forestredis", name = "ForestRedis", version = "1.0.0", description = "ForestRedis plugin for Velocity", authors = {"ForestTech"})
public class ForestRedisVelocity  implements IForestRedisPlugin {

    private static ForestRedisVelocity instance;

    private ProxyServer proxy, server;
    private PluginDescription description;
    private Path folder;
    private Logger logger;

    @Inject
    public void init(ProxyServer proxy, ProxyServer server, PluginDescription description, @DataDirectory Path folder, Logger logger) {
        this.proxy = proxy;
        this.server = server;
        this.description = description;
        this.folder = folder;
        this.logger = logger;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        load();

        proxy.getCommandManager().register("forestredis", new VelocityForestRedisCommand());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        // Close the RedisManager
        if (RedisManager.getAPI() == null) {
            return;
        }
        RedisManager.getAPI().close();
    }

    @Override
    public void runAsync(Runnable task) {
        proxy.getScheduler().buildTask(this, task).schedule();
    }

    @Override
    public void onMessageReceived(String channel, MessageTransferObject messageTransferObject) {
        proxy.getEventManager().register(this, new RedisMessageReceivedEvent(channel, messageTransferObject));
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public IConfigurationAdapter getConfigAdapter() {
        VelocityConfigAdapter velocityConfigAdapter = new VelocityConfigAdapter(this.folder);
        velocityConfigAdapter.setup("config");
        return velocityConfigAdapter;
    }

    /**
     * Obtains the instance of the plugin
     *
     * @return  Instance of {@link ForestRedisVelocity}
     */
    public static ForestRedisVelocity getInstance() {
        return instance;
    }
}
