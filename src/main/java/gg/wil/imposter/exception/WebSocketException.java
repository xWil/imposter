package gg.wil.imposter.exception;

public abstract class WebSocketException extends RuntimeException {

    protected final WebSocketExceptionType type;
    public WebSocketExceptionType getType() { return type; }

    public WebSocketException(String message, WebSocketExceptionType type) {
        super(message);
        this.type = type;
    }

    public enum WebSocketExceptionType {
        INVALID_LOBBY_CODE,
        INVALID_PLAYER_ID;
    }
}
