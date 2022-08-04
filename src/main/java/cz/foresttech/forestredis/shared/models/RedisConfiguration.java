package cz.foresttech.forestredis.shared.models;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * RedisConfiguration object stores Redis server's credentials. Can be used to create JedisPool instance.
 */
public class RedisConfiguration {

    private final String hostName;
    private final int port;
    private final String username;
    private final String password;
    private final boolean ssl;

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Constructs the instance of the object
     *
     * @param hostName Hostname of the Redis server
     * @param port     Port of the Redis server
     * @param username Username used to connect to Redis server (can be null)
     * @param password Password used to connect to Redis server (can be null)
     * @param ssl      Whether to make the connection with SSL
     */
    public RedisConfiguration(String hostName, int port, String username, String password, boolean ssl) {
        this.hostName = hostName;
        this.port = port;
        this.username = username;
        this.password = password;
        this.ssl = ssl;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Creates {@link JedisPool} instance using the stored values.
     *
     * @return {@link JedisPool} object obtained using the values stored inside this object.
     */
    public JedisPool build() {
        // Hostname must exist!
        if (hostName == null) {
            return null;
        }

        try {
            if (this.username == null) {
                return new JedisPool(new JedisPoolConfig(), this.hostName, this.port, Protocol.DEFAULT_TIMEOUT, this.password, this.ssl);
            }
            return new JedisPool(new JedisPoolConfig(), this.hostName, this.port, Protocol.DEFAULT_TIMEOUT, this.username, this.password, this.ssl);
        } catch (Exception exception) {
            return null;
        }
    }

    /*----------------------------------------------------------------------------------------------------------*/

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSsl() {
        return ssl;
    }

}
