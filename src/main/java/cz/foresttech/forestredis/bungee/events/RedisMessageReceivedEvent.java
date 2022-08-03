package cz.foresttech.forestredis.bungee.events;

import cz.foresttech.forestredis.bungee.ForestRedisBungee;
import cz.foresttech.forestredis.shared.events.IRedisMessageReceivedEvent;
import cz.foresttech.forestredis.shared.models.MessageTransferObject;
import cz.foresttech.forestredis.shared.RedisManager;
import net.md_5.bungee.api.plugin.Event;

public class RedisMessageReceivedEvent extends Event implements IRedisMessageReceivedEvent {

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
    public boolean isSelfSender() {
        return this.messageTransferObject.getSenderIdentifier().equals(ForestRedisBungee.getAPI().getServerIdentifier());
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

}
