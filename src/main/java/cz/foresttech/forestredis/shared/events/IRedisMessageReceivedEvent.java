package cz.foresttech.forestredis.shared.events;

/**
 * Redis incoming message Event interface. Used for handling differences between Spigot and Bungee Event API.
 */
public interface IRedisMessageReceivedEvent {

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the sender's identifier
     *
     * @return  Sender server's name
     */
    String getSenderIdentifier();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the name of the channel message came from
     *
     * @return  Name of the incoming channel
     */
    String getChannel();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains message received
     *
     * @return  Message received
     */
    String getMessage();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains object from received message by provided type
     *
     * @param objectClass   Object class
     * @param <T>   Object type
     * @return  Parsed object (null if it cannot be parsed)
     */
    <T> T getMessageObject(Class<T> objectClass);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Checks if the sender server has the same identifier as current server
     *
     * @return  Whether the message was sent by this server
     */
    boolean isSelfSender();

}
