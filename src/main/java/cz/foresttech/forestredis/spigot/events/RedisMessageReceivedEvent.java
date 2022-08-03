package cz.foresttech.forestredis.spigot.events;

import cz.foresttech.forestredis.shared.events.IRedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.RedisManager;
import cz.foresttech.forestredis.spigot.ForestRedisSpigot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisMessageReceivedEvent extends Event implements IRedisMessageReceivedEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final String channel;
    private final MessageTransferObject messageTransferObject;

    public RedisMessageReceivedEvent(String channel, MessageTransferObject messageTransferObject) {
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
    @SuppressWarnings("Make sure the recieved message can really be converted to provided type!")
    public <T> T getMessageObject(Class<T> objectClass) {
        return this.messageTransferObject.parseMessageObject(objectClass);
    }

    @Override
    public boolean isSelfSender() {
        return this.messageTransferObject.getSenderIdentifier().equals(ForestRedisSpigot.getAPI().getServerIdentifier());
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
