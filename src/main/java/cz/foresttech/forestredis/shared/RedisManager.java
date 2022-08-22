package cz.foresttech.forestredis.shared;

import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.models.RedisConfiguration;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Class for maintaining and handling connection to Redis server.
 * It automatically fires Bungee/Spigot events corresponding to current server type.
 * <p>
 * It allows developers to subscribe channels and listen to them using generic EventHandlers.
 */
public class RedisManager {

    /**
     * Main instance
     */
    private static RedisManager api;

    /**
     * Plugin which the plugin is associated with
     */
    private final IForestRedisPlugin plugin;

    /**
     * Configuration object to store credentials
     */
    private RedisConfiguration redisConfiguration;

    /**
     * Current server's identifier. Shall be unique across your network.
     */
    private String serverIdentifier;

    /**
     * Set of subscribed channels
     */
    private final HashSet<String> channels;

    /**
     * List of current subscriptions
     */
    private final List<Subscription> subscriptions;

    /**
     * Current JedisPool object
     */
    private JedisPool jedisPool;

    /**
     * Whether the processes are in closing state
     */
    private boolean closing;

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Constructor method for creating {@link RedisManager} instance. Constructor does not subscribe to the channels,
     * it just stores the provided data for the future.
     *
     * @param plugin             Origin plugin which tries to obtain the instance
     * @param serverIdentifier   Identifier of the server (e.g. 'Bungee01'). Shall be unique to prevent bugs
     * @param redisConfiguration {@link RedisConfiguration} object with Redis server credentials
     */
    public RedisManager(IForestRedisPlugin plugin, String serverIdentifier, RedisConfiguration redisConfiguration) {
        this.plugin = plugin;
        this.closing = false;

        this.serverIdentifier = serverIdentifier;
        this.redisConfiguration = redisConfiguration;

        this.subscriptions = new ArrayList<>();

        this.channels = new HashSet<>();
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Reloads the manager while keeping already subscribed channels if set.
     *
     * @param serverIdentifier  New server identifier (if null, already using server id will be used)
     * @param redisConfiguration    New RedisConfiguration (if null, already using configuration will be used)
     * @param keepChannels  Keep already subscribed channels
     */
    public void reload(String serverIdentifier, RedisConfiguration redisConfiguration, boolean keepChannels) {
        this.close();
        this.closing = false;

        if (serverIdentifier != null) {
            this.serverIdentifier = serverIdentifier;
        }
        if (redisConfiguration != null) {
            this.redisConfiguration = redisConfiguration;
        }

        if (keepChannels) {
            String[] channels = this.channels.toArray(String[]::new);
            this.channels.clear();
            this.setup(channels);
            return;
        }

        this.setup();
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Primary setup method which establishes {@link JedisPool} from the {@link #redisConfiguration}.
     * Method then automatically subscribes to {@link #channels}.
     * <p>
     * Note! It does not return false if subscription itself was unsuccessful as the calls are asynchronous.
     *
     * @param channels Default list channels to listen on (case-sensitive), can be empty and provided afterwards
     * @return Whether the setup was successful
     * @see #subscribe(String...)
     */
    public boolean setup(String... channels) {
        // Check the RedisConfiguration existence
        if (this.redisConfiguration == null) {
            plugin.logger().warning("Cannot establish Jedis Pool! Configuration cannot be null!");
            return false;
        }

        // Build the JedisPool
        this.jedisPool = this.redisConfiguration.build();
        if (this.jedisPool == null) {
            plugin.logger().warning("Cannot establish Jedis Pool from the provided configuration!");
            return false;
        }

        this.plugin.logger().info("Jedis Pool established with server identifier '" + this.serverIdentifier + "'!");

        // If channels were provided, add them to the list and subscribe to them
        if (channels != null && channels.length > 0) {
            this.channels.addAll(Set.of(channels));

            Subscription subscription = new Subscription(this.channels.toArray(new String[0]));
            this.plugin.runAsync(subscription);
            this.subscriptions.add(subscription);
        }

        return true;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Unsubscribes the channels given.
     *
     * @param channels Names of the channels to unsubscribe (case-sensitive)
     */
    public void unsubscribe(String... channels) {
        if (this.closing) {
            return;
        }

        if (channels == null || channels.length == 0) {
            return;
        }

        try {
            for (Subscription sub : this.subscriptions) {
                sub.unsubscribe(channels);
            }
            this.plugin.logger().info("Successfully unsubscribed channels: " + Arrays.toString(channels) + "!");
        } catch (Exception ex) {
            this.plugin.logger().warning("An error occurred while unsubscribing channels: " + Arrays.toString(channels) + "!");
            return;
        }

        this.channels.removeAll(Set.of(channels));
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Subscribes to the provided channels if they're not subscribed already. Corresponding Events
     * will be thrown only if the received message is sent to subscribed channels.
     *
     * @param channels Names of the channels to subscribe (case-sensitive)
     * @return Whether at least one of the channel was successfully subscribed
     */
    public boolean subscribe(String... channels) {
        if (this.closing) {
            return false;
        }

        if (channels == null || channels.length == 0) {
            return false;
        }

        Set<String> actualChannelsToAdd = new HashSet<>();

        for (String channel : channels) {
            if (this.channels.contains(channel) && channel != null) {
                continue;
            }
            actualChannelsToAdd.add(channel);
        }

        // Check if user provided any actual channel to subscribe
        if (actualChannelsToAdd.isEmpty()) {
            return false;
        }

        this.channels.addAll(actualChannelsToAdd);

        Subscription subscription = new Subscription(actualChannelsToAdd.toArray(new String[0]));
        this.plugin.runAsync(subscription);
        subscriptions.add(subscription);

        return true;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Publishes the object to the provided channel. Also handles server identification.
     * DO NOT USE this to publish simple {@link String} message.
     *
     * @param targetChannel   Channel to be published into (case-sensitive)
     * @param objectToPublish Object to be published
     * @return Returns 'false' if the message cannot be converted to JSON or in closing state. Returns 'true' if the process was successful
     * @see #publishMessage(String, String)
     */
    public boolean publishObject(String targetChannel, Object objectToPublish) {
        MessageTransferObject messageTransferObject = MessageTransferObject.wrap(this.serverIdentifier, objectToPublish, System.currentTimeMillis());
        return this.executePublish(targetChannel, messageTransferObject);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Publishes the message to the provided channel. Also handles server identification.
     * This method is not recommended for publishing serialized objects. To publish objects:
     *
     * @param targetChannel    Channel to be published into (case-sensitive)
     * @param messageToPublish The message to be published
     * @return Returns 'false' if the message cannot be converted to JSON or in closing state. Returns 'true' if the process was successful.
     * @see #publishObject(String, Object)
     */
    public boolean publishMessage(String targetChannel, String messageToPublish) {
        MessageTransferObject messageTransferObject = new MessageTransferObject(this.serverIdentifier, messageToPublish, System.currentTimeMillis());
        return this.executePublish(targetChannel, messageTransferObject);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Internal method for publishing {@link MessageTransferObject} objects.
     *
     * @param targetChannel         Channel to be published into
     * @param messageTransferObject The {@link MessageTransferObject} object to be published
     * @return Whether the provided {@link MessageTransferObject} makes sense
     */
    private boolean executePublish(String targetChannel, MessageTransferObject messageTransferObject) {
        if (this.closing) {
            return false;
        }

        if (messageTransferObject == null) {
            return false;
        }

        String messageJson = messageTransferObject.toJson();
        if (messageJson == null) {
            return false;
        }

        this.plugin.runAsync(() -> {
            try (Jedis jedis = this.jedisPool.getResource()) {
                jedis.publish(targetChannel, messageJson);
            } catch (Exception e) {
                RedisManager.this.plugin.logger().warning("Could not send message to the Redis server!");
            }
        });

        return true;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Closes the Redis connection and unsubscribes to all channels.
     */
    public void close() {
        if (this.closing) {
            return;
        }

        this.closing = true;
        for (Subscription sub : this.subscriptions) {
            if (sub.isSubscribed()) {
                sub.unsubscribe();
            }
        }

        this.subscriptions.clear();

        if (this.jedisPool == null) {
            return;
        }

        this.jedisPool.destroy();
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the current server identifier. It is used as sender name in events.
     *
     * @return Server identifier
     */
    public String getServerIdentifier() {
        return serverIdentifier;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns whether the channel is subscribed or not.
     *
     * @param channel Channel's name to check (case-sensitive).
     * @return Whether the channel is subscribed or not
     */
    public boolean isSubscribed(String channel) {
        return this.channels.contains(channel);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns list of all subscribed channels.
     *
     * @return List of all subscribed channels (case-sensitive)
     */
    public Set<String> getSubscribedChannels() {
        return channels;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Private subscription class used for handling PubSub connection.
     *
     * @see #subscribe(String...)
     */
    private class Subscription extends JedisPubSub implements Runnable {

        private final String[] channels;

        public Subscription(String[] channels) {
            this.channels = channels;
        }

        @Override
        public void run() {
            boolean firstTry = true;

            while (!RedisManager.this.closing && !Thread.interrupted() && !RedisManager.this.jedisPool.isClosed()) {
                try (Jedis jedis = RedisManager.this.jedisPool.getResource()) {
                    if (firstTry) {
                        RedisManager.this.plugin.logger().info("Redis pubsub connection established!");
                        firstTry = false;
                    } else {
                        RedisManager.this.plugin.logger().info("Redis pubsub connection re-established!");
                    }

                    try {
                        jedis.subscribe(this, channels); // blocking call
                        RedisManager.this.plugin.logger().info("Successfully subscribed channels: " + Arrays.toString(channels) + "!");
                    } catch (Exception e) {
                        RedisManager.this.plugin.logger().warning("Could not subscribe!");
                    }
                } catch (Exception e) {
                    if (RedisManager.this.closing) {
                        return;
                    }

                    RedisManager.this.plugin.logger().warning("Redis pubsub connection dropped, trying to re-open the connection!");
                    try {
                        unsubscribe();
                    } catch (Exception ignored) {
                    }

                    // Sleep for 5 seconds to prevent massive spam in console
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        @Override
        public void onMessage(String channel, String message) {
            if (channel == null || message == null) {
                return;
            }

            MessageTransferObject messageTransferObject = MessageTransferObject.fromJson(message);
            if (messageTransferObject == null) {
                RedisManager.this.plugin.logger().warning("Cannot retrieve message object sent to channel '" + channel + "'! Message: '" + message + "'");
                return;
            }

            RedisManager.this.plugin.onMessageReceived(channel, messageTransferObject);
        }
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Initialization method for creating {@link RedisManager} main instance. This won't start any
     * connection or subscription.
     *
     * @param plugin             Origin plugin which tries to obtain the instance
     * @param serverIdentifier   Identifier of the server (e.g. 'Bungee01'). Shall be unique to prevent bugs
     * @param redisConfiguration {@link RedisConfiguration} object with Redis server credentials
     */
    public static void init(IForestRedisPlugin plugin, String serverIdentifier, RedisConfiguration redisConfiguration) {
        api = new RedisManager(plugin, serverIdentifier, redisConfiguration);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Gets the main instance of {@link RedisManager} object. This is the only
     * recommended approach to access the API methods.
     *
     * @return Main instance of {@link RedisManager}
     */
    public static RedisManager getAPI() {
        return api;
    }

    /*----------------------------------------------------------------------------------------------------------*/
}
