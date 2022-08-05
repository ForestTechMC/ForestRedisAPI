package cz.foresttech.forestredis.shared.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * DTO object used for sending data across network. Supports
 * custom object serialization using {@link Gson}.
 */
public class MessageTransferObject {

    private String senderIdentifier;
    private String message;
    private long timestamp;

    /**
     * Empty constructor in case of using setters to add data later
     */
    public MessageTransferObject() {
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Constructs the instance with provided parameters
     *
     * @param senderIdentifier Identifier of sending server
     * @param message          Message content
     * @param timestamp        TimeStamp of the message
     */
    public MessageTransferObject(String senderIdentifier, String message, long timestamp) {
        this.senderIdentifier = senderIdentifier;
        this.message = message;
        this.timestamp = timestamp;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Converts current data to JSON using {@link Gson}
     *
     * @return Object serialized to JSON String
     */
    public String toJson() {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(this);
        } catch (Exception ex) {
            return null;
        }
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains {@link MessageTransferObject} from provided JSON String.
     *
     * @param json Serialized {@link MessageTransferObject} in JSON String
     * @return Deserialized {@link MessageTransferObject}
     */
    public static MessageTransferObject fromJson(String json) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(json, MessageTransferObject.class);
        } catch (Exception ex) {
            return null;
        }
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Wraps the given object to {@link MessageTransferObject} object.
     *
     * @param senderIdentifier Identifier of the sending server
     * @param objectToWrap     Object which shall be wrapped
     * @param timestamp        TimeStamp of the message
     * @return Instance of {@link MessageTransferObject} containing serialized object from input
     */
    public static MessageTransferObject wrap(String senderIdentifier, Object objectToWrap, long timestamp) {
        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(objectToWrap);

        return new MessageTransferObject(senderIdentifier, message, timestamp);
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Parses the message string to provided object type.
     *
     * @param objectType Class of the object
     * @param <T>        Type of the object
     * @return Parsed object or null if object cannot be parsed
     */
    public <T> T parseMessageObject(Class<T> objectType) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(this.message, objectType);
        } catch (Exception ex) {
            return null;
        }
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the timestamp of the message
     *
     * @return Date when message has been sent
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Changes the timestamp of the message
     *
     * @param timestamp New date when the message has been sent
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the server sender identifier
     *
     * @return  Server identifier of the sender
     */
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Obtains the content of the transferred message
     *
     * @return  Content of the message
     */
    public String getMessage() {
        return message;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Changes the server sender identifier
     *
     * @param senderIdentifier  New server sender identifier
     */
    public void setSenderIdentifier(String senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Updates the content of the transferred message
     *
     * @param message   New transferred message content
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /*----------------------------------------------------------------------------------------------------------*/

}
