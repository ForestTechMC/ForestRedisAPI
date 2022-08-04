package cz.foresttech.forestredis.shared;

import cz.foresttech.forestredis.shared.config.IConfigurationAdapter;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.models.RedisConfiguration;

import java.util.List;
import java.util.logging.Logger;

/**
 * Generic plugin interface. It is used to handle similar functions across Bungee and Spigot server engines.
 */
public interface IForestRedisPlugin {

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Runs asynchronous task
     *
     * @param task Task to be run async
     */
    void runAsync(Runnable task);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Calls the corresponding events when message was received
     *
     * @param channel
     * @param messageTransferObject
     */
    void callEvent(String channel, MessageTransferObject messageTransferObject);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns logger object
     *
     * @return
     */
    Logger logger();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the {@link IConfigurationAdapter} implementation
     *
     * @return
     */
    default IConfigurationAdapter getConfigAdapter() {
        return null;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Loading RedisManager from config method
     */
    default void load() {
        if (RedisManager.getAPI() != null) {
            RedisManager.getAPI().close();
        }

        // Load the configuration file
        IConfigurationAdapter configAdapter = this.getConfigAdapter();
        if (!configAdapter.isSetup()) {
            return;
        }

        this.logger().info("config.yml loaded successfully!");

        // Load server identifier
        String serverIdentifier = configAdapter.getString("serverIdentifier", null);
        if (serverIdentifier == null) {
            serverIdentifier = "MySuperServer1";
            this.logger().info("Cannot load 'serverIdentifier' from config.yml! Using 'MySuperServer1'!");
        }

        // Construct RedisConfiguration
        RedisConfiguration redisConfiguration = new RedisConfiguration(
                configAdapter.getString("redis.hostname", "localhost"),
                configAdapter.getInt("redis.port", 6379),
                configAdapter.getString("redis.username", null),
                configAdapter.getString("redis.password", null),
                configAdapter.getBoolean("redis.ssl", false)
        );

        // Initialize RedisManager object
        new RedisManager(this, serverIdentifier, redisConfiguration);

        // Setup the RedisManager
        List<String> channels = configAdapter.getStringList("channels");
        if (channels.isEmpty()) {
            RedisManager.getAPI().setup();
            return;
        }

        String[] channelsArray = channels.toArray(new String[0]);
        RedisManager.getAPI().setup(channelsArray);
    }
}
