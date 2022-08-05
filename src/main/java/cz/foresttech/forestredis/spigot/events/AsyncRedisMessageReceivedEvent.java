package cz.foresttech.forestredis.spigot.events;

import cz.foresttech.forestredis.shared.events.IRedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.RedisManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Asynchronous Spigot Event class used when message was received from subscribed channel.
 */
public class AsyncRedisMessageReceivedEvent extends Event implements IRedisMessageReceivedEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Name of the channel the message came from
     */
    private final String channel;

    /**
     * MessageTransferObject containing message's data
     */
    private final MessageTransferObject messageTransferObject;

    /**
     * Constructs the instance of the Event
     *
     * @param channel   Channel in which was the message published
     * @param messageTransferObject {@link MessageTransferObject} object containing data about published message
     */
    public AsyncRedisMessageReceivedEvent(String channel, MessageTransferObject messageTransferObject) {
        super(true);
        this.channel = channel;
        this.messageTransferObject = messageTransferObject;
    }

    @Override
    public String getSenderIdentifier() {
        return this.messageTransferObject.getSenderIdentifier();
    }

    @Override
    public String getChannel() {
        return this.channel;
    }

    @Override
    public String getMessage() {
        return this.messageTransferObject.getMessage();
    }

    @Override
    public <T> T getMessageObject(Class<T> objectClass) {
        return this.messageTransferObject.parseMessageObject(objectClass);
    }

    @Override
    public boolean isSelfSender() {
        return this.messageTransferObject.getSenderIdentifier().equals(RedisManager.getAPI().getServerIdentifier());
    }

    @Override
    public long getTimeStamp() {
        return this.messageTransferObject.getTimestamp();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
