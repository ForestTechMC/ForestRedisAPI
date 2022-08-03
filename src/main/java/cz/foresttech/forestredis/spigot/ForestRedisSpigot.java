package cz.foresttech.forestredis.spigot;

import cz.foresttech.forestredis.shared.*;
import cz.foresttech.forestredis.shared.config.IConfigurationAdapter;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.spigot.commands.SpigotForestRedisCommand;
import cz.foresttech.forestredis.spigot.config.SpigotConfigAdapter;
import cz.foresttech.forestredis.spigot.events.AsyncRedisMessageReceivedEvent;
import cz.foresttech.forestredis.spigot.events.RedisMessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ForestRedisSpigot extends JavaPlugin implements IForestRedisPlugin {

    private static ForestRedisSpigot instance;

    @Override
    public void onEnable() {
        instance = this;
        load();
        this.getCommand("forestredis").setExecutor(new SpigotForestRedisCommand());
    }

    @Override
    public void onDisable() {
        if (RedisManager.getAPI() == null) {
            return;
        }
        RedisManager.getAPI().close();
    }

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, task);
    }

    @Override
    public void callEvent(String channel, MessageTransferObject messageTransferObject) {
        Bukkit.getPluginManager().callEvent(new AsyncRedisMessageReceivedEvent(channel, messageTransferObject));
        Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new RedisMessageReceivedEvent(channel, messageTransferObject)));
    }

    @Override
    public Logger logger() {
        return this.getLogger();
    }

    @Override
    public IConfigurationAdapter getConfigAdapter() {
        SpigotConfigAdapter spigotConfigAdapter = new SpigotConfigAdapter();
        spigotConfigAdapter.setup("config");
        return spigotConfigAdapter;
    }

    public static ForestRedisSpigot getInstance() {
        return instance;
    }
}
