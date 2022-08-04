package cz.foresttech.forestredis.spigot.events;

import cz.foresttech.forestredis.shared.events.IRedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.RedisManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Spigot Event class used when message was received from subscribed channel.
 */
public class RedisMessageReceivedEvent extends Event implements IRedisMessageReceivedEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Name of the channel the message came from
     */
    private final String channel;

    /**
     * MessageTransferObject containing message's data
     */
    private final MessageTransferObject messageTransferObject;

    public RedisMessageReceivedEvent(String channel, MessageTransferObject messageTransferObject) {
        this.channel = channel;
        this.messageTransferObject = messageTransferObject;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the sender's identifier
     *
     * @return Sender server's name
     */
    @Override
    public String getSenderIdentifier() {
        return this.messageTransferObject.getSenderIdentifier();
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the name of the channel message came from
     *
     * @return Name of the incoming channel
     */
    @Override
    public String getChannel() {
        return this.channel;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains message received
     *
     * @return Message received
     */
    @Override
    public String getMessage() {
        return this.messageTransferObject.getMessage();
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains object from received message by provided type
     *
     * @param objectClass Object class
     * @param <T>         Object type
     * @return Parsed object (null if it cannot be parsed)
     */
    @Override
    @SuppressWarnings("Make sure the recieved message can really be converted to provided type!")
    public <T> T getMessageObject(Class<T> objectClass) {
        return this.messageTransferObject.parseMessageObject(objectClass);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Checks if the sender server has the same identifier as current server
     *
     * @return Whether the message was sent by this server
     */
    @Override
    public boolean isSelfSender() {
        return this.messageTransferObject.getSenderIdentifier().equals(RedisManager.getAPI().getServerIdentifier());
    }

    /*----------------------------------------------------------------------------------------------------------*/

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
