package gg.wil.imposter.websocket.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.exception.message.InvalidTypeException;
import gg.wil.imposter.exception.message.MissingFieldException;
import gg.wil.imposter.session.Player;

public abstract class WebSocketReceiveMessage {

    private final WebSocketReceiveMessageType type;
    public WebSocketReceiveMessageType getType() {
        return type;
    }

    private final Player from;
    public Player getFrom() {
        return from;
    }

    public WebSocketReceiveMessage(WebSocketReceiveMessageType type, Player from) {
        this.type = type;
        this.from = from;
    }

    protected final String getString(JsonObject data, String fieldName) throws MessageException {
        if (!data.has(fieldName)) throw new MissingFieldException(getMissingMessage(fieldName));
        JsonElement field = data.get(fieldName);
        if(!field.isJsonPrimitive() || !field.getAsJsonPrimitive().isString()) throw new InvalidTypeException(this.getInvalidTypeMessage(fieldName, "STRING"));
        return field.getAsString().trim();
    }

    protected final String getString(JsonObject data, String fieldName, int maxLength) {
        String string = this.getString(data, fieldName);
        if(string.length() > maxLength) throw new InvalidDataException("Field '" + fieldName + "' is too long, max length is " + maxLength + " characters");
        return string;
    }

    protected final int getInt(JsonObject data, String fieldName) throws MessageException {
        if (!data.has(fieldName)) throw new MissingFieldException(getMissingMessage(fieldName));
        JsonElement field = data.get(fieldName);
        if(!field.isJsonPrimitive() || !field.getAsJsonPrimitive().isNumber()) throw new InvalidTypeException(this.getInvalidTypeMessage(fieldName, "INT"));
        return field.getAsInt();
    }

    protected final JsonObject getJsonObject(JsonObject data, String fieldName) throws MessageException {
        if (!data.has(fieldName)) throw new MissingFieldException(getMissingMessage(fieldName));
        JsonElement field = data.get(fieldName);
        if(!field.isJsonObject()) throw new InvalidTypeException(this.getInvalidTypeMessage(fieldName, "OBJECT"));
        return field.getAsJsonObject();
    }

    private String getMissingMessage(String field) {
        return "Message is missing field: '" + field + "'";
    }

    private String getInvalidTypeMessage(String field, String type) {
        return "Field '" + field + "' is of an invalid type, should be of type " + type;
    }
}
