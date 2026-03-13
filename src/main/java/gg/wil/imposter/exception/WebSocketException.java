package gg.wil.imposter.exception;

public abstract class WebSocketException extends RuntimeException {

    protected final WebSocketExceptionType type;
    public WebSocketExceptionType getType() { return type; }

    public WebSocketException(String message, WebSocketExceptionType type) {
        super(message);
        this.type = type;
    }

    public enum WebSocketExceptionType {
        ALREADY_CONNECTED,
        INVALID_LOBBY_CODE,
        INVALID_PLAYER_ID,
        INVALID_SESSION_ID;
    }
}
