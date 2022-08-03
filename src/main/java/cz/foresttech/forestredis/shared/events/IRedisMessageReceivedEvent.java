package cz.foresttech.forestredis.shared.events;

public interface IRedisMessageReceivedEvent {

    String getSenderIdentifier();

    String getChannel();

    String getMessage();

    <T> T getMessageObject(Class<T> objectClass);

    boolean isSelfSender();

}
