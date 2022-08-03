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

    public MessageTransferObject() {
    }

    public MessageTransferObject(String senderIdentifier, String message) {
        this.senderIdentifier = senderIdentifier;
        this.message = message;
    }

    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    public String getMessage() {
        return message;
    }

    public void setSenderIdentifier(String senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Converts current data to JSON using {@link Gson}
     *
     * @return
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
     * @param json
     * @return
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
     * @param senderIdentifier
     * @param objectToWrap
     * @return
     */
    public static MessageTransferObject wrap(String senderIdentifier, Object objectToWrap) {
        Gson gson = new GsonBuilder().create();
        String message = gson.toJson(objectToWrap);

        return new MessageTransferObject(senderIdentifier, message);
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

}
